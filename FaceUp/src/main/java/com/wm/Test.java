package com.wm;

/**
 * Created by wangmiao on 2017/12/22.
 */
public class Test {
    public int sum(){
        return 1+2;
    }

}

class Test2 extends  Test{
    @Override
    public int sum() {
        int c= super.sum();
        System.out.println(c);
        return 0;
    }
}
class client{
    public static void main(String args[]){
        Test2 c = new Test2();
        System.out.println(c.sum());
    }
}