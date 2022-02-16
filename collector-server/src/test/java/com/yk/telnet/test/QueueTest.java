package com.yk.telnet.test;

import org.junit.Test;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.IntStream;

public class QueueTest
{
    @Test
    public void testOffer()
    {
        BlockingQueue<String> link = new LinkedBlockingQueue<>(2);
        link.offer("1");
        link.offer("2");
        link.offer("3"); //不会等待,直接返回false
        System.out.println(link);
    }

    @Test
    public void testPut() throws InterruptedException
    {
        BlockingQueue<String> link = new LinkedBlockingQueue<>(2);
        link.put("1");
        link.put("2");
        link.put("3"); //一直等待
        System.out.println(link);
    }

    @Test
    public void testAdd()
    {
        BlockingQueue<String> link = new LinkedBlockingQueue<>(2);
        link.add("1");
        link.add("2");
        link.add("3"); //抛出异常
        System.out.println(link);
    }

    @Test
    public void testTake() throws InterruptedException
    {
        IntStream.range(0, 10).forEach(t -> System.out.println(t));
        System.out.println(1 % 5);
        BlockingQueue<String> link = new LinkedBlockingQueue<>(2);
        link.take(); //一直等待
        System.out.println(link);
    }

    @Test
    public void testPoll() throws InterruptedException
    {
        IntStream.range(0, 10).forEach(t -> System.out.println(t));
        System.out.println(1 % 5);
        BlockingQueue<String> link = new LinkedBlockingQueue<>(2);
        String r = link.poll();
        System.out.println(link);
    }
}
