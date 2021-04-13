package com.yk.message.impl.factory;

import com.yk.message.api.consumer.IConsumer;
import com.yk.message.api.factory.AbstractConsumerFactory;
import com.yk.message.impl.consumer.ConsumerImpl;

import java.util.Map;
import java.util.Optional;
import java.util.Properties;

public class ConsumerFactory<K, V> extends AbstractConsumerFactory<K, V>
{
    @Override
    public IConsumer<K, V> getConsumer(Map<String, Object> customerParams)
    {
        Properties pro = new Properties();
        Optional.ofNullable(customerParams).ifPresent(t -> t.entrySet().stream().map(s ->
        {
            pro.put(s.getKey(), s.getValue());
            return pro;
        }));
        return new ConsumerImpl<K, V>(pro);
    }
}
