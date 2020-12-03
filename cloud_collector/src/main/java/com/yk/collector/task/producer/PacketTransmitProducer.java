package com.yk.collector.task.producer;

import com.yk.collector.task.AbstractProducerConsumer;
import com.yk.collector.task.storage.DataPacket;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 既是消费者又是生产者，起到转发的作用
 */
public class PacketTransmitProducer extends AbstractProducerConsumer<DataPacket> {

    private LinkedBlockingQueue<DataPacket> bufferQueue = new LinkedBlockingQueue<DataPacket>();

    public void processPacket(List<DataPacket> list) {

    }

    public void setBufferQueue(LinkedBlockingQueue<DataPacket> bufferQueue) {
        this.bufferQueue = bufferQueue;
    }
}
