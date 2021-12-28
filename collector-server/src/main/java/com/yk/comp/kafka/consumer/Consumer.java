package com.yk.comp.kafka.consumer;

import com.yk.comp.kafka.model.KafkaMessage;
import com.yk.comp.kafka.util.Constants;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class Consumer
{
    private Logger logger = LoggerFactory.getLogger(getClass());

    @KafkaListener(topics = Constants.TOPIC, groupId = Constants.GROUP_01)
    public void onMessage(List<String> message, Acknowledgment acknowledgment)
    {
        logger.info("[onMessage1][线程编号:{} 消息内容：{}]", Thread.currentThread().getId(), message);
        acknowledgment.acknowledge();
    }

    // 相同分组不会重复消费
    @KafkaListener(topics = Constants.TOPIC, groupId = Constants.GROUP_01)
    public void onMessage2(List<String> message, Acknowledgment acknowledgment)
    {
        logger.info("[onMessage2][线程编号:{} 消息内容：{}]", Thread.currentThread().getId(), message);
        acknowledgment.acknowledge();
    }

    // 重复消费需要不同的分组
    @KafkaListener(topics = Constants.TOPIC, groupId = Constants.GROUP_02)
    public void onMessage3(List<ConsumerRecord<String, String>> message, Acknowledgment acknowledgment)
    {
        logger.info("[onMessage3][线程编号:{} 消息内容：{}]", Thread.currentThread().getId(), message.stream().map(ConsumerRecord::value).collect(Collectors.toList()));
        acknowledgment.acknowledge();
    }
}
