package com.yk.comp.elastic.dao;

import com.yk.comp.elastic.model.ProcessResult;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProcessResultRepository extends ElasticsearchRepository<ProcessResult, String>
{
}