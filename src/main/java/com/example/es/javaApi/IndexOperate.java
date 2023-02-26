package com.example.es.javaApi;

import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.client.indices.GetIndexResponse;

/**
 * @author lixiaoqiang
 */
public class IndexOperate {
    public static void main(String[] args) throws Exception{
        RestHighLevelClient client = new RestHighLevelClient(RestClient.builder(new HttpHost("localhost", 9200, "http")));
        // createIndex(client);
        searchIndex(client);
        deleteIndex(client);
        client.close();
    }

    /**
     * 创建索引
     * @param client
     * @throws Exception
     */
    private static void createIndex(RestHighLevelClient client) throws Exception{
        // 创建并发送请求
        CreateIndexResponse user2 = client.indices().create(new CreateIndexRequest("user2"), RequestOptions.DEFAULT);
        // 创建请求返回结果
        System.out.println("操作状态:" + user2.isAcknowledged());
    }

    /**
     * 查询索引
     */
    private static void searchIndex(RestHighLevelClient client) throws Exception {
        GetIndexResponse user2 = client.indices().get(new GetIndexRequest("user2"), RequestOptions.DEFAULT);
        System.out.println("aliases: " + user2.getAliases());
        System.out.println("mappings: " + user2.getMappings());
        System.out.println("settings: " + user2.getSettings());
    }

    private static void deleteIndex(RestHighLevelClient client) throws Exception{
        AcknowledgedResponse user2 = client.indices().delete(new DeleteIndexRequest("user2"), RequestOptions.DEFAULT);
        System.out.println("删除操作结果: " + user2.isAcknowledged());
    }
}
