package com.yk.collector.task;

import com.yk.collector.task.storage.Buffer;
import com.yk.collector.task.storage.DataPacket;

import java.util.List;

public abstract class AbstractManagerTask extends Thread {

    private Buffer<DataPacket> packetBuffer;


    /**
     *
     */
    @Override
    public void run() {
        List<DataPacket> buffers = packetBuffer.getBuffer();

        packetNextProcess(buffers);
    }

    protected abstract void packetNextProcess(List<DataPacket> list);
}
