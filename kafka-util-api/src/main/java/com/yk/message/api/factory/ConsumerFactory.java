package com.yk.message.api.factory;

import com.yk.message.api.consumer.IConsumer;
import com.yk.message.api.util.FactoryInitClassLoader;

import java.util.Map;

public class ConsumerFactory extends AbstractConsumerFactory
{
    private AbstractConsumerFactory factory;

    private ConsumerFactory()
    {
        try
        {
            factory = (AbstractConsumerFactory) FactoryInitClassLoader.getInstance().getLoader().loadClass("com.yk.message.impl.factory.ConsumerFactory").newInstance();
        }
        catch (InstantiationException | IllegalAccessException | ClassNotFoundException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public <K, V> IConsumer<K, V> getConsumer(Map<String, Object> map)
    {
        return factory.getConsumer(map);
    }

    public static ConsumerFactory getInstance()
    {
        return ConsumerFactoryHolder.INSTNACE;
    }

    private static class ConsumerFactoryHolder
    {
        public static ConsumerFactory INSTNACE = new ConsumerFactory();
    }
}
