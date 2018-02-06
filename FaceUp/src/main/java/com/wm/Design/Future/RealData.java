package com.wm.Design.Future;

/**
 * Created by wangmiao on 2018/1/18.
 */
public class RealData implements Data {

    private String result;

    public RealData(String queryStr) {
        System.out.printf("根据%s去查询这是一个很耗时的操作"+queryStr);
        System.out.println();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("操作完毕获取结果");
        result = "查询结果";
    }

    @Override
    public String getRequest() {
        return result;
    }
}
