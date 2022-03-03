package com.yk.message.impl.factory;

import com.yk.message.api.factory.AbstractProducerFactory;
import com.yk.message.api.producer.IProducer;
import com.yk.message.impl.producer.ProducerImpl;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

public class ProducerFactory extends AbstractProducerFactory
{
    @Override
    public <K, V> IProducer<K, V> getProducer(Map<String, Object> producerProperties)
    {
        Properties properties = new Properties();
        Optional.ofNullable(producerProperties).orElse(new HashMap<>()).forEach(properties::put);
        return new ProducerImpl<K, V>(properties);
    }

    @Override
    public <K, V> ProducerRecord<K, V> createProducerRecord(String v)
    {
        return null;
    }

    @Override
    public <K, V> ProducerRecord<K, V> createProducerRecord(String k, String v)
    {
        return null;
    }

    @Override
    public <K, V> ProducerRecord<K, V> createProducerRecord(int mills, String k, String v)
    {
        return null;
    }

}