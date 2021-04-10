package com.yk.collector.task.analysis;

import com.yk.collector.task.storage.Buffer;
import com.yk.collector.task.storage.DataPacket;

import java.net.DatagramPacket;

public class TypeProcessor implements ITypeProcessor {

    private Buffer<DataPacket> packetBuffer;

    public TypeProcessor(Buffer<DataPacket> packetBuffer) {
        this.packetBuffer = packetBuffer;
    }

    /**
     * processer
     *
     * @param packet
     */
    public void processer(DatagramPacket packet) {
        DataPacket dataPacket = new DataPacket();
        dataPacket.setDatetime(System.currentTimeMillis());
        dataPacket.setData(packet.getData());
        packetBuffer.addBuffer(dataPacket);
    }
}
