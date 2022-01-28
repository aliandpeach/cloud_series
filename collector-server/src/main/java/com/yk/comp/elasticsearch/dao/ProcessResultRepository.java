package com.yk.comp.elasticsearch.dao;

import com.yk.comp.elasticsearch.model.MatchResult;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProcessResultRepository extends ElasticsearchRepository<MatchResult, String>
{
}