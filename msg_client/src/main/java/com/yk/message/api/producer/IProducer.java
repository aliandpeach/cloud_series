package com.yk.message.api.producer;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;

import java.util.List;
import java.util.concurrent.Future;

public interface IProducer<K, V> {
    List<Future<RecordMetadata>> send(ProducerRecord<K, V> producerRecord);

    List<Future<RecordMetadata>> send(ProducerRecord<K, V> producerRecord, Callback callback);

    long lastOffset(TopicPartition partition);

    void close();
}
