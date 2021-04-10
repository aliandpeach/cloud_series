package com.yk.message.api.util;


import com.yk.message.api.factory.AbstractConsumerFactory;

import java.net.URL;

public class MsgClassLoaderTest {
    public static void main(String[] args) {
        try {
            Class<?> clazz = MsgClassLoader.getInstance().getLoader().loadClass("com.yk.message.impl.factory.ConsumerFactory");
            AbstractConsumerFactory t = (AbstractConsumerFactory)clazz.newInstance();
            t.getConsumer(null);
            System.out.println("");
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
