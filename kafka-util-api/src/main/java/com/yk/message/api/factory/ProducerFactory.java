package com.yk.message.api.factory;

import com.yk.message.api.producer.IProducer;
import com.yk.message.api.util.FactoryInitClassLoader;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class ProducerFactory extends AbstractProducerFactory
{
    private Logger logger = LoggerFactory.getLogger(ProducerFactory.class);

    private AbstractProducerFactory factory;

    private ProducerFactory()
    {
        try
        {
            factory = (AbstractProducerFactory) FactoryInitClassLoader.getInstance().getLoader().loadClass("com.yk.message.impl.factory.ProducerFactory").newInstance();
        }
        catch (InstantiationException | IllegalAccessException | ClassNotFoundException e)
        {
            logger.error("ProducerFactory init constructor error", e);
        }
    }

    @Override
    public <K, V> IProducer<K, V> getProducer(Map<String, Object> map)
    {
        return factory.getProducer(map);
    }

    @Override
    public <K, V> ProducerRecord<K, V> createProducerRecord(String v)
    {
        return factory.createProducerRecord(v);
    }

    @Override
    public <K, V> ProducerRecord<K, V> createProducerRecord(String k, String v)
    {
        return factory.createProducerRecord(k, v);
    }

    @Override
    public <K, V> ProducerRecord<K, V> createProducerRecord(int mills, String k, String v)
    {
        return factory.createProducerRecord(mills, k, v);
    }

    public static ProducerFactory getInstance()
    {
        return ProducerFactory.ProducerFactoryHolder.INSTNACE;
    }

    private static class ProducerFactoryHolder
    {
        public static ProducerFactory INSTNACE = new ProducerFactory();
    }
}
