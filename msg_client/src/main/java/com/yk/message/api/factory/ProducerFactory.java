package com.yk.message.api.factory;

import com.yk.message.api.producer.IProducer;
import com.yk.message.api.util.MsgClassLoader;

import java.util.Map;

public class ProducerFactory<K, V> extends AbstractProducerFactory<K, V> {

    private AbstractProducerFactory<K, V> factory;

    private ProducerFactory() {
        try {
            factory = (AbstractProducerFactory) MsgClassLoader.getInstance().getLoader().loadClass("com.yk.message.impl.factory.ProducerFactory").newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public IProducer<K, V> getProducer(Map<String, Object> map) {
        return factory.getProducer(map);
    }

    private static class ProducerFactoryHolder {
        public static ProducerFactory INSTNACE = new ProducerFactory();
    }
}
