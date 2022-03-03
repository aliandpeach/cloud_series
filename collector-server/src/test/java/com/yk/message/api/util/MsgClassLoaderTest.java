package com.yk.message.api.util;


import com.yk.message.api.factory.AbstractConsumerFactory;

import java.net.URL;

public class MsgClassLoaderTest {
    public static void main(String[] args) {
        try {
            Class<?> clazz = FactoryInitClassLoader.getInstance().getLoader().loadClass("com.yk.message.impl.factory.ConsumerFactory");
            AbstractConsumerFactory t = (AbstractConsumerFactory)clazz.newInstance();
            t.getConsumer(null);
            System.out.println("");
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
