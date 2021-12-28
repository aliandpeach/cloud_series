package com.yk.comp.kafka.producer;

import com.yk.comp.kafka.model.KafkaMessage;
import com.yk.comp.kafka.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.concurrent.ExecutionException;

@Component
public class Producer
{
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public String send(KafkaMessage<String> kafkaMessage, boolean async) throws ExecutionException, InterruptedException
    {
        //发送消息
        ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send(Constants.TOPIC, kafkaMessage.getData());
        if (async)
        {
            return "OK";
        }
        future.addCallback(new ListenableFutureCallback<SendResult<String, String>>()
        {
            @Override
            public void onFailure(Throwable throwable)
            {
                //发送失败的处理
                logger.info(Constants.TOPIC + " - 生产者 发送消息失败：" + throwable.getMessage());
            }

            @Override
            public void onSuccess(SendResult<String, String> stringObjectSendResult)
            {
                //成功的处理
                logger.info(Constants.TOPIC + " - 生产者 发送消息成功：" + stringObjectSendResult.toString());
            }
        });
        SendResult<String, String> sc = future.get();
        logger.info(Constants.TOPIC + " - 生产者 发送消息结果：" + sc);
        return sc.toString();
    }
}
