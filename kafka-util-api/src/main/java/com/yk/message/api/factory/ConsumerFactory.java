package com.yk.message.api.factory;

import com.yk.message.api.consumer.IConsumer;
import com.yk.message.api.util.FactoryInitClassLoader;

import java.util.Map;

public class ConsumerFactory<K, V> extends AbstractConsumerFactory<K, V>
{
    private AbstractConsumerFactory<K, V> factory;
    
    private ConsumerFactory()
    {
        try
        {
            factory = (AbstractConsumerFactory<K, V>) FactoryInitClassLoader.getInstance().getLoader().loadClass("com.yk.message.impl.factory.ConsumerFactory").newInstance();
        }
        catch (InstantiationException | IllegalAccessException | ClassNotFoundException e)
        {
            e.printStackTrace();
        }
    }
    
    @Override
    public IConsumer<K, V> getConsumer(Map<String, Object> map)
    {
        return factory.getConsumer(map);
    }
    
    private static class ConsumerFactoryHolder
    {
        public static ConsumerFactory INSTNACE = new ConsumerFactory();
    }
}
