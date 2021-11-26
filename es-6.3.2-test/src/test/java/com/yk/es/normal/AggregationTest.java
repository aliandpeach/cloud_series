package com.yk.es.normal;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.script.Script;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.composite.CompositeAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.composite.CompositeValuesSourceBuilder;
import org.elasticsearch.search.aggregations.bucket.composite.ParsedComposite;
import org.elasticsearch.search.aggregations.bucket.composite.TermsValuesSourceBuilder;
import org.elasticsearch.search.aggregations.metrics.cardinality.CardinalityAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.cardinality.ParsedCardinality;
import org.elasticsearch.search.aggregations.metrics.tophits.ParsedTopHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AggregationTest extends com.yk.es.normal.AbstractService
{
    Map<String, String[]> parameters = new HashMap<>();

    @Override
    public void parameters()
    {
        super.index = "matching_result_0560ec07891f472980b78dcb2fa9a29c";
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

    @Test
    public void testQueryPage() throws Exception
    {
        // 假设总数为47条, if中的循环则是为了找到第46条, 进而确定 aggregateAfter
        int offset = 46;
        int limit = 10;
        Map<String, Object> after = null;
        if (offset > 0)
        {
            int step = 20;
            int count = offset % step > 0 ? offset / step + 1 : offset / step;
            for (int i = 1; i < count; i++)
            {
                int skip = i * step > offset ? offset % step : step;
                ResultPage result = testSearchAfterPageTaskId(skip, after);
                after = result.getAfter();
                if (offset >= result.getTotal())
                {
                    return;
                }
            }
        }
        ResultPage result = testSearchAfterPageTaskId(limit, after);
        System.out.println(result);
    }

    public ResultPage testSearchAfterPageTaskId(int limit, Map<String, Object> afterKey) throws Exception
    {
        ResultPage result = new ResultPage();
        List<QueryBuilder> queryBuilders = new ArrayList<>();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        for (QueryBuilder queryBuilder : queryBuilders)
        {
            boolQueryBuilder.should(queryBuilder);
        }

        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(index);
        searchRequest.types(type);

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(boolQueryBuilder);// 查询条件
        String[] names = new String[]{"taskId", "jobName", "status", "resourceType", "breachContent", "externalBreachContent", "sensitivityId", "secretRate", "matchContent", "ruleName", "filepath", "detectDateTs"};
        searchSourceBuilder.fetchSource(names, new String[]{});

        List<CompositeValuesSourceBuilder<?>> sources = new ArrayList<>();
        TermsValuesSourceBuilder breachContent = new TermsValuesSourceBuilder("taskId")
                .field("taskId.keyword")
                .order("desc");
        sources.add(breachContent);

        CompositeAggregationBuilder composite = new CompositeAggregationBuilder("my_buckets", sources);
        String[] exclude = {};

        if (null != afterKey)
        {
            composite.aggregateAfter(afterKey);
        }

        composite.subAggregation(
                AggregationBuilders.topHits("my_top_hits_name").size(100)
                        .fetchSource(names, exclude)
                        .sort(SortBuilders.fieldSort("detectDateTs").order(SortOrder.DESC))
                        .sort(SortBuilders.fieldSort("_id").order(SortOrder.DESC)));
        composite.size(limit);
        searchSourceBuilder.aggregation(composite);

        Script script = new Script("doc['taskId.keyword'].values");
        CardinalityAggregationBuilder cardinalityAggregationBuilder =
                AggregationBuilders.cardinality("task_id_group_count").script(script).precisionThreshold(40000);
        searchSourceBuilder.aggregation(cardinalityAggregationBuilder);

        searchRequest.source(searchSourceBuilder);
        SearchResponse response = rhlClient.search(searchRequest);
        SearchHits searchHits = response.getHits();
        SearchHit[] resultSearchHitAry = searchHits.getHits();

        Aggregations aggregations = response.getAggregations();
        ParsedComposite my_buckets = aggregations.get("my_buckets");
        afterKey = my_buckets.afterKey();
        result.setAfter(afterKey);

        ParsedCardinality eCountAggregator = aggregations.get("task_id_group_count");
        long total = eCountAggregator.getValue();
        result.setTotal(total);

        List<ParsedComposite.ParsedBucket> buckets = my_buckets.getBuckets();
        List<List<Map<String, Object>>> data = new ArrayList<>();
        for (ParsedComposite.ParsedBucket parsedBucket : buckets)
        {
            Map<String, Object> keys = parsedBucket.getKey();

            Aggregations aggs = parsedBucket.getAggregations();
            Aggregation aggregation = aggs.get("my_top_hits_name");

            List<Map<String, Object>> taskIdAggs = new ArrayList<>();
            if (aggregation instanceof ParsedTopHits)
            {
                SearchHits searchHitsAgg = ((ParsedTopHits) aggregation).getHits();
                SearchHit[] aggHitAry = searchHitsAgg.getHits();

                for (SearchHit h : aggHitAry)
                {
                    taskIdAggs.add(h.getSourceAsMap());
                }
            }
            data.add(taskIdAggs);
        }
        result.setData(data);
        return result;
    }

    @Test
    public void testSearchAfterPage() throws Exception
    {
        int currentPage = 1;
        int pageSize = 1;
        Map<String, Object> afterKey = null;

        List<QueryBuilder> queryBuilders = new ArrayList<>();
//        queryBuilders.add(QueryBuilders.termQuery("taskId.keyword", "38077361-b142-407c-b542-efa139484c1b"));
//        queryBuilders.add(QueryBuilders.termQuery("taskId.keyword", "473b0b61-2d58-4c99-bab0-00e8b9359a20"));

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        for (QueryBuilder queryBuilder : queryBuilders)
        {
            boolQueryBuilder.should(queryBuilder); // or
        }

//        boolQueryBuilder.must(QueryBuilders.wildcardQuery("breachContent.keyword", "*"));

        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(index);
        searchRequest.types(type);

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(boolQueryBuilder);// 查询条件
        searchSourceBuilder.fetchSource(new String[]{"taskId", "breachContent", "matchContent"}, new String[]{});

        List<CompositeValuesSourceBuilder<?>> sources = new ArrayList<>();
        TermsValuesSourceBuilder breachContent = new TermsValuesSourceBuilder("taskId").field("taskId.keyword").order("desc"); // .missingBucket(true); // 6.4 以后的es才支持 missingBucket
        sources.add(breachContent);

        CompositeAggregationBuilder composite = new CompositeAggregationBuilder("my_buckets", sources);
        String[] include = {"taskId", "breachContent", "matchContent"};
        String[] exclude = {};

//        composite.aggregateAfter(new HashMap<>()); // afterKey

        composite.subAggregation(
                AggregationBuilders.topHits("my_top_hits_name").size(100)
                        .fetchSource(include, exclude)
                        .sort(SortBuilders.fieldSort("detectDateTs").order(SortOrder.DESC))
                        .sort(SortBuilders.fieldSort("_id").order(SortOrder.DESC)));
//        composite.subAggregation(AggregationBuilders.terms("filename").field("filename.keyword"));
        composite.size(pageSize);// buckets size
        searchSourceBuilder.aggregation(composite);

        Script script = new Script("doc['taskId.keyword'].values");
        CardinalityAggregationBuilder cardinalityAggregationBuilder =
                AggregationBuilders.cardinality("task_id_group_count").script(script).precisionThreshold(40000);
        searchSourceBuilder.aggregation(cardinalityAggregationBuilder);

        searchRequest.source(searchSourceBuilder);
        SearchResponse response = rhlClient.search(searchRequest);
        SearchHits searchHits = response.getHits();
        SearchHit[] resultSearchHitAry = searchHits.getHits();
        for (SearchHit hit : resultSearchHitAry)
        {
            Map<String, Object> hitObj = hit.getSourceAsMap();
//            System.out.println(hitObj);
        }

        Aggregations aggregations = response.getAggregations();
        ParsedComposite my_buckets = aggregations.get("my_buckets");
        Map<String, Object> _afterKey = my_buckets.afterKey();

        List<ParsedComposite.ParsedBucket> buckets = my_buckets.getBuckets();
        for (ParsedComposite.ParsedBucket parsedBucket : buckets)
        {
            Map<String, Object> keys = parsedBucket.getKey();
            System.out.println(keys);
            long docCount = parsedBucket.getDocCount();

            Aggregations aggs = parsedBucket.getAggregations();
            Aggregation aggregation = aggs.get("my_top_hits_name");

            if (aggregation instanceof ParsedTopHits)
            {
                SearchHits searchHitsAgg = ((ParsedTopHits) aggregation).getHits();
                SearchHit[] aggHitAry = searchHitsAgg.getHits();
                for (SearchHit h : aggHitAry)
                {
                    System.out.println(h.getSourceAsMap());
                }
            }
            System.out.println();
        }
    }

    /**
     * GET /matching_result_a89ff75dd9a54876ae92cf571f770a52/_doc/_search
     * {"query":{"bool":{"should":[{"term":{"taskId.keyword":{"value":"38077361-b142-407c-b542-efa139484c1b","boost":1.0}}},{"term":{"taskId.keyword":{"value":"473b0b61-2d58-4c99-bab0-00e8b9359a20","boost":1.0}}}],"adjust_pure_negative":true,"boost":1.0}},"_source":{"includes":["taskId","breachContent","matchContent"],"excludes":[]},"aggregations":{"my_buckets":{"composite":{"size":10,"sources":[{"taskId":{"terms":{"field":"taskId.keyword","order":"asc"}}}]},"aggregations":{"my_top_hits_name":{"top_hits":{"from":0,"size":1,"version":false,"explain":false,"_source":{"includes":["taskId","breachContent","matchContent"],"excludes":[]},"sort":[{"detectDateTs":{"order":"desc"}},{"_id":{"order":"desc"}}]}}}}}}
     *
     *
     * 多桶聚合
     */
    @Test
    public void testCompositeAggregations() throws IOException
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
            boolQueryBuilder.should(queryBuilder); // or
        }

//        boolQueryBuilder.must(QueryBuilders.wildcardQuery("breachContent.keyword", "*"));

        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(index);
        searchRequest.types(type);

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(boolQueryBuilder);// 查询条件
        searchSourceBuilder.fetchSource(new String[]{"taskId", "breachContent", "matchContent"}, new String[]{});

        List<CompositeValuesSourceBuilder<?>> sources = new ArrayList<>();
        TermsValuesSourceBuilder breachContent = new TermsValuesSourceBuilder("taskId").field("taskId.keyword"); // .missingBucket(true); // 6.4 以后的es才支持 missingBucket
        sources.add(breachContent);

        CompositeAggregationBuilder composite = new CompositeAggregationBuilder("my_buckets", sources);
        String[] include = {"taskId", "breachContent", "matchContent"};
        String[] exclude = {};
        composite.subAggregation(
                AggregationBuilders.topHits("my_top_hits_name").size(100)
                        .fetchSource(include, exclude)
                        .sort(SortBuilders.fieldSort("detectDateTs").order(SortOrder.DESC))
                        .sort(SortBuilders.fieldSort("_id").order(SortOrder.DESC)));
//        composite.subAggregation(AggregationBuilders.terms("filename").field("filename.keyword"));
        composite.size(10);// buckets size
        searchSourceBuilder.aggregation(composite);

        searchRequest.source(searchSourceBuilder);
        SearchResponse response = rhlClient.search(searchRequest);
        SearchHits searchHits = response.getHits();
        SearchHit[] resultSearchHitAry = searchHits.getHits();
        for (SearchHit hit : resultSearchHitAry)
        {
            Map<String, Object> hitObj = hit.getSourceAsMap();
//            System.out.println(hitObj);
        }

        Aggregations aggregations = response.getAggregations();
        ParsedComposite my_buckets = aggregations.get("my_buckets");
        List<ParsedComposite.ParsedBucket> buckets = my_buckets.getBuckets();
        for (ParsedComposite.ParsedBucket parsedBucket : buckets)
        {
            Map<String, Object> keys = parsedBucket.getKey();
            System.out.println(keys);
            long docCount = parsedBucket.getDocCount();

            Aggregations aggs = parsedBucket.getAggregations();
            Aggregation aggregation = aggs.get("my_top_hits_name");

            if (aggregation instanceof ParsedTopHits)
            {
                SearchHits searchHitsAgg = ((ParsedTopHits) aggregation).getHits();
                SearchHit[] aggHitAry = searchHitsAgg.getHits();
                for (SearchHit h : aggHitAry)
                {
                    System.out.println(h.getSourceAsMap());
                }
            }
            System.out.println();
        }
    }

    /**
     * GET /matching_result_a89ff75dd9a54876ae92cf571f770a52/_doc/_search
     * {"query":{"bool":{"should":[{"term":{"taskId.keyword":{"value":"38077361-b142-407c-b542-efa139484c1b","boost":1.0}}},{"term":{"taskId.keyword":{"value":"473b0b61-2d58-4c99-bab0-00e8b9359a20","boost":1.0}}}],"adjust_pure_negative":true,"boost":1.0}},"_source":{"includes":["taskId","breachContent","matchContent"],"excludes":[]},"aggregations":{"taskId":{"terms":{"field":"taskId.keyword","size":10,"min_doc_count":1,"shard_min_doc_count":0,"show_term_doc_count_error":false,"order":[{"_count":"desc"},{"_key":"asc"}]},"aggregations":{"my_top_hits_name":{"top_hits":{"from":0,"size":100,"version":false,"explain":false,"_source":{"includes":["taskId","breachContent","matchContent"],"excludes":[]},"sort":[{"detectDateTs":{"order":"desc"}},{"_id":{"order":"desc"}}]}}}}}}
     */
    @Test
    public void testAggregations() throws IOException
    {
        List<QueryBuilder> queryBuilders = new ArrayList<>();
//        queryBuilders.add(QueryBuilders.termQuery("taskId.keyword", "38077361-b142-407c-b542-efa139484c1b"));
//        queryBuilders.add(QueryBuilders.termQuery("taskId.keyword", "473b0b61-2d58-4c99-bab0-00e8b9359a20"));

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        for (QueryBuilder queryBuilder : queryBuilders)
        {
            boolQueryBuilder.should(queryBuilder); // or
        }

//        boolQueryBuilder.must(QueryBuilders.wildcardQuery("breachContent.keyword", "*"));

        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(index);
        searchRequest.types(type);

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(boolQueryBuilder);// 查询条件
        searchSourceBuilder.fetchSource(new String[]{"taskId", "breachContent", "matchContent"}, new String[]{});

        AggregationBuilder taskIdTermsBuilder = AggregationBuilders.terms("taskId").field("taskId.keyword");
//        AggregationBuilder otherBuilder = AggregationBuilders.terms("breachContent").field("breachContent.keyword");
//        taskIdTermsBuilder.subAggregation(otherBuilder);

        taskIdTermsBuilder.subAggregation(AggregationBuilders.topHits("my_top_hits_name").size(100)
                .fetchSource(new String[]{"taskId", "breachContent", "matchContent"}, new String[]{})
                .sort(SortBuilders.fieldSort("detectDateTs").order(SortOrder.DESC))
                .sort(SortBuilders.fieldSort("_id").order(SortOrder.DESC)));
        searchSourceBuilder.aggregation(taskIdTermsBuilder);

        Script script = new Script("doc['taskId.keyword'].values");
        CardinalityAggregationBuilder cardinalityAggregationBuilder =
                AggregationBuilders.cardinality("task_id_group_count").script(script).precisionThreshold(40000);
        searchSourceBuilder.aggregation(cardinalityAggregationBuilder);

        searchRequest.source(searchSourceBuilder);
        SearchResponse response = rhlClient.search(searchRequest);
        SearchHits searchHits = response.getHits();
        SearchHit[] resultSearchHitAry = searchHits.getHits();
        for (SearchHit hit : resultSearchHitAry)
        {
            Map<String, Object> hitObj = hit.getSourceAsMap();
            System.out.println(hitObj);
        }

        Aggregations aggregations = response.getAggregations();
        System.out.println();
    }

    @Test
    public void testTermsAggregations() throws IOException
    {
        List<QueryBuilder> queryBuilders = new ArrayList<>();
//        queryBuilders.add(QueryBuilders.termQuery("taskId.keyword", "38077361-b142-407c-b542-efa139484c1b"));
//        queryBuilders.add(QueryBuilders.termQuery("taskId.keyword", "473b0b61-2d58-4c99-bab0-00e8b9359a20"));

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        for (QueryBuilder queryBuilder : queryBuilders)
        {
            boolQueryBuilder.should(queryBuilder); // or
        }

//        boolQueryBuilder.must(QueryBuilders.wildcardQuery("breachContent.keyword", "*"));

        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(index);
        searchRequest.types(type);

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(boolQueryBuilder);// 查询条件
        searchSourceBuilder.fetchSource(new String[]{"taskId", "breachContent", "matchContent"}, new String[]{});

        AggregationBuilder taskIdTermsBuilder = AggregationBuilders.terms("taskId").field("taskId.keyword");
        AggregationBuilder otherBuilder = AggregationBuilders.terms("breachContent").field("breachContent.keyword");
        taskIdTermsBuilder.subAggregation(otherBuilder);

        searchSourceBuilder.aggregation(taskIdTermsBuilder);

        searchRequest.source(searchSourceBuilder);
        SearchResponse response = rhlClient.search(searchRequest);
        SearchHits searchHits = response.getHits();
        SearchHit[] resultSearchHitAry = searchHits.getHits();
        for (SearchHit hit : resultSearchHitAry)
        {
            Map<String, Object> hitObj = hit.getSourceAsMap();
            System.out.println(hitObj);
        }

        Aggregations aggregations = response.getAggregations();
        System.out.println();
    }

    public static class ResultPage
    {
        private long total;

        private Map<String, Object> after;

        private List<List<Map<String, Object>>> data;

        public List<List<Map<String, Object>>> getData()
        {
            return data;
        }

        public void setData(List<List<Map<String, Object>>> data)
        {
            this.data = data;
        }

        public long getTotal()
        {
            return total;
        }

        public void setTotal(long total)
        {
            this.total = total;
        }

        public Map<String, Object> getAfter()
        {
            return after;
        }

        public void setAfter(Map<String, Object> after)
        {
            this.after = after;
        }
    }
}
