package com.yk.es.normal;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.composite.CompositeAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.composite.CompositeValuesSourceBuilder;
import org.elasticsearch.search.aggregations.bucket.composite.ParsedComposite;
import org.elasticsearch.search.aggregations.bucket.composite.TermsValuesSourceBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.metrics.tophits.ParsedTopHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AggregationTest extends com.yk.es.normal.AbstractService
{
    Map<String, String[]> parameters = new HashMap<>();
    
    @Override
    public void parameters()
    {
        super.index = "matching_result_a89ff75dd9a54876ae92cf571f770a52";
        super.type = "_doc";
        
        
        parameters.put("jobName", Arrays.asList("a89ff75dd9a54876ae92cf571f770a52").toArray(new String[0]));
        parameters.put("orgName", Arrays.asList("").toArray(new String[0]));
        
        parameters.put("ip", Arrays.asList("").toArray(new String[0]));
        parameters.put("detectDateTs_beginTime", Arrays.asList("").toArray(new String[0]));
        parameters.put("pageSize", Arrays.asList("10").toArray(new String[0]));
        parameters.put("secretRate", Arrays.asList("").toArray(new String[0]));
        parameters.put("sort", new String[]{null});
        parameters.put("current", Arrays.asList("1").toArray(new String[0]));
        parameters.put("size", Arrays.asList("10").toArray(new String[0]));
        parameters.put("falsePositive", Arrays.asList("0").toArray(new String[0]));
        parameters.put("keywordOption", Arrays.asList("0").toArray(new String[0]));
        parameters.put("ruleName", Arrays.asList("").toArray(new String[0]));
        parameters.put("breachContent", Arrays.asList("").toArray(new String[0]));
        parameters.put("keyword", Arrays.asList("").toArray(new String[0]));
        parameters.put("totalMatches", Arrays.asList("").toArray(new String[0]));
        parameters.put("currentPage", Arrays.asList("1").toArray(new String[0]));
        parameters.put("matchPos", Arrays.asList("").toArray(new String[0]));
        parameters.put("fileType", Arrays.asList("").toArray(new String[0]));
        parameters.put("detectDateTs_endTime", Arrays.asList("").toArray(new String[0]));
        parameters.put("order", new String[]{null});
    }
    
    /**
     * GET matching_result_2343bae5b8e7497083bc6c3e81885bff/_doc/_search
     * {
     *   "size": 0,
     *   "aggs": {
     *     "my_buckets": {
     *       "composite": {
     *         "sources": [
     *           {
     *             "breachContent": {
     *               "terms": {
     *                 "field": "breachContent.keyword"
     *               }
     *             }
     *           },
     *           {
     *             "matchContent": {
     *               "terms": {
     *                 "field": "matchContent.keyword"
     *               }
     *             }
     *           }
     *         ]
     *       },
     *       "aggs": {
     *         "yyy": {
     *           "top_hits": {
     *             "sort": [
     *               {
     *                 "detectDateTs": {
     *                   "order": "desc"
     *                 }
     *               }
     *             ],
     *             "_source": {
     *               "includes": [
     *                 "_id",
     *                 "srcFilepath"
     *               ]
     *             },
     *             "size": 2
     *           }
     *         },
     *         "filename":{
     *           "terms": {
     *             "field": "filename.keyword",
     *             "size": 10
     *           }
     *         }
     *       }
     *     }
     *   },
     *   "query": {
     *     "wildcard": {
     *       "breachContent.keyword": "*"
     *     }
     *   }
     * }
     *
     *  多桶聚合
     *
     */
    @Test
    public void testComposite() throws IOException
    {
        System.out.println();

        /*List<QueryBuilder> queryBuilderList = new ArrayList<>();
        // 模糊匹配
        queryBuilderList.add(QueryBuilders.wildcardQuery("" + ".keyword", "*" + "" + "*"));
        // 精确匹配
        queryBuilderList.add(QueryBuilders.termQuery("", ""));
        // 范围查询
        queryBuilderList.add(QueryBuilders.rangeQuery(""));
        queryBuilderList.add(QueryBuilders.matchQuery("", ""));
        queryBuilderList.add(QueryBuilders.boolQuery());*/

        List<QueryBuilder> queryBuilders = new ArrayList<>();
        queryBuilders.add(QueryBuilders.termQuery("taskId.keyword", "38077361-b142-407c-b542-efa139484c1b"));
        queryBuilders.add(QueryBuilders.termQuery("taskId.keyword", "473b0b61-2d58-4c99-bab0-00e8b9359a20"));

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        for (QueryBuilder queryBuilder : queryBuilders)
        {
            boolQueryBuilder.should(queryBuilder);
        }

//        boolQueryBuilder.must(QueryBuilders.wildcardQuery("breachContent.keyword", "*"));

        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(index);
        searchRequest.types(type);
        
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(boolQueryBuilder);// 查询条件

        List<CompositeValuesSourceBuilder<?>> sources = new ArrayList<>();
        TermsValuesSourceBuilder breachContent = new TermsValuesSourceBuilder("breachContent").field("breachContent.keyword").missingBucket(true); // 6.4 以后的es才支持 missingBucket
        TermsValuesSourceBuilder matchContent = new TermsValuesSourceBuilder("matchContent").field("matchContent.keyword").missingBucket(true);
        sources.add(breachContent);

        CompositeAggregationBuilder composite = new CompositeAggregationBuilder("my_buckets", sources);
        String[] include = {"taskId", "breachContent", "matchContent"};
        String[] exclude = {};
        composite.subAggregation(
                AggregationBuilders.topHits("my_top_hits_name").size(1)
                        .fetchSource(include, exclude)
                        .sort(SortBuilders.fieldSort("detectDateTs").order(SortOrder.DESC))
                        .sort(SortBuilders.fieldSort("_id").order(SortOrder.DESC)));
//        composite.subAggregation(AggregationBuilders.terms("filename").field("filename.keyword"));
        composite.size(10);
        searchSourceBuilder.aggregation(composite);
        
        searchRequest.source(searchSourceBuilder);
        SearchResponse response = rhlClient.search(searchRequest, RequestOptions.DEFAULT);
        SearchHits searchHits = response.getHits();
        Aggregations aggregations = response.getAggregations();
        ParsedComposite my_buckets = aggregations.get("my_buckets");
        List<ParsedComposite.ParsedBucket> buckets = my_buckets.getBuckets();
        for (ParsedComposite.ParsedBucket parsedBucket : buckets)
        {
            Map<String, Object> keys = parsedBucket.getKey();
            long docCount = parsedBucket.getDocCount();
            
            ParsedStringTerms stringTerms = parsedBucket.getAggregations().get("filename");
            Object obj = stringTerms.getBuckets().get(0).getKey();
            
            ParsedTopHits topHits = parsedBucket.getAggregations().get("my_top_hits_name");
            SearchHit[] sh = topHits.getHits().getHits();
            
            
            System.out.println();
        }
        
        SearchHit[] hits = searchHits.getHits();
        Map<String, Object> map = hits[4].getSourceAsMap();
        System.out.println(map);
    }
}
