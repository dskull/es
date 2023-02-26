package com.example.es.javaApi;

import com.example.es.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHost;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.*;
import org.elasticsearch.rest.RestHandler;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.xcontent.XContent;
import org.elasticsearch.xcontent.XContentType;

import java.util.Map;

/**
 * @author lixiaoqiang
 */
public class DocOperate {
    public static void main(String[] args) throws Exception{
        RestHighLevelClient client = new RestHighLevelClient(RestClient.builder(new HttpHost("localhost", 9200, "http")));
        groupQuery(client);
        client.close();
    }

    /**
     * 创建文档
     * @param client
     * @throws Exception
     */
    private static void createDoc(RestHighLevelClient client) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        User user = new User("张三", 23, "男");
        IndexRequest indexRequest = new IndexRequest().index("user").id("1001");
        indexRequest.source(objectMapper.writeValueAsString(user), XContentType.JSON);
        IndexResponse index = client.index(indexRequest, RequestOptions.DEFAULT);
        System.out.println("index: " + index.getIndex());
        System.out.println("id: " + index.getId());
        System.out.println("result: " + index.getResult());
    }

    /**
     * 更新文档
     * @param client
     * @throws Exception
     */
    private static void updateDoc(RestHighLevelClient client) throws Exception {
        UpdateRequest request = new UpdateRequest();
        request.index("user").id("1001");
        request.doc(XContentType.JSON, "sex", "女");
        UpdateResponse update = client.update(request, RequestOptions.DEFAULT);
        System.out.println("index: " + update.getIndex());
        System.out.println("id: " + update.getId());
        System.out.println("result: " + update.getResult());
    }

    /**
     * 查询id为1001的文档
     * @param client
     * @throws Exception
     */
    private static void queryDoc(RestHighLevelClient client) throws Exception {
        GetRequest getRequest = new GetRequest().index("user").id("1001");
        GetResponse documentFields = client.get(getRequest, RequestOptions.DEFAULT);
        System.out.println("index: " + documentFields.getIndex());
        System.out.println("id: " + documentFields.getId());
        System.out.println("type: " + documentFields.getType());
        System.out.println("result: " + documentFields.getSourceAsString());
    }

    /**
     * 删除文档
     * @param client
     * @throws Exception
     */
    private static void deleteDoc(RestHighLevelClient client) throws Exception {
        DeleteRequest deleteRequest = new DeleteRequest().index("user").id("1001");
        DeleteResponse delete = client.delete(deleteRequest, RequestOptions.DEFAULT);
        System.out.println(delete);
    }

    /**
     * 批量操作文档
     * @param client
     * @throws Exception
     */
    private static void batchDoc(RestHighLevelClient client) throws Exception {
        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.add(new IndexRequest().index("user").id("1001").source(XContentType.JSON, "name", "zhangsan"));
        bulkRequest.add(new IndexRequest().index("user").id("1002").source(XContentType.JSON, "name", "lisi"));
        bulkRequest.add(new IndexRequest().index("user").id("1003").source(XContentType.JSON, "name", "wanger"));
        BulkResponse bulk = client.bulk(bulkRequest, RequestOptions.DEFAULT);
        System.out.println("took: " + bulk.getTook());
        System.out.println("items: " + bulk.getItems());
        bulkRequest.add(new UpdateRequest().index("user").id("1001").doc(XContentType.JSON, "name", "zhangsan1"));
        bulkRequest.add(new UpdateRequest().index("user").id("1002").doc(XContentType.JSON, "name", "lisi1"));
        bulkRequest.add(new UpdateRequest().index("user").id("1003").doc(XContentType.JSON, "name", "wanger1"));
        bulk = client.bulk(bulkRequest, RequestOptions.DEFAULT);
        System.out.println("took: " + bulk.getTook());
        System.out.println("items: " + bulk.getItems());
        bulkRequest.add(new DeleteRequest().index("user").id("1001"));
        bulkRequest.add(new DeleteRequest().index("user").id("1002"));
        bulkRequest.add(new DeleteRequest().index("user").id("1003"));
        bulk = client.bulk(bulkRequest, RequestOptions.DEFAULT);
        System.out.println("took: " + bulk.getTook());
        System.out.println("items: " + bulk.getItems());
    }

    /**
     * 批量保存文档
     * @param client
     * @throws Exception
     */
    private static void batchInsertDoc(RestHighLevelClient client) throws Exception {
        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.add(new IndexRequest().index("user").id("1001").source(XContentType.JSON, "name", "zhangsan", "age", "10", "sex", "女"));
        bulkRequest.add(new IndexRequest().index("user").id("1002").source(XContentType.JSON, "name", "lisi", "age", "30", "sex", "女"));
        bulkRequest.add(new IndexRequest().index("user").id("1003").source(XContentType.JSON, "name", "wanger", "age", "40", "sex", "男"));
        bulkRequest.add(new IndexRequest().index("user").id("1004").source(XContentType.JSON, "name", "wanger1", "age", "20", "sex", "女"));
        bulkRequest.add(new IndexRequest().index("user").id("1005").source(XContentType.JSON, "name", "wanger2", "age", "50", "sex", "男"));
        bulkRequest.add(new IndexRequest().index("user").id("1006").source(XContentType.JSON, "name", "wanger3", "age", "20", "sex", "男"));
        bulkRequest.add(new IndexRequest().index("user").id("1007").source(XContentType.JSON, "name", "张三", "age", "10", "sex", "女"));
        bulkRequest.add(new IndexRequest().index("user").id("1008").source(XContentType.JSON, "name", "李四", "age", "30", "sex", "女"));
        bulkRequest.add(new IndexRequest().index("user").id("1009").source(XContentType.JSON, "name", "王二", "age", "40", "sex", "男"));
        bulkRequest.add(new IndexRequest().index("user").id("1010").source(XContentType.JSON, "name", "王二1", "age", "20", "sex", "女"));
        bulkRequest.add(new IndexRequest().index("user").id("1011").source(XContentType.JSON, "name", "王二2", "age", "50", "sex", "男"));
        bulkRequest.add(new IndexRequest().index("user").id("1012").source(XContentType.JSON, "name", "王二3", "age", "20", "sex", "男"));
        BulkResponse bulk = client.bulk(bulkRequest, RequestOptions.DEFAULT);
    }

    /**
     * 查询全部数据
     * @param client
     * @throws Exception
     */
    private static void queryAllMatch(RestHighLevelClient client) throws Exception {
        SearchRequest searchRequest = new SearchRequest();
        // 查询user的索引
        searchRequest.indices("user");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        searchRequest.source(searchSourceBuilder);
        SearchResponse search = client.search(searchRequest, RequestOptions.DEFAULT);
        SearchHits hits = search.getHits();
        System.out.println("took: " + search.getTook());
        System.out.println("timedOut: " + search.isTimedOut());
        System.out.println("totalHits: " + hits.getTotalHits());
        System.out.println("maxScore: " + hits.getMaxScore());
        for (SearchHit hit : hits) {
            System.out.println(hit.getSourceAsString());
        }
    }

    /**
     * 匹配查询
     * @param client
     * @throws Exception
     */
    private static void matchQuery(RestHighLevelClient client) throws Exception {
        SearchRequest request = new SearchRequest();
        request.indices("user");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        // matchQuery 和 termQuery的区别
        searchSourceBuilder.query(QueryBuilders.termQuery("age", "30"));
        request.source(searchSourceBuilder);
        SearchResponse search = client.search(request, RequestOptions.DEFAULT);
        SearchHits hits = search.getHits();
        System.out.println("took: " + search.getTook());
        System.out.println("timedOut: " + search.isTimedOut());
        System.out.println("totalHits: " + hits.getTotalHits());
        System.out.println("maxScore: " + hits.getMaxScore());
        for (SearchHit hit : hits) {
            System.out.println(hit.getSourceAsString());
        }
    }

    /**
     * 分页查询
     * @param client
     * @throws Exception
     */
    private static void pageQuery(RestHighLevelClient client) throws Exception {
        SearchRequest request = new SearchRequest();
        request.indices("user");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        // matchQuery 和 termQuery的区别
        // termQuery高亮查询
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        searchSourceBuilder.from(1);
        searchSourceBuilder.size(2);
        request.source(searchSourceBuilder);
        SearchResponse search = client.search(request, RequestOptions.DEFAULT);
        SearchHits hits = search.getHits();
        System.out.println("took: " + search.getTook());
        System.out.println("timedOut: " + search.isTimedOut());
        System.out.println("totalHits: " + hits.getTotalHits());
        System.out.println("maxScore: " + hits.getMaxScore());
        for (SearchHit hit : hits) {
            System.out.println(hit.getSourceAsString());
        }
    }

    /**
     * 排序查询
     * @param client
     * @throws Exception
     */
    private static void sortQuery(RestHighLevelClient client) throws Exception {
        SearchRequest request = new SearchRequest();
        request.indices("user");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        // matchQuery 和 termQuery的区别
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        searchSourceBuilder.sort("age", SortOrder.DESC);
        request.source(searchSourceBuilder);
        SearchResponse search = client.search(request, RequestOptions.DEFAULT);
        SearchHits hits = search.getHits();
        System.out.println("took: " + search.getTook());
        System.out.println("timedOut: " + search.isTimedOut());
        System.out.println("totalHits: " + hits.getTotalHits());
        System.out.println("maxScore: " + hits.getMaxScore());
        for (SearchHit hit : hits) {
            System.out.println(hit.getSourceAsString());
        }
    }

    /**
     * 组合查询
     * @throws Exception
     */
    private static void composeQuery(RestHighLevelClient client) throws Exception {
        // 创建搜索请求对象
        SearchRequest request = new SearchRequest();
        request.indices("user");
        // 构建查询的请求体
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        // 必须包含 条件1
        boolQueryBuilder.must(QueryBuilders.matchQuery("age", "20"));
        // 一定不包含 条件2
        boolQueryBuilder.mustNot(QueryBuilders.matchPhraseQuery("name", "王二3"));
        // 可能包含 条件3
        // boolQueryBuilder.should(QueryBuilders.matchQuery("sex", "男"));
        // 可能包含 条件4
        // boolQueryBuilder.should(QueryBuilders.matchQuery("name", "王二1"));

        sourceBuilder.query(boolQueryBuilder);
        request.source(sourceBuilder);
        SearchResponse search = client.search(request, RequestOptions.DEFAULT);
        SearchHits hits = search.getHits();
        System.out.println("took: " + search.getTook());
        System.out.println("timedOut: " + search.isTimedOut());
        System.out.println("totalHits: " + hits.getTotalHits());
        System.out.println("maxScore: " + hits.getMaxScore());
        for (SearchHit hit : hits) {
            System.out.println(hit.getSourceAsString());
        }
        /**
         * 条件1结果
         * {"name":"王二1","age":"20","sex":"女"}
         * {"name":"王二3","age":"20","sex":"男"}
         * {"name":"wanger1","age":"20","sex":"女"}
         * {"name":"wanger3","age":"20","sex":"男"}
         *
         * 条件1和条件2结果
         * 加上条件2后 王二1也被过滤了，分词导致如果改成matchPhraseQuery将不会出现这种情况
         * {"name":"wanger1","age":"20","sex":"女"}
         * {"name":"wanger3","age":"20","sex":"男"}
         *
         * 条件1和条件2和条件3结果
         * {"name":"wanger3","age":"20","sex":"男"}
         * {"name":"wanger1","age":"20","sex":"女"}
         *
         * 条件124结果
         */
    }

    /**
     * 范围查询
     * @param client
     * @throws Exception
     */
    private static void rangeQuery(RestHighLevelClient client) throws Exception {
        SearchRequest request = new SearchRequest().indices("user");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("age");
        rangeQueryBuilder.gt("40");
        sourceBuilder.query(rangeQueryBuilder);
        request.source(sourceBuilder);
        SearchResponse search = client.search(request, RequestOptions.DEFAULT);
        SearchHits hits = search.getHits();
        System.out.println("took: " + search.getTook());
        System.out.println("timedOut: " + search.isTimedOut());
        System.out.println("totalHits: " + hits.getTotalHits());
        System.out.println("maxScore: " + hits.getMaxScore());
        for (SearchHit hit : hits) {
            System.out.println(hit.getSourceAsString());
        }
    }

    /**
     * 模糊查询
     * @throws Exception
     */
    private static void fuzzyQuery(RestHighLevelClient client) throws Exception {
        SearchRequest request = new SearchRequest().indices("user");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        FuzzyQueryBuilder fuzzyQueryBuilder = QueryBuilders.fuzzyQuery("name", "wanger");
        sourceBuilder.query(fuzzyQueryBuilder);
        request.source(sourceBuilder);
        SearchResponse search = client.search(request, RequestOptions.DEFAULT);
        SearchHits hits = search.getHits();
        System.out.println("took: " + search.getTook());
        System.out.println("timedOut: " + search.isTimedOut());
        System.out.println("totalHits: " + hits.getTotalHits());
        System.out.println("maxScore: " + hits.getMaxScore());
        for (SearchHit hit : hits) {
            System.out.println(hit.getSourceAsString());
        }
    }

    /**
     * 高亮查询
     * @param client
     * @throws Exception
     */
    private static void termsQuery(RestHighLevelClient client) throws Exception {
        SearchRequest request = new SearchRequest().indices("user");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        TermsQueryBuilder queryBuilder = QueryBuilders.termsQuery("name", "wanger");
        sourceBuilder.query(queryBuilder);
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.preTags("<font color='red'>");
        highlightBuilder.postTags("</font>");
        highlightBuilder.field("name");
        sourceBuilder.highlighter(highlightBuilder);
        request.source(sourceBuilder);
        SearchResponse search = client.search(request, RequestOptions.DEFAULT);
        SearchHits hits = search.getHits();
        System.out.println("took: " + search.getTook());
        System.out.println("timedOut: " + search.isTimedOut());
        System.out.println("totalHits: " + hits.getTotalHits());
        System.out.println("maxScore: " + hits.getMaxScore());
        for (SearchHit hit : hits) {
            System.out.println(hit.getSourceAsString());
            // 高亮属性
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            System.out.println(highlightFields);
        }
    }

    /**
     * 最大值查询
     * @param client
     * @throws Exception
     */
    private static void maxQuery(RestHighLevelClient client) throws Exception {
        SearchRequest request = new SearchRequest().indices("user");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.aggregation(AggregationBuilders.max("maxAge").field("age")).size(0);
        request.source(sourceBuilder);
        SearchResponse search = client.search(request, RequestOptions.DEFAULT);
        SearchHits hits = search.getHits();
        System.out.println(search);
        for (SearchHit hit : hits) {
            System.out.println(hit.getSourceAsString());
        }
    }

    private static void groupQuery(RestHighLevelClient client) throws Exception {
        SearchRequest request = new SearchRequest().indices("user");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.aggregation(AggregationBuilders.terms("age_groupby").field("age"));
        request.source(sourceBuilder);
        SearchResponse search = client.search(request, RequestOptions.DEFAULT);
        SearchHits hits = search.getHits();
        System.out.println(search);
        for (SearchHit hit : hits) {
            System.out.println(hit.getSourceAsString());
        }
    }
}
