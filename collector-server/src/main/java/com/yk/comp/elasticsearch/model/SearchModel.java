package com.yk.comp.elasticsearch.model;

import lombok.Data;

/**
 * 描述
 *
 * @author yangk
 * @version 1.0
 * @since 2021/12/22 09:14:06
 */
@Data
public class SearchModel
{
    private String indexName;

    public SearchModel(String indexName)
    {
        this.indexName = indexName;
    }
}
