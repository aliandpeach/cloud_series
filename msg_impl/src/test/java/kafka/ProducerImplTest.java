package kafka;

import com.yk.message.api.producer.IProducer;
import com.yk.message.impl.factory.ProducerFactory;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class ProducerImplTest {

    @Test
    public void test() throws ExecutionException, InterruptedException {
        Map<String, Object> params = new HashMap<>();
        IProducer<String, String> producer = new ProducerFactory<String, String>().getProducer(params);
        Future<RecordMetadata> futureList = producer.send(new ProducerRecord<String, String>("test", "test1-" + new Random().nextDouble()),
                new Callback() {
                    @Override
                    public void onCompletion(RecordMetadata metadata, Exception exception) {
                        System.out.println("The offset of the record we just sent is: " + metadata.partition() + "|" + metadata.offset());
                    }
                }
        );

        futureList = producer.send(new ProducerRecord<String, String>("test", "test2-" + new Random().nextDouble()),
                new Callback() {
                    @Override
                    public void onCompletion(RecordMetadata metadata, Exception exception) {
                        System.out.println("The offset of the record we just sent is: " + metadata.partition() + "|" + metadata.offset());
                    }
                }
        );
        producer.close(); // 必须调用close 否则需要给足够的时间到当前主线程，或者调用future.get(),确保已经消息已经发送出去
        /*List<CompletableFuture<Runnable>> completableFutures = new ArrayList<>();
        for (Future<RecordMetadata> future : futureList) {
            completableFutures.add(CompletableFuture.supplyAsync(() -> new Runnable(){
                @Override
                public void run() {
                    try {
                        Thread.sleep(10000);
                        future.get();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                }
            }));
        }
        CompletableFuture.allOf(new CompletableFuture[0]);*/
    }
}
