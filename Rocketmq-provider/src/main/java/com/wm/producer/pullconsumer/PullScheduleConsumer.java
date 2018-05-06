package com.wm.producer.pullconsumer;

import com.alibaba.rocketmq.client.consumer.*;
import com.alibaba.rocketmq.client.exception.MQBrokerException;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.alibaba.rocketmq.common.message.MessageQueue;
import com.alibaba.rocketmq.common.protocol.heartbeat.MessageModel;
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
public class PullScheduleConsumer {


    public static void main(String[] args) throws MQClientException {
        String name = "schedule_consumer";
        final MQPullConsumerScheduleService scheduleService = new MQPullConsumerScheduleService(name);
        scheduleService.getDefaultMQPullConsumer().setNamesrvAddr("h1:9876;h2:9876;h3:9876;h4:9876");
        scheduleService.setMessageModel(MessageModel.CLUSTERING);
        scheduleService.registerPullTaskCallback("PullTopic", new PullTaskCallback() {
            public void doPullTask(MessageQueue mq, PullTaskContext context) {
                MQPullConsumer consumer = context.getPullConsumer();
                try {
                    //获取从哪里拉取
                    long offset =  consumer.fetchConsumeOffset(mq,false);
                    System.out.println("offset》"+offset);
                    if(offset<=0) offset=0;
                    PullResult pullResult = consumer.pull(mq,"*",offset,32);
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
                        case OFFSET_ILLEGAL:
                            break;
                        default:
                            break ;
                    }

                    //存储offset，客户端每5s会定时刷新到broker
                    consumer.updateConsumeOffset(mq,pullResult.getNextBeginOffset());

                    //设置再过多少秒后重新拉取
                    context.setPullNextDelayTimeMillis(2000);
                } catch ( Exception e) {
                    e.printStackTrace();
                }
            }
        });


        scheduleService.start();

    }

}
