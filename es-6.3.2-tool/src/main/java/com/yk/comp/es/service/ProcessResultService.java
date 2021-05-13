package com.yk.comp.es.service;

import com.yk.comp.es.dao.ProcessResultRepository;
import com.yk.comp.es.model.ProcessResult;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProcessResultService
{

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
}
