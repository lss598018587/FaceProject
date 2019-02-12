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
package com.alibaba.rocketmq.client.consumer.rebalance;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;

import com.alibaba.rocketmq.client.consumer.AllocateMessageQueueStrategy;
import com.alibaba.rocketmq.client.log.ClientLogger;
import com.alibaba.rocketmq.common.message.MessageQueue;


/**
 * Average Hashing queue algorithm
 *
 * @author fuchong<yubao.fyb@alibaba-inc.com>
 * @author manhong.yqd<manhong.yqd@taobao.com>
 * @since 2013-7-24
 */
public class AllocateMessageQueueAveragely implements AllocateMessageQueueStrategy {
    private final Logger log = ClientLogger.getLog();


    @Override
    public String getName() {
        return "AVG";
    }


    /**
     *
     * 功能描述: 以默认的平均分配方式为例子来看他的allocat()方法。
     *
     * 固定消息队列长度为4。
     *
     *              Consumer * 2 可以整除   	Consumer * 3 不可整除	    Consumer * 5 无法都分配
     * 消息队列[0]	    Consumer[0]	                Consumer[0]	            Consumer[0]
     * 消息队列[1]	    Consumer[0]	                Consumer[0]	            Consumer[1]
     * 消息队列[2]	    Consumer[1]	                Consumer[1]	            Consumer[2]
     * 消息队列[3]	    Consumer[1]	                Consumer[2]	            Consumer[3]
     *
     *
     * @auther: miaomiao
     * @date: 19/1/19 下午4:48
     */
    @Override
    public List<MessageQueue> allocate(String consumerGroup, String currentCID, List<MessageQueue> mqAll,
                                       List<String> cidAll) {
        if (currentCID == null || currentCID.length() < 1) {
            throw new IllegalArgumentException("currentCID is empty");
        }
        if (mqAll == null || mqAll.isEmpty()) {
            throw new IllegalArgumentException("mqAll is null or mqAll empty");
        }
        if (cidAll == null || cidAll.isEmpty()) {
            throw new IllegalArgumentException("cidAll is null or cidAll empty");
        }

        List<MessageQueue> result = new ArrayList<MessageQueue>();
        if (!cidAll.contains(currentCID)) {
            log.info("[BUG] ConsumerGroup: {} The consumerId: {} not in cidAll: {}", consumerGroup, currentCID,cidAll);
            return result;
        }
        // 平均分配
        int index = cidAll.indexOf(currentCID);// 第几个consumer。
        int mod = mqAll.size() % cidAll.size();// 余数，即多少消息队列无法平均分配。
        int averageSize =
                mqAll.size() <= cidAll.size() ? 1 : (mod > 0 && index < mod ? mqAll.size() / cidAll.size()
                        + 1 : mqAll.size() / cidAll.size());
        int startIndex = (mod > 0 && index < mod) ? index * averageSize : index * averageSize + mod; // 有余数的情况下，[0, mod) 平分余数，即每consumer多分配一个节点；第index开始，跳过前mod余数。
        int range = Math.min(averageSize, mqAll.size() - startIndex); // 分配队列数量。之所以要Math.min()的原因是，mqAll.size() <= cidAll.size()，部分consumer分配不到消息队列。
        for (int i = 0; i < range; i++) {
            result.add(mqAll.get((startIndex + i) % mqAll.size()));
        }
        return result;
    }
}
