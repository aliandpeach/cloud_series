package com.yk.message.api.factory;

import com.yk.message.api.consumer.IConsumer;
import com.yk.message.api.util.ApiClassLoader;

import java.util.Map;

public class ConsumerFactory<K, V> extends AbstractConsumerFactory<K, V> {

    private AbstractConsumerFactory<K, V> factory;

    private ConsumerFactory() {
        try {
            ApiClassLoader.getInstance().getLoader().loadClass("").newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public IConsumer<K, V> getConsumer(Map<String, Object> map) {
        return factory.getConsumer(map);
    }

    private static class ConsumerFactoryHolder {
        public static ConsumerFactory INSTNACE = new ConsumerFactory();
    }
}
