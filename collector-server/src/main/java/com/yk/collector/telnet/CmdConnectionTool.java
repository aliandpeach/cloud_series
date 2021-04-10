package com.yk.collector.telnet;

/**
 * cmd命令执行工具-使用TelnetConnector或者SshConnector
 */
public class CmdConnectionTool {

    private volatile boolean free;

    private TargetInfo targetInfo;

    public String cmdExecute(TargetInfo targetInfo, String cmds) {
        System.out.println("cmds" + cmds);
        return null;
    }

    /**
     * 多线程操作必须加锁
     *
     * @return
     */
    public synchronized boolean isFree() {
        return free;
    }

    /**
     * 多线程操作必须加锁
     *
     * @param free
     */
    public synchronized void setFree(boolean free) {
        this.free = free;
    }

    public synchronized TargetInfo getTargetInfo() {
        return targetInfo;
    }

    public synchronized void setTargetInfo(TargetInfo targetInfo) {
        this.targetInfo = targetInfo;
    }
}
