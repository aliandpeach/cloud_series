package com.yk.comp.elasticsearch.controller;

import com.yk.comp.elasticsearch.model.MatchResult;
import com.yk.comp.elasticsearch.model.SearchModel;
import com.yk.comp.elasticsearch.service.ProcessResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/event")
public class MatchResultController
{
    @Autowired
    private ProcessResultService processResultService;

    @GetMapping
    @RequestMapping("/query/{id}/{index}")
    public Object queryById(@PathVariable("id") String id, @PathVariable("index") String index)
    {
        ProcessResultService.INDEX_DYNAMIC.set(index);
        return processResultService.getById(id).orElse(new MatchResult());
    }

    @PostMapping
    @RequestMapping("/query/by/{index}")
    public Object search(@RequestBody MatchResult processResult, @PathVariable("index") String index)
    {
        return processResultService.search(new SearchModel(index));
    }

    @PostMapping
    @RequestMapping("/save")
    public Object save(@RequestBody MatchResult matchResult)
    {
        return processResultService.save(matchResult);
    }

    @PostMapping
    @RequestMapping("/page")
    public Object page(@RequestBody MatchResult matchResult)
    {
        return null;
    }
}
