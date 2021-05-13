package com.yk.es.normal;

import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.Requests;
import org.junit.Test;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;

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
    public void insert()
    {
        IndexRequest indexRequest = Requests.indexRequest().index(index).type(type);
        indexRequest.source(new HashMap<>(Collections.singletonMap("1", "key1")));
        try
        {
            rhlClient.index(indexRequest, RequestOptions.DEFAULT);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
