package com.yk.collector.task.consumer;

import com.yk.collector.task.AbstractProducerConsumer;
import com.yk.collector.task.storage.DataPacket;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

public class ParseConsumer extends AbstractProducerConsumer<DataPacket> {

    private LinkedBlockingQueue<DataPacket> bufferPacket = new LinkedBlockingQueue<>();

    public void processPacket(List<DataPacket> list) {

    }

    public void setBufferPacket(LinkedBlockingQueue<DataPacket> bufferPacket) {
        this.bufferPacket = bufferPacket;
    }
}
