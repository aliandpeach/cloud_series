package com.yk.collector.telnet;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * 管理连接工具的连接池
 */
public class ConnectionPool {
    private Map<TargetInfo, List<CmdConnectionTool>> pool = new ConcurrentHashMap<>();

    private Map<TargetInfo, Semaphore> controller = new ConcurrentHashMap<>();

    private ConnectionPool() {
    }

    public CmdConnectionTool getConnectionTool(TargetInfo targetInfo) {
        try {
            Semaphore semaphore = getSemaphoreByTarget(targetInfo);
            boolean is = semaphore.tryAcquire(10 * 1000, TimeUnit.MILLISECONDS);
            if (!is) {
                throw new RuntimeException("tryAcquire error = " + targetInfo);
            }
            return getFreeCmdConnectionTool(targetInfo);

        } catch (InterruptedException e) {
            throw new RuntimeException("getConnectionTool error = " + targetInfo);
        }
    }

    public void releaseConnectionTool(TargetInfo targetInfo) {
        Semaphore semaphore = getSemaphoreByTarget(targetInfo);
        semaphore.release();
        CmdConnectionTool tool = getFreeCmdConnectionTool(targetInfo);
        tool.setFree(true);
    }

    private synchronized CmdConnectionTool getFreeCmdConnectionTool(TargetInfo targetInfo) {
        List<CmdConnectionTool> tools = pool.get(targetInfo);
        if (tools == null) {
            tools = new ArrayList<>();
        }
        for (CmdConnectionTool tool : tools) {
            if (tool.isFree()) {
                tool.setTargetInfo(targetInfo);
                return tool;
            }
        }
        CmdConnectionTool tool = new CmdConnectionTool();
        tool.setFree(false);
        tool.setTargetInfo(targetInfo);
        tools.add(tool);
        return tool;
    }

    private Semaphore getSemaphoreByTarget(TargetInfo targetInfo) {
        synchronized (this) {
            Semaphore semaphore = controller.get(targetInfo);
            if (semaphore == null) {
                semaphore = new Semaphore(3);
                controller.put(targetInfo, semaphore);
            }
            semaphore = controller.get(targetInfo);
            return semaphore;
        }
    }

    public static ConnectionPool getInstance() {
        return ConnectionPoolHolder.INSTANCE;
    }

    private static class ConnectionPoolHolder {
        public static ConnectionPool INSTANCE = new ConnectionPool();
    }
}
