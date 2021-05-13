package com.yk.es.normal;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.WildcardQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.Map;

public class SearchTest extends com.yk.es.normal.AbstractService
{
    @Override
    public void parameters()
    {
        super.index = "matching_result_661d2b9237bd4a4392061d9306f33bba";
        super.type = "_doc";
    }

    @Test
    public void test()
    {
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        SearchScrollRequest searchScrollRequest = new SearchScrollRequest(index);
        sourceBuilder.size(1);
//        sourceBuilder.fetchSource(new String[]{"breachContent"}, new String[]{});
//        MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery("taskId", "f0ec27d9-90b7-4f9e-a643-e75dc503460e");
        WildcardQueryBuilder wildcardQueryBuilder = QueryBuilders.wildcardQuery("taskId.keyword", "3df15c5a23ac4d95b6df582fbbcc0291");
//        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("tag", "体育");
//        RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("detectDateTs");
//        rangeQueryBuilder.gte("1611022338000");
//        rangeQueryBuilder.lte("1631822338000");
        BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();
        boolBuilder.must(wildcardQueryBuilder);
//        boolBuilder.must(termQueryBuilder);
//        boolBuilder.must(rangeQueryBuilder);
        sourceBuilder.query(boolBuilder);
        SearchRequest searchRequest = new SearchRequest(index);
        searchRequest.types(type);
        searchRequest.source(sourceBuilder);
        try
        {
            SearchResponse response = rhlClient.search(searchRequest, RequestOptions.DEFAULT);
            String scrollId = response.getScrollId();
            SearchHits searchHits = response.getHits();
            Aggregations aggregation = response.getAggregations();
            SearchHit[] hits = searchHits.getHits();
            Map<String, Object> list = hits[0].getSourceAsMap();
            System.out.println(response);
            Assert.assertEquals("", "");
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}
