package com.yk.telnet.test;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class AwaitTest
{
    private static final Object LOCK = new Object();

    public static void main3(String args[]) throws InterruptedException
    {
        boolean run = true;
        List<String> list = new ArrayList<>();
        list.add("123");
        AtomicInteger integer = new AtomicInteger();
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(list.size(), r -> new Thread(r, "list-" + integer.getAndIncrement()));

        for (String str : list)
        {
            executor.scheduleWithFixedDelay(() ->
            {
                System.out.println("str : " + str);
            }, 0, 1, TimeUnit.SECONDS);
        }
        TimeUnit.SECONDS.sleep(5);
        // shutdownNow()并不代表线程池就一定立即就能退出，它可能必须要等待所有正在执行的任务都执行完成了才能退出, 例如线程内容使用了 while (true) 则永不能停止
        // 定时任务内部线程都打印完毕后, 主线程执行到此处时线程池会立即退出
        executor.shutdownNow();
    }


    public static void main(String args[])
    {
        // 如果一个线程中没有sleep wait Condition 定时锁等应用, interrupt()方法是无法中断该线程的
        Thread thread = new Thread(() ->
        {
            long start = System.currentTimeMillis();
            while (true)
            {
                System.out.print(".");
                long end = System.currentTimeMillis();
                if (end - start > 1000)
                {
                    synchronized (LOCK)
                    {
                        try
                        {
                            LOCK.wait();
                        }
                        catch (InterruptedException e)
                        {
                            System.out.println();
                            System.out.println("interrupted");
                            // 没有break的话, 就只是捕获了一次interrupt异常, while循环还会继续执行, 下一次循环会再进入到wait状态
                            break;
                        }
                    }
                    System.out.println("continue");
                }
            }
        });
        thread.start();

        thread.interrupt();
    }

    public static void main2(String args[]) throws InterruptedException
    {
        boolean run = true;
        List<String> list = new ArrayList<>();
        list.add("123");
        AtomicInteger integer = new AtomicInteger();
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(list.size(), r -> new Thread(r, "list-" + integer.getAndIncrement()));

        for (String str : list)
        {
            executor.scheduleWithFixedDelay(() ->
            {
                System.out.println("str : " + str);
                synchronized (LOCK)
                {
                    try
                    {
                        // 定时任务中的线程如果处于wait中 LOCK.wait(); 则定时任务不会继续循环定时去执行,直到其被notify
                        LOCK.wait();
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
            }, 0, 1, TimeUnit.SECONDS);
        }

        TimeUnit.SECONDS.sleep(5);

        // shutdownNow()并不代表线程池就一定立即就能退出，它可能必须要等待所有正在执行的任务都执行完成了才能退出, 例如线程内容使用了 while (true) 则永不能停止
        // 或者线程都处于wait sleep 等状态也可立即退出
        // 定时任务内部线程都处于wait中，主线程执行到此处时线程池会立即退出
        executor.shutdownNow();
    }

    // 单独只使用awaitTermination(15, TimeUnit.SECONDS);  主线程会等待15s, 之后继续往下执行,此时线程池还存活 可继续提交线程; 因此awaitTermination配合shutdown使用时, 不能放在前面,否则要先等待15s
    @Test
    public void main() throws InterruptedException
    {
        ExecutorService service = Executors.newFixedThreadPool(3);
        service.submit(() ->
        {
            try
            {
                TimeUnit.SECONDS.sleep(3);
                System.out.println("over");
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        });

        service.shutdown(); // 阻止新来的任务提交，对已经提交了的任务不会产生任何影响
        try
        {
            if (!service.awaitTermination(15, TimeUnit.SECONDS)) // awaitTermination 可确保线程池内部所有线程执行完毕后关闭，除非内部线程执行时间超过timeout
            {
                service.shutdownNow();
                if (!service.awaitTermination(15, TimeUnit.SECONDS))
                {
                    System.out.println("Pool did not terminate in " + 15 + " seconds");
                }
            }
        }
        catch (InterruptedException ie)
        {
            service.shutdownNow();
            Thread.currentThread().interrupt();
        }

        boolean isShutdown = service.isShutdown();
        System.out.println(isShutdown);
        Thread.currentThread().join();
    }
}
