package com.yk.telnet.test;

import com.yk.collector.telnet.CmdConnectionTool;
import com.yk.collector.telnet.ConnectionPool;
import com.yk.collector.telnet.TargetInfo;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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
}
