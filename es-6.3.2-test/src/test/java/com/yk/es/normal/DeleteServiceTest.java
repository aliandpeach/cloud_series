package com.yk.es.normal;

import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.client.Requests;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.elasticsearch.search.builder.SearchSourceBuilder;
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
     * 删除索引以及数据
     */
    @Test
    public void deleteByIndex() throws InterruptedException
    {
        try
        {
            DeleteIndexRequest deleteIndexRequest = Requests.deleteIndexRequest(index);
            rhlClient.indices().delete(deleteIndexRequest);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }

    /**
     * 删除索引下的某条数据
     */
    @Test
    public void deleteDataById() throws InterruptedException
    {
        try
        {
            DeleteRequest deleteRequest = Requests.deleteRequest(index).index(index).type(type);
            deleteRequest.id("pUlN43sBe8WmUdUPSkbB");
            rhlClient.delete(deleteRequest);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 删除索引下的某条数据
     */
    @Test
    public void deleteDataByQuery() throws InterruptedException
    {
        BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();
        boolBuilder.must(QueryBuilders.termQuery("1.keyword", ""));

        SearchSourceBuilder ssb = new SearchSourceBuilder();
        ssb.query(boolBuilder);

        SearchRequest searchRequest = new SearchRequest(index);
        searchRequest.types(type);
        searchRequest.source(ssb);

        DeleteByQueryRequest deleteByQueryRequest = new DeleteByQueryRequest(searchRequest);
//            rhlClient.deleteByQueryAsync(deleteByQueryRequest);
    }
}
