package com.yk.comp.kafka.producer;

import com.yk.comp.kafka.model.KafkaMessage;
import com.yk.comp.kafka.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

@Component
public class Producer {

    @Autowired
    private KafkaTemplate<Object, Object> kafkaTemplate;

    public SendResult asyncSend(Integer id) throws ExecutionException, InterruptedException {
        KafkaMessage<String> message = new KafkaMessage<String>();
        message.setRecords(Arrays.asList("1k", "2k", "3k"));
        // 同步发送消息
        return kafkaTemplate.send(Constants.TOPIC, message).get();
    }
}
