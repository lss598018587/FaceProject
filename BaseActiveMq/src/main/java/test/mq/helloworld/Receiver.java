package test.mq.helloworld;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

/**
 * Created by wangmiao on 2018/2/21.
 */
public class Receiver {
    public static void main(String[] args) throws JMSException {
        //第一步：建立ConnectionFactory工厂对象，需要填入用户名，密码，以及要连接的地址，均使用默认即可，默认端口为"tcp://localhost:61616"
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(
                "wangm",
                "123",
//                "tcp://localhost:61616"
                "failover:(tcp://h1:61616,tcp://h2:61616,tcp://h3:61616)?Randomize=false"
        );
        //第二步，通过ConnectionFactory工厂对象我们创建一个Connection连接，并且调用Connection的start方法开启连接，Connection默认是关闭的
        Connection connection = connectionFactory.createConnection();
        connection.start();
        //第三步：通过Connection对象创建session绘画（上下文环境对象），用于接受消息，参数配置1为是否启用事物，参数配置2为签收模式，一般我们设置自动签收。
        Session session = connection.createSession(Boolean.FALSE,Session.AUTO_ACKNOWLEDGE);
        //得发送通知MQ服务，才能真正得算消费了消息
//        Session session = connection.createSession(Boolean.FALSE,Session.CLIENT_ACKNOWLEDGE);


        //第四部：通过session创建Destination对象，指的是一个客户端用来制定生产消息目标和消费消息来源的对象，在PTP模式中，Destination被乘坐Queue即队列：在Pub/Sub模式，
        //Destination被称作Topic即主题，在程序中可以使用多个Queue和Topi。
        Destination destination = session.createQueue("wangmiao");
        //第五步：我们需要通过Session对象创建消息的发送和接受对象（生产者和消费者）MessageProducer/MessageConsumer.
        MessageConsumer consumer = session.createConsumer(destination);
        //第七步：最后我们使用JMS规范的TextMessage形式创建数据（通过Session对象），并用MessageProducer的send方法发送数据，
        //同理客户端使用receive方法进行接受数据，最后不要忘记关闭Connection连接
        while (true){
            TextMessage msg = (TextMessage) consumer.receive();
            //CLIENT_ACKNOWLEDGE 才手工去签收消息，另起一个（TCP线程），通知mq服务确认收到消息
//            msg.acknowledge();
            if(msg ==null) break;
            System.out.println("收到的内容为："+msg.getText());
        }
        if(connection!=null){
            connection.close();
        }
    }
}
