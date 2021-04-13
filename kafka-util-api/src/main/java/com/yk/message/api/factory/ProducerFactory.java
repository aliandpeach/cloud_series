package com.yk.message.api.factory;

import com.yk.message.api.producer.IProducer;
import com.yk.message.api.util.FactoryInitClassLoader;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class ProducerFactory<K, V> extends AbstractProducerFactory<K, V>
{
    private Logger logger = LoggerFactory.getLogger(ProducerFactory.class);
    
    private AbstractProducerFactory<K, V> factory;
    
    private ProducerFactory()
    {
        try
        {
            factory = (AbstractProducerFactory<K, V>) FactoryInitClassLoader.getInstance().getLoader().loadClass("com.yk.message.impl.factory.ProducerFactory").newInstance();
        }
        catch (InstantiationException | IllegalAccessException | ClassNotFoundException e)
        {
            logger.error("ProducerFactory init constructor error", e);
        }
    }
    
    @Override
    public IProducer<K, V> getProducer(Map<String, Object> map)
    {
        return factory.getProducer(map);
    }
    
    @Override
    public ProducerRecord<Object, V> createProducerRecord(String v)
    {
        return factory.createProducerRecord(v);
    }
    
    @Override
    public ProducerRecord<K, V> createProducerRecord(String k, String v)
    {
        return factory.createProducerRecord(k, v);
    }
    
    @Override
    public ProducerRecord<K, V> createProducerRecord(int mills, String k, String v)
    {
        return factory.createProducerRecord(mills, k, v);
    }
    
    private static class ProducerFactoryHolder
    {
        public static ProducerFactory INSTNACE = new ProducerFactory();
    }
}
