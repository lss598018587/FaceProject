package com.wm.Design.Future;

/**
 * Created by wangmiao on 2018/1/18.
 */
public class FutureClient {

    public Data request(final String queryStr){
        //我想要一个代理对象（Data接口的实现类）先返回一个结果给客户端，然后这里自己开一个子线程继续操作
        final FutureData futureData = new FutureData();
        //启动一个子线程，去完成这个请求剩下耗时的事情
        new Thread(new Runnable() {
            @Override
            public void run() {
                //这个线程可以去加载真实的对象，然后传递给代理对象
                RealData realData = new RealData(queryStr);
                futureData.setRealData(realData);
            }
        }).start();
        return futureData;
    }
}
