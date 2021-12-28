package com.yk.comp.es.controller;

import com.yk.comp.es.model.ProcessResult;
import com.yk.comp.es.model.SearchModel;
import com.yk.comp.es.service.ProcessResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/es/process/result")
public class ProcessResultController
{
    @Autowired
    private ProcessResultService processResultService;

    @GetMapping
    @RequestMapping("/query/{id}/{index}")
    public Object queryById(@PathVariable("id") String id, @PathVariable("index") String index)
    {
        ProcessResultService.INDEX_DYNAMIC.set(index);
        return processResultService.getById(id).orElse(new ProcessResult());
    }

    @PostMapping
    @RequestMapping("/query/by/{index}")
    public Object search(@RequestBody ProcessResult processResult, @PathVariable("index") String index)
    {
        return processResultService.search(new SearchModel(index));
    }

    @PostMapping
    @RequestMapping("/save")
    public Object save(@RequestBody ProcessResult processResult)
    {
        return processResultService.save(processResult);
    }

    @PostMapping
    @RequestMapping("/page")
    public Object page(@RequestBody ProcessResult processResult)
    {
        return null;
    }
}
