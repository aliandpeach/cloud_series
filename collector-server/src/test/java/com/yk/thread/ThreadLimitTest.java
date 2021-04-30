package com.yk.thread;

import com.google.common.util.concurrent.Monitor;
import com.google.common.util.concurrent.RateLimiter;
import org.junit.Before;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.lang.Thread.currentThread;

/**
 * 限制时间段内最多允许打印的次数
 */

public class ThreadLimitTest
{
    private static double MAX_COUNT = 5;
    
    private static double TIME_DURATION = 30;
    
    @Before
    public void before()
    {
    }
    
    
    /**
     * 利用guava实现平滑限流
     *
     * @throws InterruptedException
     */
    @Test
    public void test() throws InterruptedException
    {
        RateLimiter rateLimiter = RateLimiter.create(0.2);
        ExecutorService executor = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 5; i++)
        {
            final int a = i;
            executor.submit(() ->
            {
//                boolean success = rateLimiter.tryAcquire();
//                if (!success)
//                {
//                    System.out.println("throw");
//                    throw new RuntimeException("xx");
//                }
                rateLimiter.acquire(1);
                System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + ", " + a);
            });
        }
        
        currentThread().join();
    }
    
    /**
     * 利用guava实现限流 每秒5kb限流量
     *
     * @throws InterruptedException
     */
    @Test
    public void test2() throws InterruptedException
    {
        RateLimiter rateLimiter = RateLimiter.create(5120);
        
        byte[] packet = new byte[5120];
        rateLimiter.acquire(packet.length);
        
        System.out.println("send: 5kb bytes");
    
        rateLimiter.acquire(packet.length);
        System.out.println("send: 5kb bytes");
        currentThread().join();
    }
    
    
    @Test
    public void testBucket() throws InterruptedException
    {
        RateLimiter rateLimiter = RateLimiter.create(MAX_COUNT / TIME_DURATION);
        
        //往桶里面放数据时，确认没有超过桶的最大的容量
        Monitor offerMonitor = new Monitor();
        
        //从桶里消费数据时，桶里必须存在数据
        Monitor consumerMonitor = new Monitor();
        
        /*ExecutorService executor = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 1000; i++)
        {
            final int a = i;
            executor.submit(() ->
            {
                if (consumerMonitor.enterIf(consumerMonitor.newGuard(() -> !container.isEmpty())))
                {
                    try
                    {
                        rateLimiter.acquire();
                        System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + ", " + a);
                    }
                    finally
                    {
                        consumerMonitor.leave();
                    }
                }
                else
                {
                    //当木桶的消费完后，可以消费那些降级存入MQ或者DB里面的数据
                    System.out.println("will consumer Data from MQ...");
                    try
                    {
                        TimeUnit.SECONDS.sleep(10);
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
            });
        }*/
        
        currentThread().join();
    }
    
    private void _sleep(long mills)
    {
        try
        {
            Thread.sleep(mills);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }
}
