package com.yk.collector.telnet;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * 管理连接工具实例的连接池
 */
public class ConnectionPool {
//    private Semaphore semaphore = new Semaphore(3);

    private Map<TargetInfo, List<CmdConnectionTool>> pool = new ConcurrentHashMap<>();

    private Map<TargetInfo, Semaphore> controller = new ConcurrentHashMap<>();

    private ConnectionPool() {
    }

    public CmdConnectionTool connectionTool(TargetInfo targetInfo) {

        try {
            Semaphore semaphore = null;
            synchronized (this) {
                semaphore = controller.get(targetInfo);
                if (semaphore == null) {
                    semaphore = new Semaphore(3);
                    controller.put(targetInfo, semaphore);
                }
                semaphore = controller.get(targetInfo);
            }
            boolean is = semaphore.tryAcquire(30 * 1000, TimeUnit.MILLISECONDS);
            if (!is) {
                throw new RuntimeException("tryAcquire error = " + targetInfo);
            }
            synchronized (this) {
                List<CmdConnectionTool> tools = pool.get(targetInfo);
                if (tools == null) {
                    tools = new ArrayList<>();
                }
                CmdConnectionTool tool = new CmdConnectionTool();
                tools.add(tool);
                return tool;
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ConnectionPool getInstance() {
        return ConnectionPoolHolder.INSTANCE;
    }

    private static class ConnectionPoolHolder {
        public static ConnectionPool INSTANCE = new ConnectionPool();
    }
}
