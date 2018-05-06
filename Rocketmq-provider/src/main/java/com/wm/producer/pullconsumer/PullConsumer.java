package com.wm.producer.pullconsumer;

import com.alibaba.rocketmq.client.consumer.DefaultMQPullConsumer;
import com.alibaba.rocketmq.client.consumer.DefaultMQPushConsumer;
import com.alibaba.rocketmq.client.consumer.PullResult;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import com.alibaba.rocketmq.client.exception.MQBrokerException;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.common.consumer.ConsumeFromWhere;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.alibaba.rocketmq.common.message.MessageQueue;
import com.alibaba.rocketmq.remoting.exception.RemotingException;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * pull 拉取消息
 * Created by wangmiao on 2018/2/26.
 */
public class PullConsumer {

    private static final Map<MessageQueue,Long> offseTable = new HashMap<MessageQueue, Long>();

    public static void main(String[] args) throws MQClientException {
        String name = "pull_consumer";
        DefaultMQPullConsumer consumer = new DefaultMQPullConsumer(name);
        consumer.setNamesrvAddr("h1:9876;h2:9876;h3:9876;h4:9876");
        consumer.start();
        //从TopicQuickStart这个主题去获取所有的队列（默认会有4个队列）
        Set<MessageQueue> msq = consumer.fetchSubscribeMessageQueues("PullTopic");

        System.out.println("队列长度："+msq.size());
        //便利每一个队列，进行拉取数据
        for(MessageQueue mq :msq){
            System.out.println("Consume from the queue :"+mq);

            SINGLE_MQ:while (true){
                try {
                    //从queue中获取数据，从什么位置开始拉取数据 单次最多拉取32条记录
                    PullResult pullResult = consumer.pullBlockIfNotFound(mq,null,getMessageQueueOffset(mq),32);
                    System.out.println(pullResult);
                    System.out.println(pullResult.getPullStatus());
                    System.out.println();
                    putMessageQueueOffset(mq,pullResult.getNextBeginOffset());
                    switch (pullResult.getPullStatus()){
                        case FOUND:
                            List<MessageExt> list = pullResult.getMsgFoundList();
                            for(MessageExt message :list){
                                String topic = message.getTopic();
                                String msgBody = new String (message.getBody(),"utf-8");
                                String tag = message.getTags();
                                System.out.println("topic="+topic+",msgBody="+msgBody+",tag="+tag);
                            }
                            break ;
                        case  NO_MATCHED_MSG:
                            break ;
                        case NO_NEW_MSG:
                            System.out.println("没有新的数据了");
                            break SINGLE_MQ;
                        case OFFSET_ILLEGAL:
                            break;
                        default:
                            break ;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (RemotingException e) {
                    e.printStackTrace();
                } catch (MQBrokerException e) {
                    e.printStackTrace();
                }
            }
        }
        consumer.shutdown();

        System.out.println("Consumer start");
    }

    private static void putMessageQueueOffset(MessageQueue mq,long offset){
        offseTable.put(mq,offset);
    }
    private static long getMessageQueueOffset(MessageQueue mq){
        Long offset = offseTable.get(mq);
        if(offset !=null){
            return offset;
        }
        return 0;
    }
}
