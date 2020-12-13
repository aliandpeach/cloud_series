package com.yk.message.api.consumer;

import org.apache.kafka.common.TopicPartition;

import java.util.Collection;

public interface IConsumerRebalanceListener {
    void onPartitionRevoked(Collection<TopicPartition> partitions);

    void onPartitionAssigned(Collection<TopicPartition> partitions);
}
