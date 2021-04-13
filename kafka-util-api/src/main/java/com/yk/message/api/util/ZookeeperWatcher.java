package com.yk.message.api.util;


import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;

public class ZookeeperWatcher implements Watcher
{
    private static Logger logger = LoggerFactory.getLogger(ZookeeperWatcher.class);
    
    // 集群连接地址
    private static final String CONNECT_ADDRESS = "127.0.0.1:2181";
    // 会话超时时间
    private static final int SESSION_TIME = 2000;
    
    private static final CountDownLatch COUNT_DOWN_LATCH = new CountDownLatch(1);
    
    private ZooKeeper zk;
    
    public void createConnection(String connectAddress, int sessionTimeOut)
    {
        try
        {
            zk = new ZooKeeper(connectAddress, sessionTimeOut, this);
            COUNT_DOWN_LATCH.await();
        }
        catch (Exception e)
        {
            logger.error("zookeeper create connection error", e);
        }
    }
    
    public boolean createPath(String path, String data)
    {
        try
        {
            this.exists(path, true);
            // 创建持久节点
            this.zk.create(path, data.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            logger.info("LOG" + "节点创建成功, Path:" + path + ",data:" + data);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    
    /**
     * 判断指定节点是否存在 不存在该节点返回null
     *
     * @param path 节点路径
     */
    public Stat exists(String path, boolean needWatch)
    {
        try
        {
            return this.zk.exists(path, needWatch);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }
    
    public boolean updateNode(String path, String data) throws KeeperException, InterruptedException
    {
        exists(path, true);
        this.zk.setData(path, data.getBytes(), -1);
        return false;
    }
    
    @Override
    public void process(WatchedEvent watchedEvent)
    {
        
        // 获取事件状态
        Event.KeeperState keeperState = watchedEvent.getState();
        // 获取事件类型
        Event.EventType eventType = watchedEvent.getType();
        // zk 路径
        String path = watchedEvent.getPath();
        // 判断是否建立连接
        if (Event.KeeperState.SyncConnected == keeperState)
        {
            // 如果当前状态已经连接上了 SyncConnected：连接，AuthFailed：认证失败,Expired:失效过期,
            // ConnectedReadOnly:连接只读,Disconnected:连接失败
            if (Event.EventType.None == eventType)
            {
                // 如果建立建立成功,让后程序往下走
                logger.info("LOG" + "zk 建立连接成功!");
                COUNT_DOWN_LATCH.countDown();
            }
            else if (Event.EventType.NodeCreated == eventType)
            {
                logger.info("LOG" + "事件通知,新增node节点" + path);
            }
            else if (Event.EventType.NodeDataChanged == eventType)
            {
                logger.info("LOG" + "事件通知,当前node节点" + path + "被修改....");
            }
            else if (Event.EventType.NodeDeleted == eventType)
            {
                logger.info("LOG" + "事件通知,当前node节点" + path + "被删除....");
            }
            
        }
    }
    
    public static void main(String[] args) throws KeeperException, InterruptedException
    {
        ZookeeperWatcher zkClientWatcher = new ZookeeperWatcher();
        zkClientWatcher.createConnection(CONNECT_ADDRESS, SESSION_TIME);
        zkClientWatcher.createPath("/createNode", "pa-644064");
        // 修改createNode节点
        zkClientWatcher.updateNode("/createNode", "7894561");
    }
}
