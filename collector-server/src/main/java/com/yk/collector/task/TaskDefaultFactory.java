package com.yk.collector.task;

import com.yk.collector.task.analysis.TypeProcessor;
import com.yk.collector.task.consumer.AbstractConsumer;
import com.yk.collector.task.consumer.ParseConsumer;
import com.yk.collector.task.producer.AbstractProducer;
import com.yk.collector.task.producer.PacketTransmitProducer;
import com.yk.collector.task.storage.Buffer;
import com.yk.collector.task.storage.DataPacket;
import com.yk.collector.task.udp.UdpManagerThread;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 工厂类
 */
public class TaskDefaultFactory {

    private static final int TASK_COUNT = 3;

    private Map<TaskType, List<AbstractConsumer>> consumers = new ConcurrentHashMap<>();

    private Map<TaskType, List<AbstractProducer>> producers = new ConcurrentHashMap<>();

    private UdpManagerThread udpManagerThread;

    private TypeProcessor typeProcessor;

    private ExecutorService executor;

    public void init() {
        executor = Executors.newFixedThreadPool(11);

        /**
         * UDP 线程 -> PacketTransmitProducer
         */
        Buffer<DataPacket> buffer = new Buffer<DataPacket>();
        /**
         * PacketTransmitProducer -> ParseConsumer
         */
        LinkedBlockingQueue<DataPacket> queue = new LinkedBlockingQueue<DataPacket>();

        for (int i = 0; i < TASK_COUNT; i++) {
            for (TaskType type : TaskType.values()) {
                List<AbstractConsumer> clist = consumers.get(type);
                if (null == clist) {
                    clist = new ArrayList<>();
                    consumers.put(type, clist);
                }
                AbstractConsumer parse = new ParseConsumer();
                parse.setPacketQueue(queue);
                clist.add(parse);

                List<AbstractProducer> plist = producers.get(type);
                if (null == plist) {
                    plist = new ArrayList<>();
                    producers.put(type, plist);
                }
                AbstractProducer transmitProducer = new PacketTransmitProducer();
                transmitProducer.setBufferQueue(queue);
                transmitProducer.setPacketBuffer(buffer);
                plist.add(transmitProducer);
            }
        }

        typeProcessor = new TypeProcessor(buffer);
        udpManagerThread = new UdpManagerThread(typeProcessor);
        udpManagerThread.setUncaughtExceptionHandler((thread, exception) -> {
        });
        executor.submit(udpManagerThread);

        for (Map.Entry<TaskType, List<AbstractProducer>> entry : producers.entrySet()) {
            for (AbstractProducer producer : entry.getValue()) {
                producer.setUncaughtExceptionHandler((thread, exception) -> {
                });
                executor.submit(producer);
            }
        }
        for (Map.Entry<TaskType, List<AbstractConsumer>> entry : consumers.entrySet()) {
            for (AbstractConsumer consumer : entry.getValue()) {
                consumer.setUncaughtExceptionHandler((thread, exception) -> {
                });
                executor.submit(consumer);
            }
        }
    }
}
