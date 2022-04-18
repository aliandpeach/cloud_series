package com.yk.telnet.test;

import org.junit.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.stream.Collectors;
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

    public static void main2(String args[])
    {
        BlockingQueue<String> queue = new LinkedBlockingQueue<>();
        queue.offer("hadoop01");
        queue.offer("hadoop02");
        queue.offer("hadoop03");
        queue.offer("hadoop04");
        queue.offer("hadoop05");

        List<Runnable> runnableList = IntStream.range(0, 100).mapToObj(t -> (Runnable) () ->
        {
            while (true)
            {
                try
                {
                    System.out.println(get(queue));
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        }).collect(Collectors.toList());
        runnableList.forEach(r -> new Thread(r).start());
    }
    public static void main(String args[]) throws UnknownHostException
    {
        NiuA niuA = new NiuA(new String[]{"hadoop01", "hadoop02", "hadoop03", "hadoop04", "hadoop05"});
        String ip = InetAddress.getByName("hadoop01").getHostAddress();
        List<Runnable> runnableList = IntStream.range(0, 100).mapToObj(t -> (Runnable) () ->
        {
            while (true)
                System.out.println(niuA.getIp());
        }).collect(Collectors.toList());
        runnableList.forEach(r -> new Thread(r).start());
    }

    private static String get(BlockingQueue<String> queue) throws InterruptedException
    {
        synchronized (QueueTest.class)
        {
            String a = queue.take();
            queue.put(a);
            return a;
        }
    }
}
