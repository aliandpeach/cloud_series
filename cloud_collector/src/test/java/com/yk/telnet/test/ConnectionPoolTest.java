package com.yk.telnet.test;

import com.yk.collector.telnet.CmdConnectionTool;
import com.yk.collector.telnet.ConnectionPool;
import com.yk.collector.telnet.TargetInfo;

import java.util.ArrayList;
import java.util.List;

public class ConnectionPoolTest {
    public static void main(String[] args) {
        List<TargetInfo> targets = new ArrayList<>();
        for (int i = 1; i <= 20; i++) {
            TargetInfo target = new TargetInfo();
            target.setIp("192.168.32." + i);
            target.setUsername("username" + i);
            target.setPwd(new char[]{'p', 'w', 'd', (char) i});
            targets.add(target);
        }

        new Thread(() -> {
            for (TargetInfo t : targets) {
                CmdConnectionTool tool = ConnectionPool.getInstance().connectionTool(t);
                tool.cmdExecute(t, "cmd=" + t.getIp());
            }
        }).start();
        new Thread(() -> {
            for (TargetInfo t : targets) {
                CmdConnectionTool tool = ConnectionPool.getInstance().connectionTool(t);
                tool.cmdExecute(t, "cmd=" + t.getIp());
            }
        }).start();
        new Thread(() -> {
            for (TargetInfo t : targets) {
                CmdConnectionTool tool = ConnectionPool.getInstance().connectionTool(t);
                tool.cmdExecute(t, "cmd=" + t.getIp());
            }
        }).start();
        new Thread(() -> {
            for (TargetInfo t : targets) {
                CmdConnectionTool tool = ConnectionPool.getInstance().connectionTool(t);
                tool.cmdExecute(t, "cmd=" + t.getIp());
            }
        }).start();


    }
}
