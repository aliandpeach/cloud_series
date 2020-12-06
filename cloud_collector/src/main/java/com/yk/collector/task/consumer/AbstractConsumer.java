package com.yk.collector.task.consumer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

public abstract class AbstractConsumer<T> extends Thread {
    private ReentrantLock lock = new ReentrantLock();

    private LinkedBlockingQueue<T> packetQueue;


    /**
     * 生产者消费者线程循环 将数据放入队列或者集合中
     */
    @Override
    public void run() {
        while (true) {
            // 每次只消费50条
            List<T> list = new ArrayList<>();
            for (int i = 0; i <= 50; i++) {
                T packet = packetQueue.poll();
                list.add(packet);
            }

            processPacket(list);
        }
    }

    protected abstract void processPacket(List<T> list);

    public void setPacketQueue(LinkedBlockingQueue<T> packetQueue) {
        this.packetQueue = packetQueue;
    }
}
