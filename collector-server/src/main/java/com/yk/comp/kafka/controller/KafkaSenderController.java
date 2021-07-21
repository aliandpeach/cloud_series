package com.yk.comp.kafka.controller;

import com.yk.comp.kafka.producer.Producer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;

/**
 * 描述
 *
 * @author yangk
 * @version 1.0
 * @since 2021/07/20 16:13:50
 */
@RestController
@RequestMapping("/kafka/sender/")
public class KafkaSenderController
{
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private Producer producer;

    @RequestMapping("/send")
    public ResponseEntity<String> send() throws ExecutionException, InterruptedException
    {
        producer.asyncSend(1);
        return new ResponseEntity<>("OK", HttpStatus.OK);
    }
}
