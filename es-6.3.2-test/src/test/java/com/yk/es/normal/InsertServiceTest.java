package com.yk.es.normal;

import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.client.Requests;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.IntStream;

/**
 * InsertService
 */

public class InsertServiceTest extends AbstractService
{
    @Override
    public void parameters()
    {
        super.index = "technology";
        super.type = "_doc"; // 文档类型, 任意指定, 7版本以后固定为_doc
    }


    /**
     * 删除索引
     * DELETE technology
     *
     * 新增索引以及mapping信息
     * PUT /technology
     * {
     *   "mappings": {
     *     "_doc": {
     *         "properties": {
     *           "content": {
     *             "type": "text",
     *             "fields": {
     *               "keyword": {
     *                 "type": "keyword",
     *                 "ignore_above": 36
     *             }
     *           }
     *         }
     *       }
     *     }
     *   }
     * }
     *
     * GET /technology/_mapping
     * GET technology/_doc/_search
     * {
     * }
     * GET technology/_doc/_search
     * {
     *   "size": 0,
     *   "aggs": {
     *     "content": {
     *       "terms": {
     *         "field": "content.keyword"
     *       }
     *     }
     *   }
     * }
     *
     * 修改索引信息
     * PUT technology/_mappings/_doc
     * {
     *   "properties": {
     *     "content": {
     *       "type": "text",
     *       "fields": {
     *         "keyword": {
     *           "type": "keyword",
     *           "ignore_above": 35
     *         }
     *       }
     *     }
     *   }
     * }
     *
     *
     *
     *
     *
     *
     *
     * PUT _template/matching_result
     * {
     *         "index_patterns": "matching_result*",
     *         "order":0,
     *         "settings": {
     *             "index.number_of_replicas": "1",
     *             "index.number_of_shards": "5",
     *             "index.translog.durability":"async",
     *             "index.translog.sync_interval":"10s",
     *             "index.refresh_interval":"10s"
     *         },
     *         "mappings": {
     *            "_doc" : {
     *               "properties":{
     *                   "falsePositive":{
     *                        "type" : "text",
     *                        "fields": {
     *                         "keyword": {
     *                         "type": "keyword",
     *                          "ignore_above": 256
     *                                  }
     *                          }
     *                   },
     *                   "breachContent":{
     *                        "type" : "text",
     *                        "fields": {
     *                         "keyword": {
     *                         "type": "keyword",
     *                          "ignore_above": 2560
     *                                  }
     *                          }
     *                   },
     *                   "matchContent":{
     *                        "type" : "text",
     *                        "fields": {
     *                         "keyword": {
     *                         "type": "keyword",
     *                          "ignore_above": 2560
     *                                  }
     *                          }
     *                   }
     *               }
     *            }
     *         }
     * }
     */

    @Test
    public void insertBulk() throws InterruptedException
    {
        List<CompletableFuture<Void>> futureList = new ArrayList<>();
        IntStream.range(0, 12).forEach(_index ->
        {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() ->
            {
                try
                {
                    BulkRequest bulkRequest = new BulkRequest();
                    IndexRequest indexRequest = Requests.indexRequest().index(index).type(type);
                    indexRequest.source(new HashMap<>(Collections.singletonMap("content", UUID.randomUUID().toString())));
                    bulkRequest.add(indexRequest);
                    bulkRequest.setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE);
                    rhlClient.bulk(bulkRequest);
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            });
            futureList.add(future);
        });

        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[0])).join();
    }

    @Test
    public void insert()
    {
        try
        {
            IndexRequest indexRequest = Requests.indexRequest().index(index).type(type);
            String value = UUID.randomUUID().toString();
            System.out.println(value);
            indexRequest.id(UUID.randomUUID().toString());
            indexRequest.source(new HashMap<>(Collections.singletonMap("content", value)));
            rhlClient.index(indexRequest);

            // 修改索引mapping, 重启ES才生效
            String _mapping = "{\"_doc\":{\"properties\":{\"content\":{\"type\":\"text\",\"fields\":{\"keyword\":{\"type\":\"keyword\",\"ignore_above\":30}}}}}}";
            PutMappingRequest mapping = new PutMappingRequest()
                    .source(_mapping, XContentType.JSON).type(type).indices(index);
            rhlClient.indices().putMapping(mapping);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Test
    public void createIndexWithMapping()
    {
        try
        {
            CreateIndexRequest indexRequest = Requests.createIndexRequest(index);
            String _mapping = "{\"_doc\":{\"properties\":{\"content\":{\"type\":\"text\",\"fields\":{\"keyword\":{\"type\":\"keyword\",\"ignore_above\":30}}}}}}";
            PutMappingRequest mapping = new PutMappingRequest()
                    .source(_mapping, XContentType.JSON).type(type).indices(index);
            rhlClient.indices().create(indexRequest);

            // 创建索引后设置映射 ignore_above设置为30, 则content字段长度超过30, 聚合查询无效
            rhlClient.indices().putMapping(mapping);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
