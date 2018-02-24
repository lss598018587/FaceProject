package com.wm.filter;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

/**
 * Created by wangmiao on 2018/2/21.
 */
public class ProducerFilter {

    public final String FILTER_1 = "color = 'blue'";

    public final String FILTER_2 = "color = 'blue' and sal >2000";

    public final String FILTER_3 = "receiver = 'A'";

    //1 连接工厂
    private ConnectionFactory connectionFactory;

    //2 连接对象
    private Connection connection;

    //3 session 对象
    private Session session;
    //4 生产者
    private MessageProducer producer;

    public ProducerFilter() {
        try {
            //第一步：建立ConnectionFactory工厂对象，需要填入用户名，密码，以及要连接的地址，均使用默认即可，默认端口为"tcp://localhost:61616"
            this.connectionFactory = new ActiveMQConnectionFactory(
                    "wangm",
                    "123",
                    "tcp://localhost:61616"
            );
            //第二步，通过ConnectionFactory工厂对象我们创建一个Connection连接，并且调用Connection的start方法开启连接，Connection默认是关闭的
            this.connection = this.connectionFactory.createConnection();
            this.connection.start();
            //第三步：通过Connection对象创建session绘画（上下文环境对象），用于接受消息，参数配置1为是否启用事物，参数配置2为签收模式，一般我们设置自动签收。
            this.session = this.connection.createSession(Boolean.FALSE, Session.AUTO_ACKNOWLEDGE);
            this.producer = this.session.createProducer(null);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public Session getSession() {
        return this.session;
    }

    public void send1() {
        try {
            //Destination被称作Topic即主题，在程序中可以使用多个Queue和Topi。
            Destination destination = this.session.createQueue("wang1");
            MapMessage msg1 = this.session.createMapMessage();
            msg1.setString("name", "张1");
            msg1.setString("age", "23");
            msg1.setStringProperty("color", "blue");
            msg1.setIntProperty("sal", 2200);
            MapMessage msg2 = this.session.createMapMessage();
            msg2.setString("name", "张1");
            msg2.setString("age", "35");
            msg2.setStringProperty("color", "red");
            msg2.setIntProperty("sal", 1200);
            MapMessage msg3 = this.session.createMapMessage();
            msg3.setString("name", "王1");
            msg3.setString("age", "29");
            msg3.setStringProperty("color", "green");
            msg3.setIntProperty("sal", 2700);
            MapMessage msg4 = this.session.createMapMessage();
            msg4.setString("name", "王嘘1");
            msg4.setString("age", "12");
            msg4.setStringProperty("color", "blue");
            msg4.setIntProperty("sal", 2500);

            this.producer.send(destination, msg1, DeliveryMode.PERSISTENT, 2, 1000 * 60 * 2);
            this.producer.send(destination, msg2, DeliveryMode.PERSISTENT, 3, 1000 * 60 * 2);
            this.producer.send(destination, msg3, DeliveryMode.PERSISTENT, 1, 1000 * 60 * 2);
            this.producer.send(destination, msg4, DeliveryMode.PERSISTENT, 5, 1000 * 60 * 2);

        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public void send2() {
        try {
            Destination destination = this.session.createQueue("wang2");
            TextMessage msg = this.session.createTextMessage();
            this.producer.send(destination, msg, DeliveryMode.NON_PERSISTENT, 4, 1000 * 60 * 1);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        if (this.connection != null) {
            try {
                this.connection.close();
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws JMSException {
        ProducerFilter p = new ProducerFilter();
        p.send1();
        p.close();
    }
}
