package com.yk.message.api.factory;

import com.yk.message.api.consumer.IConsumer;

import java.util.Map;

public abstract class AbstractConsumerFactory
{
    public abstract <K, V> IConsumer<K, V> getConsumer(Map<String, Object> map);
}
