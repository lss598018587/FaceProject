package com.wm.Disruptor.demo3;

import com.lmax.disruptor.EventTranslator;
import com.lmax.disruptor.dsl.Disruptor;
import com.wm.Disruptor.demo2.Trade;

import java.util.Random;
import java.util.concurrent.CountDownLatch;

/**
 * @Author:wangmiao
 * @Desc
 * @Date Created in   2018/1/26 .
 */
public class TradePublisher implements Runnable{
    private CountDownLatch countDownLatch;
    private Disruptor<Trade> disruptor;

    private static int LOOP = 5; //模拟多少次的交易

    public TradePublisher(CountDownLatch countDownLatch, Disruptor<Trade> disruptor) {
        this.countDownLatch = countDownLatch;
        this.disruptor = disruptor;
    }

    @Override
    public void run() {
        TradeEventTranslator tradeEventTranslator = new TradeEventTranslator();
        for (int i = 0; i <LOOP ; i++) {
            disruptor.publishEvent(tradeEventTranslator);
        }
        countDownLatch.countDown();
    }

      class TradeEventTranslator implements EventTranslator<Trade>{
        private Random random = new Random();
        @Override
        public void translateTo(Trade trade, long sequence) {
            this.generateTrade(trade);
        }
        private Trade generateTrade(Trade trade){
            trade.setPrice(random.nextDouble()*9999);
            trade.setId(random.nextInt(100)+"");
            return trade;
        }
    }
}
