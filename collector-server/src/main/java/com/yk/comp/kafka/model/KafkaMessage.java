package com.yk.comp.kafka.model;

import lombok.Data;

@Data
public class KafkaMessage<T>
{
    private T data;

    KafkaMessage(T data)
    {
        this.data = data;
    }

    public static <T> KafkaMessage<T> of(T data)
    {
        return new KafkaMessage<T>(data);
    }
}
