package com.yk.comp.kafka.consumer;

import com.yk.comp.kafka.model.KafkaMessage;
import com.yk.comp.kafka.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
public class Consumer {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @KafkaListener(topics = Constants.TOPIC,
            groupId = "demo-consumer-group-" + Constants.TOPIC)
    public void onMessage(KafkaMessage message, Acknowledgment acknowledgment) {
        logger.info("[onMessage][线程编号:{} 消息内容：{}]", Thread.currentThread().getId(), message);
        acknowledgment.acknowledge();
    }
}
