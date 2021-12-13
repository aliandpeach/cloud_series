package com.yk.telnet.test;

import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class AwaitTest
{
    // 单独只使用awaitTermination(15, TimeUnit.SECONDS);  主线程会等待15s, 之后继续往下执行,此时线程池还存活 继续提交线程; 因此awaitTermination配合shutdown使用时, 不能放在前面,否则要先等待15s
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
                service.shutdownNow(); //阻止新来的任务提交，同时会中断当前正在运行的线程
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
