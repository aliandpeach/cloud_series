package com.yk.es.normal;

import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.Requests;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.IntStream;

/**
 * InsertService
 */

public class InsertServiceTest extends AbstractService
{
    @Override
    public void parameters()
    {
        super.index = "technology";
        super.type = "key";
    }

    @Test
    public void insertBulk() throws InterruptedException
    {
        List<CompletableFuture<Void>> futureList = new ArrayList<>();
        IntStream.range(0, 12).forEach(_index ->
        {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() ->
            {
                try
                {
                    BulkRequest bulkRequest = new BulkRequest();
                    IndexRequest indexRequest = Requests.indexRequest().index(index).type(type);
                    indexRequest.source(new HashMap<>(Collections.singletonMap("1", UUID.randomUUID().toString())));
                    bulkRequest.add(indexRequest);
//                  rhlClient.index(indexRequest, RequestOptions.DEFAULT);
                    bulkRequest.setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE);
                    rhlClient.bulk(bulkRequest, RequestOptions.DEFAULT);
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            });
            futureList.add(future);
        });

        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[0])).join();
    }

    @Test
    public void insert() throws InterruptedException
    {
        try
        {
            IndexRequest indexRequest = Requests.indexRequest().index(index).type(type);
            String value = UUID.randomUUID().toString();
            System.out.println(value);
            indexRequest.source(new HashMap<>(Collections.singletonMap("1", value)));
            rhlClient.index(indexRequest, RequestOptions.DEFAULT);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
