package kafka;

import com.yk.message.api.consumer.IConsumer;
import com.yk.message.impl.factory.ConsumerFactory;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.KafkaAdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.BytesDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;

public class ConsumerImplTest
{
    @Test
    public void test()
    {
        Map<String, Object> params = new HashMap<>();
        params.put(ConsumerConfig.GROUP_ID_CONFIG, "0");
        IConsumer consumer = new ConsumerFactory<>().getConsumer(params);
        consumer.subscribe(Arrays.asList("testz"));
        
        while (true)
        {
            List<ConsumerRecords<String, String>> consumerRecords = consumer.poll(300);
            for (ConsumerRecords<String, String> list : consumerRecords)
            {
                if (list.isEmpty())
                {
                    continue;
                }
                for (ConsumerRecord<String, String> record : list)
                {
                    String topic = record.topic();
                    int partition = record.partition();
                    TopicPartition topicPartition = new TopicPartition(topic, partition);
                    List<CompletableFuture<Void>> cfs = new ArrayList<>();
                    try
                    {
                        consumer.pause(Collections.singleton(topicPartition));
                        List<ConsumerRecord<String, String>> partitionRecords = list.records(topicPartition);
                        
                        long lastOffset = partitionRecords.get(partitionRecords.size() - 1).offset();
                        long current = partitionRecords.get(0).offset();
                        for (ConsumerRecord<String, String> record1 : partitionRecords)
                        {
                            cfs.add(CompletableFuture.runAsync(() ->
                            {
                                System.out.println(record1.offset() + ": " + record1.value());
                            }));
                        }
                        CompletableFuture.allOf(cfs.toArray(new CompletableFuture[0])).join();
                        consumer.commitSync(Collections.singletonMap(topicPartition, new OffsetAndMetadata(lastOffset + 1)));
                    }
                    finally
                    {
                        consumer.resume(Collections.singleton(topicPartition));
                    }
                }

//                consumer.pause(list.partitions());
//
//                for (TopicPartition partition : list.partitions()) {
//                    List<ConsumerRecord<String, String>> partitionRecords = list.records(partition);
//                    // 数据处理
//                    for (ConsumerRecord<String, String> record : partitionRecords) {
//                        System.out.println(record.offset() + ": " + record.value());
//                        String topic = record.topic();
//                        int part = record.partition();
//                    }
//                    // 取得当前读取到的最后一条记录的offset
//                    long lastOffset = partitionRecords.get(partitionRecords.size() - 1).offset();
//                    // 提交offset，记得要 + 1
//                    consumer.commitSync(Collections.singletonMap(partition, new OffsetAndMetadata(lastOffset + 1)));
//                }
//                consumer.resume(list.partitions());
            }
        }
    }
    
    @Before
    public void createTopic()
    {
//        Properties props = new Properties();
//        props.put("bootstrap.servers", "192.168.31.211:9092");
//        props.put("acks", "all");
//        props.put("retries", 0);
//        props.put("batch.size", 16384);
//        props.put("linger.ms", 1);
//        props.put("key.serializer", StringSerializer.class.getName());
//        props.put("value.serializer", BytesDeserializer.class.getName());
//
//        AdminClient client = KafkaAdminClient.create(props);
//        NewTopic topic = new NewTopic("test1", 5, (short) 1);
//        client.createTopics(Arrays.asList(topic));
//        client.close();//关闭
    
    }
}
