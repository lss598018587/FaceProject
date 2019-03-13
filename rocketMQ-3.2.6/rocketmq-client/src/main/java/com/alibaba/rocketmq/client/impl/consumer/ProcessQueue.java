/**
 * Copyright (C) 2010-2013 Alibaba Group Holding Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.rocketmq.client.impl.consumer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.slf4j.Logger;

import com.alibaba.rocketmq.client.log.ClientLogger;
import com.alibaba.rocketmq.common.message.MessageConst;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.alibaba.rocketmq.common.protocol.body.ProcessQueueInfo;


/**
 * Queue consumption snapshot
 * 
 * @author shijia.wxr<vintage.wang@gmail.com>
 * @since 2013-7-24
 */
public class ProcessQueue {
    public final static long RebalanceLockMaxLiveTime = Long.parseLong(System.getProperty(
        "rocketmq.client.rebalance.lockMaxLiveTime", "30000"));
    public final static long RebalanceLockInterval = Long.parseLong(System.getProperty(
        "rocketmq.client.rebalance.lockInterval", "20000"));

    private final Logger log = ClientLogger.getLog();
    /**
     * 消息映射读写锁
     */
    private final ReadWriteLock lockTreeMap = new ReentrantReadWriteLock();
    // 用来保存拉取到的消息
    /**
     * 消息映射
     * key：消息队列位置
     */
    private final TreeMap<Long, MessageExt> msgTreeMap = new TreeMap<Long, MessageExt>();
    // ProcessQueue中保存的消息里的最大offset，为ConsumeQueue的offset
    private volatile long queueOffsetMax = 0L;
    // 当前保存的消息数，放进来的时候会加，移除的时候会减
    private final AtomicLong msgCount = new AtomicLong();
    // 该数据结构里的消息是否废弃
    private volatile boolean dropped = false;
    // 上次执行拉取消息的时间
    private volatile long lastPullTimestamp = System.currentTimeMillis();
    private final static long PullMaxIdleTime = Long.parseLong(System.getProperty(
        "rocketmq.client.pull.pullMaxIdleTime", "120000"));
    // 上次消费完消息后记录的时间
    private volatile long lastConsumeTimestamp = System.currentTimeMillis();
    // 消费锁，主要在顺序消费和移除ProcessQueue的时候使用
    private final Lock lockConsume = new ReentrantLock();

    private volatile boolean locked = false;
    // 上次锁定的时间
    private volatile long lastLockTimestamp = System.currentTimeMillis();
    // 是否正在消息
    private volatile boolean consuming = false;
    /**
     * 消息映射临时存储（消费中的消息）
     */
    private final TreeMap<Long, MessageExt> msgTreeMapTemp = new TreeMap<Long, MessageExt>();
    // 记录了废弃ProcessQueue的时候lockConsume的次数
    private final AtomicLong tryUnlockTimes = new AtomicLong(0);
    // 该参数为调整线程池的时候提供了数据参考
    /**
     * Broker累计消息数量
     * 计算公式 = queueMaxOffset - 新添加消息数组[n - 1].queueOffset
     * Acc = Accumulation
     * cnt = （猜测）对比度
     */
    private volatile long msgAccCnt = 0;


    /**
     *
     * 功能描述: 顺序消费的时候使用，消费之前会判断一下ProcessQueue锁定时间是否超过阈值(默认30000ms)，如果没有超时，代表还是持有锁
     *
     * @auther: miaomiao
     * @date: 19/1/21 下午1:58
     */
    public boolean isLockExpired() {
        boolean result = (System.currentTimeMillis() - this.lastLockTimestamp) > RebalanceLockMaxLiveTime;
        return result;
    }


    /**
     *
     * 功能描述: 在拉取的时候更新lastPullTimestamp的值，
     * 然后在rebalance的时候会去判断ProcessQueue已经超过一定的时间没有去拉取消息，
     * 如果是的话，则将ProcessQueue废弃(setDropped(true))且从ProcessQueue和MessageQueue的对应关系中移除该ProcessQueue
     *
     * @auther: miaomiao
     * @date: 19/1/21 下午2:00
     */
    public boolean isPullExpired() {
        //当前时间 - 最后一次拉取消息时间 > 120s ( 120s 可配置)
        boolean result = (System.currentTimeMillis() - this.lastPullTimestamp) > PullMaxIdleTime;
        return result;
    }

    /**
     *
     * 功能描述: 取回来的消息将会在proceeQueue当中存放在其中的treeMap中（整个操作为了保证线程安全，全程加锁），并且在之后统计消费的数量统计。
     *
     * 添加消息，并返回是否提交给消费者
     * 返回true，当有新消息添加成功时，
     *
     * @param msgs 消息
     * @return 是否提交给消费者
     */

    public boolean putMessage(final List<MessageExt> msgs) {
        boolean dispatchToConsume = false;
        try {
            this.lockTreeMap.writeLock().lockInterruptibly();
            try {
                // 添加消息
                int validMsgCnt = 0;
                for (MessageExt msg : msgs) {
                    MessageExt old = msgTreeMap.put(msg.getQueueOffset(), msg);
                    if (null == old) {
                        validMsgCnt++;
                        this.queueOffsetMax = msg.getQueueOffset();
                    }
                }
                msgCount.addAndGet(validMsgCnt);

                // 计算是否正在消费
                if (!msgTreeMap.isEmpty() && !this.consuming) {
                    dispatchToConsume = true;
                    this.consuming = true;
                }

                // Broker累计消息数量
                if (!msgs.isEmpty()) {
                    MessageExt messageExt = msgs.get(msgs.size() - 1);
                    String property = messageExt.getProperty(MessageConst.PROPERTY_MAX_OFFSET);
                    if (property != null) {
                        long accTotal = Long.parseLong(property) - messageExt.getQueueOffset();
                        if (accTotal > 0) {
                            this.msgAccCnt = accTotal;
                        }
                    }
                }
            }
            finally {
                this.lockTreeMap.writeLock().unlock();
            }
        }
        catch (InterruptedException e) {
            log.error("putMessage exception", e);
        }

        return dispatchToConsume;
    }


    public long getMaxSpan() {
        try {
            this.lockTreeMap.readLock().lockInterruptibly();
            try {
                if (!this.msgTreeMap.isEmpty()) {
                    return this.msgTreeMap.lastKey() - this.msgTreeMap.firstKey();
                }
            }
            finally {
                this.lockTreeMap.readLock().unlock();
            }
        }
        catch (InterruptedException e) {
            log.error("getMaxSpan exception", e);
        }

        return 0;
    }

    public long removeMessage(final List<MessageExt> msgs) {
        long result = -1;
        final long now = System.currentTimeMillis();
        try {
            this.lockTreeMap.writeLock().lockInterruptibly();
            this.lastConsumeTimestamp = now;
            try {
                if (!msgTreeMap.isEmpty()) {
                    //这里+1的原因是：如果msgTreeMap为空时，下一条获得的消息位置为queueOffsetMax+1
                    result = this.queueOffsetMax + 1;

                    // 移除消息
                    int removedCnt = 0;
                    for (MessageExt msg : msgs) {
                        MessageExt prev = msgTreeMap.remove(msg.getQueueOffset());
                        if (prev != null) {
                            removedCnt--;
                        }
                    }
                    msgCount.addAndGet(removedCnt);

                    if (!msgTreeMap.isEmpty()) {
                        result = msgTreeMap.firstKey();
                    }
                }
            }
            finally {
                this.lockTreeMap.writeLock().unlock();
            }
        }
        catch (Throwable t) {
            log.error("removeMessage exception", t);
        }

        return result;
    }


    public TreeMap<Long, MessageExt> getMsgTreeMap() {
        return msgTreeMap;
    }


    public AtomicLong getMsgCount() {
        return msgCount;
    }


    public boolean isDropped() {
        return dropped;
    }


    public void setDropped(boolean dropped) {
        this.dropped = dropped;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }


    public boolean isLocked() {
        return locked;
    }


    /**
     *  回滚消费中的消息
     *  逻辑类似于{@link #makeMessageToCosumeAgain(List)}
     */
    public void rollback() {
        try {
            this.lockTreeMap.writeLock().lockInterruptibly();
            try {
                this.msgTreeMap.putAll(this.msgTreeMapTemp);
                this.msgTreeMapTemp.clear();
            }
            finally {
                this.lockTreeMap.writeLock().unlock();
            }
        }
        catch (InterruptedException e) {
            log.error("rollback exception", e);
        }
    }


    /**
     * 提交消费中的消息已消费成功，返回消费进度
     *
     * @return 消费进度
     */
    public long commit() {
        try {
            this.lockTreeMap.writeLock().lockInterruptibly();
            try {
                // 消费进度
                Long offset = this.msgTreeMapTemp.lastKey();
                msgCount.addAndGet(this.msgTreeMapTemp.size() * (-1));
                this.msgTreeMapTemp.clear();
                // 返回消费进度
                if (offset != null) {
                    return offset + 1;
                }
            }
            finally {
                this.lockTreeMap.writeLock().unlock();
            }
        }
        catch (InterruptedException e) {
            log.error("commit exception", e);
        }

        return -1;
    }


    /**
     * 指定消息重新消费
     * 逻辑类似于{@link #rollback()}
     *
     * @param msgs 消息
     */
    public void makeMessageToCosumeAgain(List<MessageExt> msgs) {
        try {
            this.lockTreeMap.writeLock().lockInterruptibly();
            try {
                for (MessageExt msg : msgs) {
                    this.msgTreeMapTemp.remove(msg.getQueueOffset());
                    this.msgTreeMap.put(msg.getQueueOffset(), msg);
                }
            }
            finally {
                this.lockTreeMap.writeLock().unlock();
            }
        }
        catch (InterruptedException e) {
            log.error("makeMessageToCosumeAgain exception", e);
        }
    }

    /**
     * 描述：获得持有消息前N条
     * 
     * @author: miaomiao
     * @date: 19/3/13 下午5:00
     * @param  batchSize 条数
     * @return  消息
     */
    public List<MessageExt> takeMessags(final int batchSize) {
        List<MessageExt> result = new ArrayList<MessageExt>(batchSize);
        final long now = System.currentTimeMillis();
        try {
            this.lockTreeMap.writeLock().lockInterruptibly();
            this.lastConsumeTimestamp = now;
            try {
                if (!this.msgTreeMap.isEmpty()) {
                    for (int i = 0; i < batchSize; i++) {
                        Map.Entry<Long, MessageExt> entry = this.msgTreeMap.pollFirstEntry();
                        if (entry != null) {
                            result.add(entry.getValue());
                            msgTreeMapTemp.put(entry.getKey(), entry.getValue());
                        }
                        else {
                            break;
                        }
                    }
                }

                if (result.isEmpty()) {
                    consuming = false;
                }
            }
            finally {
                this.lockTreeMap.writeLock().unlock();
            }
        }
        catch (InterruptedException e) {
            log.error("take Messages exception", e);
        }

        return result;
    }


    public void clear() {
        try {
            this.lockTreeMap.writeLock().lockInterruptibly();
            try {
                this.msgTreeMap.clear();
                this.msgTreeMapTemp.clear();
                this.msgCount.set(0);
                this.queueOffsetMax = 0L;
            }
            finally {
                this.lockTreeMap.writeLock().unlock();
            }
        }
        catch (InterruptedException e) {
            log.error("rollback exception", e);
        }
    }


    public long getLastLockTimestamp() {
        return lastLockTimestamp;
    }


    public void setLastLockTimestamp(long lastLockTimestamp) {
        this.lastLockTimestamp = lastLockTimestamp;
    }


    public Lock getLockConsume() {
        return lockConsume;
    }


    public long getLastPullTimestamp() {
        return lastPullTimestamp;
    }


    public void setLastPullTimestamp(long lastPullTimestamp) {
        this.lastPullTimestamp = lastPullTimestamp;
    }


    public long getMsgAccCnt() {
        return msgAccCnt;
    }


    public void setMsgAccCnt(long msgAccCnt) {
        this.msgAccCnt = msgAccCnt;
    }


    public long getTryUnlockTimes() {
        return this.tryUnlockTimes.get();
    }


    public void incTryUnlockTimes() {
        this.tryUnlockTimes.incrementAndGet();
    }


    public void fillProcessQueueInfo(final ProcessQueueInfo info) {
        try {
            this.lockTreeMap.readLock().lockInterruptibly();

            if (!this.msgTreeMap.isEmpty()) {
                info.setCachedMsgMinOffset(this.msgTreeMap.firstKey());
                info.setCachedMsgMaxOffset(this.msgTreeMap.lastKey());
                info.setCachedMsgCount(this.msgTreeMap.size());
            }

            if (!this.msgTreeMapTemp.isEmpty()) {
                info.setTransactionMsgMinOffset(this.msgTreeMapTemp.firstKey());
                info.setTransactionMsgMaxOffset(this.msgTreeMapTemp.lastKey());
                info.setTransactionMsgCount(this.msgTreeMapTemp.size());
            }

            info.setLocked(this.locked);
            info.setTryUnlockTimes(this.tryUnlockTimes.get());
            info.setLastLockTimestamp(this.lastLockTimestamp);

            info.setDroped(this.dropped);
            info.setLastPullTimestamp(this.lastPullTimestamp);
            info.setLastConsumeTimestamp(this.lastConsumeTimestamp);
        }
        catch (Exception e) {
        }
        finally {
            this.lockTreeMap.readLock().unlock();
        }
    }


    public long getLastConsumeTimestamp() {
        return lastConsumeTimestamp;
    }


    public void setLastConsumeTimestamp(long lastConsumeTimestamp) {
        this.lastConsumeTimestamp = lastConsumeTimestamp;
    }
}
