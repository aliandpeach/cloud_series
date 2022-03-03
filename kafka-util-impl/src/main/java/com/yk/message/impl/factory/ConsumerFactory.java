package com.yk.message.impl.factory;

import com.yk.message.api.consumer.IConsumer;
import com.yk.message.api.factory.AbstractConsumerFactory;
import com.yk.message.impl.consumer.ConsumerImpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;

public class ConsumerFactory extends AbstractConsumerFactory
{
    @Override
    public <K, V> IConsumer<K, V> getConsumer(Map<String, Object> customerParams)
    {
        Properties properties = new Properties();
        Optional.ofNullable(customerParams).orElse(new HashMap<>()).forEach(properties::put);
        return new ConsumerImpl<K, V>(properties);
    }
}
