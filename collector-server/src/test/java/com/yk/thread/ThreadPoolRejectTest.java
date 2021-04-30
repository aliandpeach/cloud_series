package com.yk.thread;

import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * ThreadPoolRejectTest
 *
 * @author yangk
 * @version 1.0
 * @since 2021/4/27 10:03
 */

public class ThreadPoolRejectTest
{
    /**
     * 测试: count = 最大线程数 - 核心线程数 + 核心线程数 + 队列中的线程
     *
     * count 是该线程池能提交的最大数量（当内部已经提交了count个线程，并且都没有执行结束）
     *
     * @throws InterruptedException
     */
    @Test
    public void test() throws InterruptedException
    {
        ExecutorService executor = new ThreadPoolExecutor(10, 11,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(10), new RejectedExecutionHandler()
        {
            @Override
            public void rejectedExecution(Runnable r, ThreadPoolExecutor executor)
            {
                System.out.println(r);
            }
        });
        for (int i = 0; i < 20; i++)
        {
            final int a = i;
            executor.submit(new Thread(() -> this.waitForever(null)));
        }
        
        executor.submit(new Thread(() -> this.waitForever("add1")));
        executor.submit(new Thread(() -> this.waitForever("add2")));
        Thread.currentThread().join();
    }
    
    private void waitForever(String printContent)
    {
        if (null != printContent && printContent.length() > 0)
        {
            System.out.println(printContent);
        }
        while (true)
        {
            try
            {
                Thread.sleep(10000);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }
}
