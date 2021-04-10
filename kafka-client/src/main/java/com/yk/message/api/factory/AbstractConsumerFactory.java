package com.yk.message.api.factory;

import com.yk.message.api.consumer.IConsumer;

import java.util.Map;

public abstract class AbstractConsumerFactory<K, V> {
    public abstract IConsumer<K, V> getConsumer(Map<String, Object> map);
}
