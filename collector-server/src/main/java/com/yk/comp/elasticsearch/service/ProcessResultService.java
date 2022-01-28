package com.yk.comp.elasticsearch.service;


import com.yk.comp.elasticsearch.dao.ProcessResultRepository;
import com.yk.comp.elasticsearch.model.MatchResult;
import com.yk.comp.elasticsearch.model.SearchModel;
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
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.UpdateQuery;
import org.springframework.data.elasticsearch.core.query.UpdateResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class ProcessResultService
{
    public static final ThreadLocal<String> INDEX_DYNAMIC = new ThreadLocal<>();

    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Autowired
    private ProcessResultRepository processResultRepository;

    public long count()
    {
        return processResultRepository.count();
    }

    public MatchResult save(MatchResult processResult)
    {
        return processResultRepository.save(processResult);
    }

    public UpdateResponse update(MatchResult matchResult, String indexName)
    {
        UpdateResponse response = elasticsearchRestTemplate.update(UpdateQuery.builder("").build(), IndexCoordinates.of(indexName));
        return response;
    }

    public void delete(MatchResult matchResult)
    {
        processResultRepository.delete(matchResult);
//        processResultRepository.deleteById(processResult.getSkuId());
    }

    public Iterable<MatchResult> getAll()
    {
        return processResultRepository.findAll();
    }

    public Optional<MatchResult> getById(String id)
    {
        return processResultRepository.findById(id);
    }

    public List<MatchResult> getByName(String name)
    {
        List<MatchResult> list = new ArrayList<>();
        MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder("name", name);
        Iterable<MatchResult> iterable = processResultRepository.search(matchQueryBuilder);
        iterable.forEach(list::add);
        return list;
    }

    public List<MatchResult> getBy(MatchResult processResult)
    {
        List<MatchResult> list = new ArrayList<>();
        MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder("breachContent.keyword", processResult.getBreachContent());
        Iterable<MatchResult> iterable = processResultRepository.search(matchQueryBuilder);
        iterable.forEach(list::add);
        return list;
    }

    public String save(MatchResult processResult, String indexName)
    {
        IndexQuery indexQuery = new IndexQueryBuilder()
                .withObject(processResult)
                .build();
        String result = elasticsearchRestTemplate.index(indexQuery, IndexCoordinates.of(indexName));
        return result;
    }

    public List<MatchResult> search(SearchModel searchModel)
    {
        QueryBuilder queryBuilder = new BoolQueryBuilder();
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();

        List<SearchHits<MatchResult>> list = null;
        try
        {
            List<SearchHits<MatchResult>> searchHits = elasticsearchRestTemplate.multiSearch(Collections.singletonList(nativeSearchQueryBuilder.build()), MatchResult.class, IndexCoordinates.of(searchModel.getIndexName()));
            searchHits.get(0).getSearchHits().get(0).getSortValues();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public Page<MatchResult> pageQuery(Integer pageNo, Integer pageSize, String kw)
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

        Page<MatchResult> articles = processResultRepository.search(searchQuery);
        return articles;
    }

    public String getIndexNamePart()
    {
        String part = INDEX_DYNAMIC.get();
        String name = Thread.currentThread().getName();
        return part;
    }
}
