package com.yk.message.impl.producer;

import com.yk.message.api.producer.IProducer;
import com.yk.message.impl.util.ConfigUtil;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class ProducerImpl<K, V> implements IProducer<K, V>
{
    private Map<String, KafkaProducer<K, V>> kafkaProducerMap = new ConcurrentHashMap<>();
    
    private Properties props;
    
    public ProducerImpl(Map<String, Object> params)
    {
        props = ConfigUtil.producerConfigs();
    }
    
    public List<Future<RecordMetadata>> send(ProducerRecord<K, V> producerRecord)
    {
        KafkaProducer<K, V> kvKafkaProducer = kafkaProducerMap.get(producerRecord.topic());
        synchronized (this)
        {
            if (null == kvKafkaProducer)
            {
                kvKafkaProducer = new KafkaProducer<K, V>(props);
                kafkaProducerMap.put(producerRecord.topic(), kvKafkaProducer);
            }
        }
        return this.send(producerRecord, null);
    }
    
    public List<Future<RecordMetadata>> send(ProducerRecord<K, V> producerRecord, Callback callback)
    {
        KafkaProducer<K, V> kvKafkaProducer = kafkaProducerMap.get(producerRecord.topic());
        synchronized (this)
        {
            if (null == kvKafkaProducer)
            {
                kvKafkaProducer = new KafkaProducer<K, V>(props);
                kafkaProducerMap.put(producerRecord.topic(), kvKafkaProducer);
            }
        }
        
        List<Future<RecordMetadata>> metadataFutureList = kafkaProducerMap.entrySet()
                .stream().map(entry -> entry.getValue().send(producerRecord, callback)).collect(Collectors.toCollection(() -> new ArrayList<>()));
        return metadataFutureList;
    }
    
    public long lastOffset(TopicPartition partition)
    {
        return 0;
    }
    
    public void close()
    {
        kafkaProducerMap.entrySet()
                .stream().forEach(entry -> entry.getValue().close());
    }
}
