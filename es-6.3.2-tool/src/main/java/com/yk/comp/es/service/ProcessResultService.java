package com.yk.comp.es.service;

import com.yk.comp.es.dao.ProcessResultRepository;
import com.yk.comp.es.model.ProcessResult;
import com.yk.comp.es.model.SearchModel;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.index.get.GetResult;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.data.elasticsearch.core.query.UpdateQuery;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ProcessResultService
{
    // 线程池中的线程副本可能会有线程安全问题, 因为线程池中的线程是复用的
    public static final ThreadLocal<String> INDEX_DYNAMIC = new ThreadLocal<>();

    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Autowired
    private ProcessResultRepository processResultRepository;

    public long count()
    {
        return processResultRepository.count();
    }

    public ProcessResult save(ProcessResult processResult)
    {
        return processResultRepository.save(processResult);
    }

    public GetResult update(ProcessResult processResult)
    {
        UpdateResponse response = elasticsearchRestTemplate.update(new UpdateQuery());
        return response.getGetResult();
    }

    public void delete(ProcessResult processResult)
    {
        processResultRepository.delete(processResult);
//        processResultRepository.deleteById(processResult.getSkuId());
    }

    public Iterable<ProcessResult> getAll()
    {
        return processResultRepository.findAll();
    }

    public Optional<ProcessResult> getById(String id)
    {
        return processResultRepository.findById(id);
    }

    public List<ProcessResult> getByName(String name)
    {
        List<ProcessResult> list = new ArrayList<>();
        MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder("name", name);
        Iterable<ProcessResult> iterable = processResultRepository.search(matchQueryBuilder);
        iterable.forEach(list::add);
        return list;
    }

    public List<ProcessResult> getBy(ProcessResult processResult)
    {
        List<ProcessResult> list = new ArrayList<>();
        MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder("breachContent.keyword", processResult.getBreachContent());
        Iterable<ProcessResult> iterable = processResultRepository.search(matchQueryBuilder);
        iterable.forEach(list::add);
        return list;
    }

    public String save(ProcessResult processResult, String indexName)
    {
        IndexQuery barIdxQuery = new IndexQueryBuilder()
                .withIndexName(indexName)
                .withType("_doc")
                .withObject(processResult)
                .build();

        String result = elasticsearchRestTemplate.index(barIdxQuery);
        return result;
    }

    public List<ProcessResult> search(SearchModel searchModel)
    {
        QueryBuilder queryBuilder = new BoolQueryBuilder();
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withIndices(searchModel.getIndexName())
                .build();

        List<Map> list = null;
        try
        {
            Page<?> page = processResultRepository.search(searchQuery);
            list = elasticsearchRestTemplate.queryForList(searchQuery, Map.class);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public Page<ProcessResult> pageQuery(Integer pageNo, Integer pageSize, String kw)
    {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

        BoolQueryBuilder articleContent = boolQuery.should(QueryBuilders.termQuery("articleContent", "keyword"));
        BoolQueryBuilder articleName = boolQuery.should(QueryBuilders.termQuery("articleName", "keyword"));

        FieldSortBuilder order = SortBuilders.fieldSort("readCount").order(SortOrder.DESC);

        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();

        nativeSearchQueryBuilder.withQuery(articleContent);
        nativeSearchQueryBuilder.withQuery(articleName);
        nativeSearchQueryBuilder.withSort(order);

        NativeSearchQuery searchQuery = nativeSearchQueryBuilder.build();

        Page<ProcessResult> articles = processResultRepository.search(searchQuery);
        return articles;
    }

    public String getIndexNamePart()
    {
        String part = INDEX_DYNAMIC.get();
        String name = Thread.currentThread().getName();
        return part;
    }
}
