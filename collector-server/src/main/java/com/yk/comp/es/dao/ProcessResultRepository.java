package com.yk.comp.es.dao;

import com.yk.comp.es.model.ProcessResult;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProcessResultRepository extends ElasticsearchRepository<ProcessResult, String>
{
}