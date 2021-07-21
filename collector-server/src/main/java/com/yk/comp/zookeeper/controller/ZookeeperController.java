package com.yk.comp.zookeeper.controller;

import com.yk.comp.zookeeper.util.ZkApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/zookeeper")
public class ZookeeperController
{
    @Autowired
    private ZkApi zkApi;

    @GetMapping(value = "/createNode")
    public boolean createNode(String path, String data)
    {
        log.debug("ZookeeperController create node {},{}", path, data);
        return zkApi.createNode(path, data);
    }

}
