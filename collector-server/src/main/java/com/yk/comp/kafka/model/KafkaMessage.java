package com.yk.comp.kafka.model;

public class KafkaMessage<T>
{
    private T message;

    public T getMessage()
    {
        return message;
    }

    public void setMessage(T message)
    {
        this.message = message;
    }
}
