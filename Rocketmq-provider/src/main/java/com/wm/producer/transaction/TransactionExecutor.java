package com.wm.producer.transaction;

import com.alibaba.rocketmq.client.producer.LocalTransactionExecuter;
import com.alibaba.rocketmq.client.producer.LocalTransactionState;
import com.alibaba.rocketmq.common.message.Message;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 执行本地事物，由客户端调用
 */
public class TransactionExecutor implements LocalTransactionExecuter {
    private AtomicInteger index = new AtomicInteger(1);

    public LocalTransactionState executeLocalTransactionBranch(Message message, Object arg) {
        System.out.println("msg="+new String(message.getBody()));

        System.out.println("arg="+arg);

        String tag = message.getTags();
        if(tag.equals("Transaction2")){
            //这里有一个分阶段提交任务的概念
            System.out.println("这里处理业务逻辑，DAO操作，失败的情况下ROLLBACK");
            return LocalTransactionState.ROLLBACK_MESSAGE;
        }

        return LocalTransactionState.COMMIT_MESSAGE;
    }

}
