package kafka;

import com.yk.message.api.producer.IProducer;
import com.yk.message.impl.factory.ProducerFactory;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ProducerImplTest
{
    
    private static ExecutorService executor = Executors.newFixedThreadPool(10);
    
    @Test
    public void test() throws ExecutionException, InterruptedException
    {
        Map<String, Object> params = new HashMap<>();
        IProducer<String, String> producer = new ProducerFactory<String, String>().getProducer(params);
        List<Future<RecordMetadata>> futureList = producer.send(new ProducerRecord<String, String>("testz", "test1-" + new Random().nextDouble()),
                new Callback()
                {
                    @Override
                    public void onCompletion(RecordMetadata metadata, Exception exception)
                    {
                        System.out.println("The offset of the record we just sent is: " + metadata.partition() + "|" + metadata.offset());
                    }
                }
        );
        
        futureList = producer.send(new ProducerRecord<String, String>("testz", "test2-" + new Random().nextDouble()),
                new Callback()
                {
                    @Override
                    public void onCompletion(RecordMetadata metadata, Exception exception)
                    {
                        System.out.println("The offset of the record we just sent is: " + metadata.partition() + "|" + metadata.offset());
                    }
                }
        );
        // producer.close(); // 必须调用close 否则需要给足够的时间到当前主线程，或者调用future.get(),确保已经消息已经发送出去
        
        
        List<CompletableFuture<RecordMetadata>> completableFutures = new ArrayList<>();
        Optional.ofNullable(futureList).ifPresent(futures -> futures.forEach(future ->
        {
            completableFutures.add(CompletableFuture.supplyAsync(() ->
            {
                try
                {
                    Thread.sleep(10000);
                    return future.get();
                }
                catch (InterruptedException | ExecutionException e)
                {
                    e.printStackTrace();
                    return null;
                }
            }, executor));
        }));
        // 等待所有 supplyAsync 线程执行完毕
        CompletableFuture.allOf(completableFutures.toArray(new CompletableFuture[0])).join();
        
        // CompletableFuture介绍
        // https://www.jianshu.com/p/fa7b86130a4d?from=timeline&isappinstalled=0
    }
}
