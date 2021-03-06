package com.yk.telnet.test;

import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class AwaitTest
{
    // service.shutdown();
    // shutdown之后不能再提交 会抛出异常
    // service.submit(() -> System.out.println(2));

    // 单独只使用awaitTermination(15, TimeUnit.SECONDS); 那么整个线程池会等待设置的时间
    @Test
    public void main() throws InterruptedException
    {
        ExecutorService service = Executors.newFixedThreadPool(3);
        service.submit(() -> {System.out.println(1);
            try
            {
                TimeUnit.SECONDS.sleep(5);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        });

        // awaitTermination之后 线程池还存活 继续可以使用
        // service.shutdown(); // 执行 shutdown请求后, 调用awaitTermination可确保线程池内部所有线程执行完毕后关闭，除非内部线程执行时间超过timeout
        boolean is = service.awaitTermination(15, TimeUnit.SECONDS);

        boolean isShutdown = service.isShutdown();
        System.out.println(isShutdown);
        Thread.currentThread().join();
    }
}
