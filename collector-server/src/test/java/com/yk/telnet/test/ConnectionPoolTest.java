package com.yk.telnet.test;

import com.yk.collector.telnet.CmdConnectionTool;
import com.yk.collector.telnet.ConnectionPool;
import com.yk.collector.telnet.TargetInfo;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

public class ConnectionPoolTest
{
    @Test
    public void main() throws InterruptedException
    {
        TargetInfo target1 = new TargetInfo();
        target1.setIp("192.168.32." + 1);
        target1.setUsername("username" + 1);
        target1.setPwd(new StringBuffer("pwd" + 1));

        ExecutorService service = Executors.newFixedThreadPool(10);

        service.submit(new Thread(() ->
        {
            for (int i = 1; i <= 3; i++)
            {
                CmdConnectionTool tool = ConnectionPool.getInstance().getConnectionTool(target1);
                tool.cmdExecute(target1, "cmd1=" + target1.getIp());
            }
        }));
        service.submit(new Thread(() ->
        {
            for (int i = 1; i <= 3; i++)
            {
                CmdConnectionTool tool = ConnectionPool.getInstance().getConnectionTool(target1);
                tool.cmdExecute(target1, "cmd2=" + target1.getIp());
            }
        }));
        new Thread(() ->
        {
            for (int i = 1; i <= 3; i++)
            {
                ConnectionPool.getInstance().releaseConnectionTool(target1);
            }
        }).start();
        Thread.currentThread().join();
    }

    /**
     * 分批依次执行线程 1
     */
    @Test
    public void testRunThreadInBatches() throws InterruptedException
    {
        int total = 100;
        int runCount = 5;

        ExecutorService service = Executors.newFixedThreadPool(runCount * 2);

        AtomicInteger counter = new AtomicInteger(0);
        Queue<RunThread> list = new LinkedBlockingQueue<>();
        for (int i = 0; i < total; i++)
        {
            RunThread th = new RunThread();
            th.setCounter(counter);
            list.offer(th);
        }

        while (list.size() != 0)
        {
            CountDownLatch down = new CountDownLatch(runCount);
            for (int i = 0; i < runCount; i++)
            {
                RunThread th = list.poll();
                if (null == th)
                {
                    // (total / runCount) 不一定是整除
                    continue;
                }
                th.setDown(down);
                service.submit(th);
            }
            down.await();
        }
        service.shutdown();
        service.awaitTermination(1, TimeUnit.MINUTES);
    }

    /**
     * 分批依次执行线程 2
     * <p>
     * 利用 CyclicBarrier 的特性
     */
    @Test
    public void testCyclicBarrier() throws InterruptedException
    {
        int total = 100;
        int runCount = 20;

        AtomicInteger counter = new AtomicInteger(0);
        Queue<RunThreadBarrier> list = new LinkedBlockingQueue<>();
        for (int i = 0; i < total; i++)
        {
            RunThreadBarrier th = new RunThreadBarrier();
            th.setCounter(counter);
            list.offer(th);
        }

        ExecutorService service = Executors.newFixedThreadPool(runCount * 2);

        List<String> result = new ArrayList<>();

        while (list.size() != 0)
        {
            List<Future<String>> futures = new ArrayList<>();
            CountDownLatch down = new CountDownLatch(1);
            CyclicBarrier cyclicBarrier = new CyclicBarrier(runCount, new Run(counter, down));
            for (int i = 0; i < runCount; i++)
            {
                RunThreadBarrier th = list.poll();
                if (null == th)
                {
                    // (total / runCount) 不一定是整除
                    continue;
                }
                th.setBarrier(cyclicBarrier);
                futures.add(service.submit(th));
            }
        }
        service.shutdown();
        service.awaitTermination(1, TimeUnit.MINUTES);
    }

    private class Run implements Runnable
    {
        private AtomicInteger counter;
        private CountDownLatch down;

        public Run(AtomicInteger counter, CountDownLatch down)
        {
            this.counter = counter;
            this.down = down;
        }

        @Override
        public void run()
        {
            System.out.println("完成" + counter.get());
            down.countDown();
        }
    }

    /**
     * 分批依次执行线程 2.1
     * <p>
     * 利用 CyclicBarrier 的特性
     */
    @Test
    public void testCyclicBarrier2_1() throws InterruptedException
    {
        int total = 20;
        int runCount = 5;

        AtomicInteger counter = new AtomicInteger(0);
        Queue<RunThreadBarrier> list = new LinkedBlockingQueue<>();
        for (int i = 0; i < total; i++)
        {
            RunThreadBarrier th = new RunThreadBarrier();
            th.setCounter(counter);
            list.offer(th);
        }

        ExecutorService service = Executors.newFixedThreadPool(runCount * 2);

        List<String> result = new ArrayList<>();

        int group = total % runCount == 0 ? total / runCount : total / runCount + 1;

        IntStream.range(0, group).forEach(t ->
        {
            List<CompletableFuture<String>> futures = new ArrayList<>();
            for (int i = 0; i < runCount; i++)
            {
                RunThreadBarrier th = list.poll();
                if (null == th)
                {
                    // (total / runCount) 不一定是整除
                    continue;
                }
                futures.add(CompletableFuture.supplyAsync(th::call, service));
            }
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
            futures.forEach(f ->
            {
                try
                {
                    result.add(f.get());
                }
                catch (InterruptedException | ExecutionException e)
                {
                    e.printStackTrace();
                }
            });

            System.out.println("完成" + counter.get());
        });
        System.out.println(result);
        service.shutdown();
        service.awaitTermination(1, TimeUnit.MINUTES);
    }

    /**
     * 分批依次执行线程 3
     */
    @Test
    public void testRunThreadInBatches3() throws InterruptedException
    {
        int total = 100;
        int runCount = 5;
        long start = System.currentTimeMillis();
        AtomicInteger counter = new AtomicInteger(0);
        BlockingQueue<String> list = new LinkedBlockingQueue<>(5);

        ExecutorService service = Executors.newFixedThreadPool(runCount * 2);

        List<CompletableFuture<Void>> futures = new ArrayList<>();
        IntStream.range(0, total).forEach(t ->
        {
            futures.add(CompletableFuture.runAsync(() ->
            {
                try
                {
                    synchronized (ConnectionPoolTest.class)
                    {
                        while (list.size() == runCount)
                        {
                            ConnectionPoolTest.class.wait();
                        }
                    }
                    list.put("1");

                    TimeUnit.SECONDS.sleep(new Random().nextInt(5 - 2 + 1) + 2);
                    counter.incrementAndGet();
                    System.out.println(counter.get() + " end");
                    list.take();
                    /*synchronized (ConnectionPoolTest.class)
                    {
                        ConnectionPoolTest.class.notifyAll();
                    }*/
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }, service));
        });

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        long end = System.currentTimeMillis();
        System.out.println("总数" + total + " 个请求, 分批异步执行, 完成时间  = {}" + (end - start));

        service.shutdown();
        service.awaitTermination(1, TimeUnit.MINUTES);
    }

    private static class RunThread extends Thread
    {
        private CountDownLatch down;

        private AtomicInteger counter;

        public void setCounter(AtomicInteger counter)
        {
            this.counter = counter;
        }

        public void setDown(CountDownLatch down)
        {
            this.down = down;
        }

        @Override
        public void run()
        {
            try
            {
                System.out.println(Thread.currentThread().getName() + " start");
                TimeUnit.SECONDS.sleep(new Random().nextInt(5 - 2 + 1) + 2);
                System.out.println(Thread.currentThread().getName() + " end");
                counter.incrementAndGet();
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
            finally
            {
                down.countDown();
            }
        }
    }


    private static class RunThreadBarrier implements Callable<String>
    {
        private AtomicInteger counter;

        private CyclicBarrier barrier;

        public void setBarrier(CyclicBarrier barrier)
        {
            this.barrier = barrier;
        }

        public void setCounter(AtomicInteger counter)
        {
            this.counter = counter;
        }

        @Override
        public String call()
        {
            try
            {
//                System.out.println(Thread.currentThread().getName() + " start");
                TimeUnit.SECONDS.sleep(new Random().nextInt(5 - 2 + 1) + 2);
                System.out.println(Thread.currentThread().getName() + " end");
                return "success" + counter.incrementAndGet();
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
                return "failed" + counter.get();
            }
            finally
            {
                if (null != barrier)
                {
                    try
                    {
                        barrier.await();
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                    catch (BrokenBarrierException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
