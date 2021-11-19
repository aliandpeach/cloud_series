package com.yk.es.normal;

import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequest;
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
        super.index = "matching_result_a89ff75dd9a54876ae92cf571f770a52";
        super.type = "_doc";
    }

    @Test
    public void testSearchAfterPage() throws Exception
    {
        int pageSize = 10;
        int currentPage = 1;
        Object[] afterKey = null;

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        SearchScrollRequest searchScrollRequest = new SearchScrollRequest(index);
        sourceBuilder.size(pageSize);
//        sourceBuilder.fetchSource(new String[]{"breachContent"}, new String[]{});

//        sourceBuilder.searchAfter(afterKey); // afterKey

//        MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery("taskId", "f0ec27d9-90b7-4f9e-a643-e75dc503460e");
        WildcardQueryBuilder wildcardQueryBuilder = QueryBuilders.wildcardQuery("taskId.keyword", "473b0b61-2d58-4c99-bab0-00e8b9359a20");
//        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("tag", "体育");
//        RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("detectDateTs");
//        rangeQueryBuilder.gte("1611022338000");
//        rangeQueryBuilder.lte("1631822338000");

        BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();
        boolBuilder.must(wildcardQueryBuilder);
//        boolBuilder.must(termQueryBuilder);
//        boolBuilder.must(rangeQueryBuilder);
//        boolBuilder.should(wildcardQueryBuilder);

        sourceBuilder.query(boolBuilder);
        String[] include = new String[]{};
        String[] excludes = new String[]{};
        sourceBuilder.fetchSource(include, excludes);
        SearchRequest searchRequest = new SearchRequest(index);
        searchRequest.types(type);
        searchRequest.source(sourceBuilder);
        try
        {
            SearchResponse response = rhlClient.search(searchRequest);
            SearchHits searchHits = response.getHits();
            SearchHit[] hits = searchHits.getHits();

            for (SearchHit h : hits)
            {
                System.out.println(h.getSourceAsMap());

                Object[] _afterKey = h.getSortValues();
            }
        }
        catch (IOException e)
        {
        }
    }

    /**
     * 多条件组合
     */
    @Test
    public void testSearch()
    {
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        SearchScrollRequest searchScrollRequest = new SearchScrollRequest(index);
        sourceBuilder.size(10);
//        sourceBuilder.fetchSource(new String[]{"breachContent"}, new String[]{});

//        MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery("taskId", "f0ec27d9-90b7-4f9e-a643-e75dc503460e");
        WildcardQueryBuilder wildcardQueryBuilder = QueryBuilders.wildcardQuery("taskId.keyword", "473b0b61-2d58-4c99-bab0-00e8b9359a20");
//        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("tag", "体育");
//        RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("detectDateTs");
//        rangeQueryBuilder.gte("1611022338000");
//        rangeQueryBuilder.lte("1631822338000");

        BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();
        boolBuilder.must(wildcardQueryBuilder);
//        boolBuilder.must(termQueryBuilder);
//        boolBuilder.must(rangeQueryBuilder);
//        boolBuilder.should(wildcardQueryBuilder);

        sourceBuilder.query(boolBuilder);
        String[] include = new String[]{};
        String[] excludes = new String[]{};
        sourceBuilder.fetchSource(include, excludes);
        SearchRequest searchRequest = new SearchRequest(index);
        searchRequest.types(type);
        searchRequest.source(sourceBuilder);
        try
        {
            SearchResponse response = rhlClient.search(searchRequest);
            SearchHits searchHits = response.getHits();
            SearchHit[] hits = searchHits.getHits();
            for (SearchHit h : hits)
            {
                System.out.println(h.getSourceAsMap());
                Object[] after = h.getSortValues();
            }
        }
        catch (IOException e)
        {
        }
    }

    @Test
    public void searchById() throws IOException
    {
        GetRequest getRequest = new GetRequest("matching_result_a89ff75dd9a54876ae92cf571f770a52", "_doc", "df8825a2f308389dbf2df01092712b0201");
        Map<String, Object> map = rhlClient.get(getRequest).getSourceAsMap();
        System.out.println(map);
    }
}
