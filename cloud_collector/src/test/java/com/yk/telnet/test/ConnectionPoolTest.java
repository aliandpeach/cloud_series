package com.yk.telnet.test;

import com.yk.collector.telnet.CmdConnectionTool;
import com.yk.collector.telnet.ConnectionPool;
import com.yk.collector.telnet.TargetInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ConnectionPoolTest {
    public static void main(String[] args) {
        List<TargetInfo> targets = new ArrayList<>();
        for (int i = 1; i <= 20; i++) {
            TargetInfo target = new TargetInfo();
            target.setIp("192.168.32." + i);
            target.setUsername("username" + i);
            target.setPwd(new StringBuffer("pwd" + i));
            targets.add(target);
        }

        ExecutorService service = Executors.newFixedThreadPool(10);

        service.submit(new Thread(() -> {
            for (TargetInfo t : targets) {
                CmdConnectionTool tool = ConnectionPool.getInstance().getConnectionTool(t);
                tool.cmdExecute(t, "cmd1=" + t.getIp());
            }
        }));
        service.submit(new Thread(() -> {
            for (TargetInfo t : targets) {
                CmdConnectionTool tool = ConnectionPool.getInstance().getConnectionTool(t);
                tool.cmdExecute(t, "cmd2=" + t.getIp());
            }
        }));
        service.submit(new Thread(() -> {
            for (TargetInfo t : targets) {
                CmdConnectionTool tool = ConnectionPool.getInstance().getConnectionTool(t);
                tool.cmdExecute(t, "cmd3=" + t.getIp());
            }
        }));
        service.shutdown();
        try {
            service.awaitTermination(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        new Thread(() -> {
            for (TargetInfo t : targets) {
                CmdConnectionTool tool = ConnectionPool.getInstance().getConnectionTool(t);
                tool.cmdExecute(t, "cmd4=" + t.getIp());
            }
        }).start();

        new Thread(() -> {
            for (TargetInfo t : targets) {
                ConnectionPool.getInstance().releaseConnectionTool(t);
            }
        }).start();
    }
}
