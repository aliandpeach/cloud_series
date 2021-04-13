package com.yk.message.api.factory;

import com.yk.message.api.producer.IProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Map;

public abstract class AbstractProducerFactory<K, V>
{
    public abstract IProducer<K, V> getProducer(Map<String, Object> map);
    
    public abstract ProducerRecord<Object, V> createProducerRecord(String v);
    
    public abstract ProducerRecord<K, V> createProducerRecord(String k, String v);
    
    public abstract ProducerRecord<K, V> createProducerRecord(int mills, String k, String v);
}
