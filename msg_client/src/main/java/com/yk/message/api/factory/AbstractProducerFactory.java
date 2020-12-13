package com.yk.message.api.factory;

import com.yk.message.api.producer.IProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Map;

public abstract class AbstractProducerFactory<K, V> {
    public IProducer<K, V> getProducer(Map<String, Object> map) {
        return null;
    }

    public ProducerRecord<Object, V> createProducerRecord(String v) {
        return null;
    }

    public ProducerRecord<K, V> createProducerRecord(String k, String v) {
        return null;
    }

    public ProducerRecord<K, V> createProducerRecord(int mills, String k, String v) {
        return null;
    }
}
