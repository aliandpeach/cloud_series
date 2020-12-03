package com.yk.collector.task;

import com.yk.collector.task.storage.Buffer;
import com.yk.collector.task.storage.DataPacket;

import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public abstract class AbstractProducerConsumer<T> extends Thread {
    private ReentrantLock lock = new ReentrantLock();

    private Buffer<T> packetBuffer;


    /**
     * 生产者消费者线程循环 将数据放入队列或者集合中
     */
    @Override
    public void run() {
        while (true) {
            List<T> buffers = packetBuffer.getBuffer();

            processPacket(buffers);
        }
    }

    protected abstract void processPacket(List<T> list);

    public void setPacketBuffer(Buffer<T> packetBuffer) {
        this.packetBuffer = packetBuffer;
    }
}
