package com.wm.attach3;

/**
 * @Auther: miaomiao
 * @Date: 2019-09-18 20:27
 * @Description:
 */
public class AgentTest {
    private void fun1() throws Exception {
        System.out.println("this is fun 1.");
        Thread.sleep(500);
    }

    private void fun2() throws Exception {
        System.out.println("this is fun 2.");
        Thread.sleep(500);
    }

    public static void main(String[] args) throws Exception {
        AgentTest test = new AgentTest();
        test.fun1();
        test.fun2();

    }
}
