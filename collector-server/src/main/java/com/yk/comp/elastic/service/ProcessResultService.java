package com.yk.comp.elastic.service;

import com.yk.comp.elastic.dao.ProcessResultRepository;
import com.yk.comp.elastic.model.ProcessResult;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
        iterable.forEach(e -> list.add(e));
        return list;
    }

    public List<ProcessResult> getBy(ProcessResult processResult)
    {
        List<ProcessResult> list = new ArrayList<>();
        MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder("breachContent.keyword", processResult.getBreachContent());
        Iterable<ProcessResult> iterable = processResultRepository.search(matchQueryBuilder);
        iterable.forEach(e -> list.add(e));
        return list;
    }

    public Page<ProcessResult> pageQuery(Integer pageNo, Integer pageSize, String kw)
    {
        return null;
    }


}
