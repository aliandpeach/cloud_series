package com.yk.collector.task.producer;

import com.yk.collector.task.AbstractManagerTask;
import com.yk.collector.task.storage.DataPacket;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 既是消费者又是生产者，起到转发的作用
 */
public class PacketTransmitProducer extends AbstractManagerTask {
    private LinkedBlockingQueue<DataPacket> bufferQueue = new LinkedBlockingQueue<DataPacket>();

    protected void packetNextProcess(List<DataPacket> list) {

    }
}
