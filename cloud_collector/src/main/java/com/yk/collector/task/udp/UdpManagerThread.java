package com.yk.collector.task.udp;

import com.yk.collector.task.analysis.ITypeProcessor;
import com.yk.collector.task.analysis.TypeProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.DatagramSocketImpl;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * UDP报文接收线程
 */
public class UdpManagerThread extends Thread {
    private static final Logger logger = LoggerFactory.getLogger("base");

    private static final int REVEICE_BUFFER_SIZE = 1024 * 4096;

    private static final int PACKET_BUFFER_SIZE = 4096;

    private boolean stop = false;

    private List<ITypeProcessor> typeProcessors = new CopyOnWriteArrayList<ITypeProcessor>();

    public UdpManagerThread(ITypeProcessor typeProcessor) {
        this.typeProcessors.add(typeProcessor);
    }

    /**
     * 接收报文
     */
    @Override
    public void run() {
        try {
            InetAddress addr = InetAddress.getLocalHost();
            DatagramSocket udp = new DatagramSocket(9017, addr);
            udp.setReceiveBufferSize(REVEICE_BUFFER_SIZE);
            while (!stop) {
                byte[] buf = new byte[PACKET_BUFFER_SIZE];
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                udp.receive(packet);

                InetAddress sendIP = packet.getAddress();
                int sendPort = packet.getPort();
                SocketAddress sendAddress = packet.getSocketAddress();

                for (ITypeProcessor processor : typeProcessors) {
                    processor.processer(packet);
                }
            }
        } catch (SocketException e) {
            logger.error("UdpManagerThread - SocketException", e);
        } catch (IOException e) {
            logger.error("UdpManagerThread - IOException", e);
        }

        if (!stop) {

        }
    }
}