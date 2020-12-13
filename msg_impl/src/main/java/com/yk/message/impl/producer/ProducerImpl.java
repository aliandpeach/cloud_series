package com.yk.message.impl.producer;

import com.yk.message.api.producer.IProducer;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class ProducerImpl<K, V> implements IProducer<K, V> {
    private Map<String, KafkaProducer<K, V>> kafkaProducerMap = new ConcurrentHashMap<>();

    public ProducerImpl() {
    }

    public List<Future<RecordMetadata>> send(ProducerRecord<K, V> producerRecord) {
        return this.send(producerRecord, null);
    }

    public List<Future<RecordMetadata>> send(ProducerRecord<K, V> producerRecord, Callback callback) {
        List<Future<RecordMetadata>> metadataFutureList = kafkaProducerMap.entrySet()
                .stream().map(entry -> entry.getValue().send(producerRecord)).collect(Collectors.toCollection(() -> new ArrayList<>()));
        return metadataFutureList;
    }

    public long lastOffset(TopicPartition partition) {
        return 0;
    }

    public void close() {
        kafkaProducerMap.entrySet()
                .stream().forEach(entry -> entry.getValue().close());
    }
}
