package com.wm.filter;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

/**
 * Created by wangmiao on 2018/2/21.
 */
public class ConsumerFilter {

    public final String FILTER_1 = "color = 'blue'";

    public final String FILTER_2 = "color = 'blue' and sal >2000";

    public final String FILTER_3 = "receiver = 'A'";

    //1 连接工厂
    private ConnectionFactory connectionFactory;

    //2 连接对象
    private  Connection connection;

    //3 session 对象
    private Session session;
    //4 消费者
    private MessageConsumer consumer;
    //5 目标地址
    private Destination destination;

    public ConsumerFilter() {
        try {
            this.connectionFactory = new ActiveMQConnectionFactory(
                    "wangm",
                    "123",
                    "tcp://localhost:61616"
            );
            this.connection = this.connectionFactory.createConnection();
            connection.start();
            this.session = this.connection.createSession(Boolean.FALSE,Session.AUTO_ACKNOWLEDGE);

            this.destination = this.session.createQueue("wang1");
            this.consumer = this.session.createConsumer(destination,FILTER_2);


        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public void receiver(){
        try {
            this.consumer.setMessageListener(new Listener());
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public class Listener implements MessageListener{
        public void onMessage(Message message) {
            try {
                if(message instanceof TextMessage){

                }
                if(message instanceof MapMessage){
                    MapMessage mapMessage = (MapMessage) message;
                    System.out.println(mapMessage.toString());
                    System.out.println(mapMessage.getString("name"));
                    System.out.println(mapMessage.getString("age"));
                }
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws JMSException {
        ConsumerFilter consumerFilter = new ConsumerFilter();
        consumerFilter.receiver();
    }
}
