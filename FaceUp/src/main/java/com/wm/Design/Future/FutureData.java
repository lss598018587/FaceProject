package com.wm.Design.Future;

/**
 * Created by wangmiao on 2018/1/18.
 */
public class FutureData implements Data{
    private  RealData realData;

    private boolean isReady=false;

    public RealData getRealData() {
        return realData;
    }

    public synchronized void setRealData(RealData realData) {
        //如果已经装载完毕就直接返回
        if(isReady)return;
        //如果没有装载，就进行装载真实对象
        this.realData = realData;
        isReady = true;
        //进行通知
        notify();
    }
    @Override
    public synchronized String getRequest(){
        //如果程序没有装载完毕，就一只处于阻塞状态
        while (!isReady){
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //装载好直接获取数据即可
        return this.realData.getRequest();
    }
}
