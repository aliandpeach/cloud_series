package com.yk.message.impl.consumer;

import com.yk.message.api.consumer.IConsumer;
import com.yk.message.impl.util.ConfigUtil;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class ConsumerImpl<K, V> implements IConsumer<K, V> {

    private Map<String, KafkaConsumer<K, V>> kafkaConsumerMap = new ConcurrentHashMap<String, KafkaConsumer<K, V>>();

    private Map<KafkaConsumer<K, V>, Set<String>> kafkaConsumerSetMap = new ConcurrentHashMap<KafkaConsumer<K, V>, Set<String>>();

    private Properties properties;

    private final Object lock = new Object();

    public ConsumerImpl(Properties customerConfig) {
        properties = ConfigUtil.consumerConfigs();
        properties.putAll(customerConfig);
    }

    public void subscribe(List<String> topics) {
        if (null == topics) {
            throw new RuntimeException("topics is empty");
        }

        for (String topic : topics) {
            KafkaConsumer<K, V> kafkaConsumer = kafkaConsumerMap.get(topic);
            synchronized (this) {
                if (null == kafkaConsumer) {
                    kafkaConsumer = new KafkaConsumer<K, V>(properties);
                    kafkaConsumerMap.put(topic, kafkaConsumer);
                }
            }
            kafkaConsumer.subscribe(Arrays.asList(topic));
        }
    }

    public List<ConsumerRecords<K, V>> poll(final int mills) {
        List<ConsumerRecords<K, V>> recordsList = kafkaConsumerMap.entrySet().stream()
                .map(entry -> entry.getValue().poll(mills))
                .collect(Collectors.toCollection(() -> new ArrayList<>()));
        return recordsList;
    }

    public void commit() {
        kafkaConsumerMap.entrySet().stream()
                .forEach(entry -> entry.getValue().commitSync());
    }

    public void commitSync(final Map<TopicPartition, OffsetAndMetadata> offsets) {
        kafkaConsumerMap.entrySet().stream()
                .forEach(entry -> entry.getValue().commitSync(offsets));
    }

    public void asyncCommit() {
        kafkaConsumerMap.entrySet().stream()
                .forEach(entry -> entry.getValue().commitAsync());
    }

    public void seek(TopicPartition topicPartition, long offset) {
        kafkaConsumerMap.entrySet().stream()
                .forEach(entry -> entry.getValue().seek(topicPartition, offset));
    }

    public void seedToEnd(List<TopicPartition> partitions) {
        kafkaConsumerMap.entrySet().stream()
                .forEach(entry -> entry.getValue().seekToEnd(partitions));
    }

    public void pause(Collection<TopicPartition> partitions) {
        kafkaConsumerMap.entrySet().stream()
                .forEach(entry -> entry.getValue().pause(partitions));
    }

    public void resume(Collection<TopicPartition> partitions) {
        kafkaConsumerMap.entrySet().stream()
                .forEach(entry -> entry.getValue().resume(partitions));
    }

    public boolean topicExit(String topic) {
        return false;
    }

    public void close() {
        kafkaConsumerMap.entrySet().stream()
                .forEach(entry -> entry.getValue().close());
    }
}
