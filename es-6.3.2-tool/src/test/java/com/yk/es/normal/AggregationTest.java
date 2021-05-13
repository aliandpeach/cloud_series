package com.yk.es.normal;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
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
        super.index = "matching_result_2343bae5b8e7497083bc6c3e81885bff";
        super.type = "_doc";
        
        
        parameters.put("jobName", Arrays.asList("2343bae5b8e7497083bc6c3e81885bff").toArray(new String[0]));
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
     */
    @Test
    public void test() throws IOException
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
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(index);
        searchRequest.types(type);
        
        SearchSourceBuilder ssb = new SearchSourceBuilder();
        // ssb.size(0);
        List<CompositeValuesSourceBuilder<?>> list = new ArrayList<>();
        list.add(new TermsValuesSourceBuilder("breachContent").field("breachContent.keyword"));
        list.add(new TermsValuesSourceBuilder("matchContent").field("matchContent.keyword"));
        CompositeAggregationBuilder composite = new CompositeAggregationBuilder("my_buckets", list);
        ssb.aggregation(composite);
        
        String[] source = {"_id", "srcFilepath"};
        String[] include = {};
        // topHit的意思是，group by之后，（按照时间降序） 排序后，每个分组的第一行数据
        composite.subAggregation(
                AggregationBuilders.topHits("yyy").size(1)
                        .fetchSource(source, include)
                        .sort(SortBuilders.fieldSort("detectDateTs").order(SortOrder.DESC)));
        
        composite.subAggregation(AggregationBuilders.terms("filename").field("filename.keyword"));
        composite.size(10);
        
        ssb.query(QueryBuilders.wildcardQuery("breachContent.keyword", "*"));
        
        searchRequest.source(ssb);
        SearchResponse response = rhlClient.search(searchRequest);
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
            
            ParsedTopHits topHits = parsedBucket.getAggregations().get("yyy");
            SearchHit[] sh = topHits.getHits().getHits();
            
            
            System.out.println();
        }
        
        SearchHit[] hits = searchHits.getHits();
        Map<String, Object> map = hits[4].getSourceAsMap();
        System.out.println(map);
    }
}
