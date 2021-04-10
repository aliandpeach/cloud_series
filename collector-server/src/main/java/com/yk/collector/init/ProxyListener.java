package com.yk.collector.init;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class ProxyListener implements ServletContextListener {

    public void contextInitialized(ServletContextEvent sce) {
        System.out.println(1);
    }

    public void contextDestroyed(ServletContextEvent sce) {
    }
}
