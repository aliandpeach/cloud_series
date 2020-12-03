package com.yk.collector.task;

import com.yk.collector.task.analysis.TypeProcessor;
import com.yk.collector.task.consumer.ParseConsumer;
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
import java.util.concurrent.ThreadPoolExecutor;

public class TaskDefaultFactory {

    private static final int TASK_COUNT = 3;

    private Map<TaskType, AbstractProducerConsumer> consumers = new ConcurrentHashMap<TaskType, AbstractProducerConsumer>();

    private Map<TaskType, AbstractProducerConsumer> roducers = new ConcurrentHashMap<TaskType, AbstractProducerConsumer>();

    private UdpManagerThread udpManagerThread;

    private TypeProcessor typeProcessor;

    private ExecutorService executor;

    public void init() {
        executor = Executors.newFixedThreadPool(11);

        /**
         * UDP 线程和Transmit的公共buffer
         */
        Buffer<DataPacket> buffer = new Buffer<DataPacket>();
        /**
         * PacketTransmitProducer -> ParseConsumer
         */
        LinkedBlockingQueue<DataPacket> queue = new LinkedBlockingQueue<DataPacket>();
        for (int i = 0; i < TASK_COUNT; i++) {
            for (TaskType type : TaskType.values()) {
                consumers.put(type, new ParseConsumer());
                consumers.put(type, new PacketTransmitProducer());
            }
        }

        typeProcessor = new TypeProcessor(buffer);
        udpManagerThread = new UdpManagerThread(typeProcessor);
        udpManagerThread.setUncaughtExceptionHandler((thread, exception) -> {

        });
        executor.submit(udpManagerThread);

        for (Map.Entry<TaskType, AbstractProducerConsumer> producer : roducers.entrySet()) {
            producer.getValue().setUncaughtExceptionHandler((thread, exception) -> {

            });
            executor.submit(producer.getValue());
        }
        for (Map.Entry<TaskType, AbstractProducerConsumer> consumer : consumers.entrySet()) {
            consumer.getValue().setUncaughtExceptionHandler((thread, exception) -> {

            });
            executor.submit(consumer.getValue());
        }
    }
}
