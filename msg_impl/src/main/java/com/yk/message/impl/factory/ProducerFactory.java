package com.yk.message.impl.factory;

import com.yk.message.api.factory.AbstractProducerFactory;
import com.yk.message.api.producer.IProducer;
import com.yk.message.impl.consumer.ConsumerImpl;
import com.yk.message.impl.producer.ProducerImpl;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

public class ProducerFactory<K, V> extends AbstractProducerFactory<K, V> {

    @Override
    public IProducer<K, V> getProducer(Map<String, Object> customerProperties) {
        Properties pro = new Properties();
        Optional.ofNullable(customerProperties).get().entrySet().stream().map(t -> {
            pro.put(t.getKey(), t.getValue());
            return pro;
        });
        return new ProducerImpl<K, V>(customerProperties);
    }

}