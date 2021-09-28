package com.yk.es.normal;

import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.IntStream;

/**
 * InsertService
 */

public class UpdateServiceTest extends AbstractService
{
    @Override
    public void parameters()
    {
        super.index = "technology";
        super.type = "key";
    }

    @Test
    public void updateData() throws InterruptedException
    {
        List<CompletableFuture<Void>> futureList = new ArrayList<>();
        IntStream.range(0, 12).forEach(_index ->
        {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() ->
            {
                try
                {
                    UpdateRequest request = new UpdateRequest(index, "_doc", "id");
                    request.doc(new HashMap<>());
                    rhlClient.update(request, RequestOptions.DEFAULT);
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
