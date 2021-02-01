package com.yk.comp.kafka.model;

import java.util.List;

public class KafkaMessage<T> {
    private List<T> records;

    public void setRecords(List<T> records) {
        this.records = records;
    }
}
