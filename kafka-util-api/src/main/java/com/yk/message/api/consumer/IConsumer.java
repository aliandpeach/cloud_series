package com.yk.message.api.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface IConsumer<K, V>
{
    void subscribe(List<String> topics);
    
    List<ConsumerRecords<K, V>> poll(int mills);
    
    void commit();
    
    void commitSync(Map<TopicPartition, OffsetAndMetadata> partitionMap);
    
    void asyncCommit();
    
    void seek(TopicPartition topicPartition, long mills);
    
    void seedToEnd(List<TopicPartition> partitions);
    
    void pause(Collection<TopicPartition> partitions);
    
    void resume(Collection<TopicPartition> partitions);
    
    boolean topicExit(String topic);
    
    void close();
}
