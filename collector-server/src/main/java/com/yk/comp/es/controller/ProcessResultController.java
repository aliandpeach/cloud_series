package com.yk.comp.es.controller;

import com.yk.comp.es.model.ProcessResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/es/process/result")
public class ProcessResultController
{
    @GetMapping
    @RequestMapping("/query/{id}")
    public Object queryById(@PathVariable("id") String id)
    {
        return null;
    }

    @GetMapping
    @RequestMapping("/query/by")
    public Object queryById(@RequestBody ProcessResult processResult)
    {
        return null;
    }

    @GetMapping
    @RequestMapping("/save")
    public Object save(@RequestBody ProcessResult processResult)
    {
        return null;
    }

    @GetMapping
    @RequestMapping("/page")
    public Object page(@RequestBody ProcessResult processResult)
    {
        return null;
    }
}
