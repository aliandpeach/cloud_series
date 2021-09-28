package com.yk.es.normal;

import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.Requests;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.IntStream;

/**
 * InsertService
 */

public class DeleteServiceTest extends AbstractService
{
    @Override
    public void parameters()
    {
        super.index = "technology";
        super.type = "key";
    }

    /**
     * 删除索引
     */
    @Test
    public void delete() throws InterruptedException
    {
        List<CompletableFuture<Void>> futureList = new ArrayList<>();
        IntStream.range(0, 12).forEach(_index ->
        {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() ->
            {
                try
                {
                    DeleteRequest deleteRequest = Requests.deleteRequest(index).index(index).type(type);
                    rhlClient.delete(deleteRequest, RequestOptions.DEFAULT);
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            });
            futureList.add(future);
        });

        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[0])).join();
        Thread.currentThread().join();
    }

    /**
     * 删除索引下的某条数据
     */
    @Test
    public void deleteData() throws InterruptedException
    {
        List<CompletableFuture<Void>> futureList = new ArrayList<>();
        IntStream.range(0, 12).forEach(_index ->
        {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() ->
            {
                try
                {
                    DeleteRequest deleteRequest = Requests.deleteRequest(index).index(index).type(type);
                    rhlClient.delete(deleteRequest, RequestOptions.DEFAULT);
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            });
            futureList.add(future);
        });

        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[0])).join();
        Thread.currentThread().join();
    }
}
