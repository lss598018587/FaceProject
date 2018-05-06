package com.wm.producer.filter;

import com.alibaba.rocketmq.common.filter.MessageFilter;
import com.alibaba.rocketmq.common.message.MessageExt;

/**
 * Created by wangmiao on 2018/3/2.
 */
public class MessageFilterImpl implements MessageFilter {
    public boolean match(MessageExt msg) {
        String property = msg.getUserProperty("SequenceId");
        if(property!=null){
            int id = Integer.parseInt(property);
            if(id%3==0 ){
                return true;
            }
        }
        return false;
    }
}
