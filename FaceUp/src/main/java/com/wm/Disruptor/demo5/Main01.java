package com.wm.Disruptor.demo5;

import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.EventTranslator;
import com.lmax.disruptor.RingBuffer;

/**
 * @Auther: miaomiao
 * @Date: 18/8/21 15:56
 * @Description:
 */
public class Main01 {
    public static void main(String[] args) {
        RingBuffer<MyDataEvent> ringBuffer =
                RingBuffer.createSingleProducer(new Main01().new MyDataEventFactory(), 1024);
        //发布事件!!!
//        MyDataEventTranslator translator =  new Main01().new MyDataEventTranslator();

        ringBuffer.publishEvent(new Main01().new MyDataEventTranslator());

        ringBuffer.publishEvent(new Main01().new MyDataEventTranslator());
        MyDataEvent dataEvent0 = ringBuffer.get(0);
        System.out.println("Event = " + dataEvent0);
        System.out.println("Data = " + dataEvent0.getMyData());


        MyDataEvent dataEvent1 = ringBuffer.get(1);
        System.out.println("Event = " + dataEvent1);
        System.out.println("Data = " + dataEvent1.getMyData());
    }

    class MyDataEventTranslator implements EventTranslator<MyDataEvent> {

        @Override
        public void translateTo(MyDataEvent event, long l) {
            MyData data = new MyData(1,"holy shit");
            event.setMyData(data);
        }
    }

    class MyDataEventFactory implements EventFactory<MyDataEvent> {

        @Override
        public MyDataEvent newInstance() {
            return new MyDataEvent();
        }
    }

    class MyDataEvent{
        private  MyData myData;

        public MyData getMyData() {
            return myData;
        }

        public void setMyData(MyData myData) {
            this.myData = myData;
        }
    }

    class MyData{
        private int id;
        private String value;

        public MyData(int id, String value) {
            this.id = id;
            this.value = value;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return "MyData{" +
                    "id=" + id +
                    ", value='" + value + '\'' +
                    '}';
        }
    }
}
