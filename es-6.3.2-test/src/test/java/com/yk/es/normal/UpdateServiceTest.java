package com.yk.es.normal;

import org.elasticsearch.action.update.UpdateRequest;
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
    public void updateDataById() throws IOException
    {
        UpdateRequest request = new UpdateRequest(index, "_doc", "id");
        request.doc(new HashMap<>());
        rhlClient.update(request);
    }
}
