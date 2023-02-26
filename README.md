# es
Elasticsearch学习2.4.13 7.9.3

| 名字          | 作用                           |
| ------------- | ------------------------------ |
| Elasticsearch | 基于json的分布式搜索和分析引擎 |
| Logstash      | 动态数据收集管道，生态丰富     |
| Kibana        | 提供可视化界面                 |
| Beats         | 轻量级数据采集器               |

- docker安装es7.8.0

  ```shell
  docker pull elasticsearch:7.8.0
  ```

  运行es,并将容器内的config文件复制到宿主机

  ```shell
  docker run -d --name es9200 --privileged=true --net elastic -p 9200:9200 -p 9300:9300  -e "discovery.type=single-node" -v /Users/username/docker/elasticsearch/9200/config:/usr/share/elasticsearch/config/ -v /Users/username/docker/elasticsearch/9200/data/:/usr/share/elasticsearch/data/ -v /Users/username/docker/elasticsearch/9200/plugins/:/usr/share/elasticsearch/plugins/ -v /Users/username/docker/elasticsearch/9200/logs/:/usr/share/elasticsearch/logs/ elasticsearch:7.8.0
  # docker挂载的主目录为：/Users/username/docker/elasticsearch/9200
  # 在主目录下执行
  docker cp es9200:/usr/share/elasticsearch/config .
  # 创建data与plugins目录
  ```
  
- 索引操作：与数据库对应

  创建索引put http://127.0.0.1:9200/shopping

  ```json
  {
    "acknowledged": true,// 相应结果
    "shards_acknowledged": true,// 分片结果
    "index": "shopping"// 索引名称
  }
  ```

  查看所有索引get http://127.0.0.1:9200/_cat/indices?v

  | 字段           | 含义                                                         |
  | -------------- | ------------------------------------------------------------ |
  | health         | 当前服务器健康状态： green(集群完整) yellow(单点正常、集群不完整) red(单点不正常) |
  | status         | 索引状态：打开、关闭                                         |
  | index          | 索引名                                                       |
  | uuid           | 索引id                                                       |
  | pri            | 主分片数量                                                   |
  | rep            | 副本数量                                                     |
  | docs.count     | 可用文档数量                                                 |
  | docs.deleted   | 文档删除状态（逻辑删除）                                     |
  | store.size     | 主分片和副分片整体占空间大小                                 |
  | pri.store.size | 主分片占空间大小                                             |

  查看索引信息get http://127.0.0.1:9200/shopping

  ```json
  {
      "shopping":{// 索引名
          "aliases":{// 别名
          },
          "mappings":{// 映射
          },
          "settings":{// 设置
              "index":{// 索引
                  "creation_date":"1677067332784",// 创建时间
                  "number_of_shards":"1",
                  "number_of_replicas":"1",
                  "uuid":"RWmHGrMLT7ODSsMpnuDUKQ",
                  "version":{
                      "created":"7080099"
                  },
                  "provided_name":"shopping"
              }
          }
      }
  }
  ```

  删除索引delete http://127.0.0.1:9200/shopping

  ```json
  {
    "acknowledged": true// 响应结果
  }
  ```

- 文档操作：与数据表对应

  创建文档book post http://127.0.0.1:9200/shopping/book 

  与之对应另一种方式 put http://127.0.0.1:9200/shopping/book/1 指定行id

  参数：

  ```json
  {
    "title": "小米手机",
    "category": "小米",
    "image": "http://www.baidu.com",
    "price": 2999
  }
  ```

  返回结果：

  ```json
  {
    "_index": "shopping",// 索引
    "_type": "book",// 文档
    "_id": "5m0ueYYBYSeowL72vzyU",// 行id
    "_version": 1,// 版本
    "result": "created",// 响应结果 created updated
    "_shards": {// 分片
      "total": 2,// 总数
      "successful": 1,
      "failed": 0
    },
    "_seq_no": 0,
    "_primary_term": 1
  }
  ```

- 文档查询

  - 基于id查询和全部查询

    get http://127.0.0.1:9200/shopping/book/1 查询id为1的文档

    ```json
    {
      "_index": "shopping",
      "_type": "book",
      "_id": "1",
      "_version": 1,
      "_seq_no": 1,
      "_primary_term": 1,
      "found": true,// false为未查询到数据
      "_source": {
        "title": "小米手机",
        "category": "小米",
        "image": "http://www.baidu.com",
        "price": 3999
      }
    }
    ```

    get http://127.0.0.1:9200/shopping/_search  查询所有数据

    ```json
    {
      "took": 5,
      "timed_out": false,
      "_shards": {
        "total": 1,
        "successful": 1,
        "skipped": 0,
        "failed": 0
      },
      "hits": {
        "total": {
          "value": 2,
          "relation": "eq"
        },
        "max_score": 1,
        "hits": [
          {
            "_index": "shopping",
            "_type": "book",
            "_id": "5m0ueYYBYSeowL72vzyU",
            "_score": 1,
            "_source": {
              "title": "小米手机",
              "category": "小米",
              "image": "http://www.baidu.com",
              "price": 2999
            }
          },
          {
            "_index": "shopping",
            "_type": "book",
            "_id": "1",
            "_score": 1,
            "_source": {
              "title": "小米手机",
              "category": "小米",
              "image": "http://www.baidu.com",
              "price": 3999
            }
          }
        ]
      }
    }
    ```


- 修改

  - 全量修改

    put http://127.0.0.1:9200/shopping/book/1 去掉了image属性

    ```json
    // 入参
    {
      "title": "小米手机",
      "category": "小米",
      "price": 3999
    }
    // 返回
    {
      "_index": "shopping",
      "_type": "book",
      "_id": "1",
      "_version": 2,
      "result": "updated",
      "_shards": {
        "total": 2,
        "successful": 1,
        "failed": 0
      },
      "_seq_no": 2,
      "_primary_term": 2
    }
    // 查询id为1的book
    {
      "_index": "shopping",
      "_type": "book",
      "_id": "1",
      "_version": 2,
      "_seq_no": 2,
      "_primary_term": 2,
      "found": true,
      "_source": {
        // image属性已经更新掉
        "title": "小米手机",
        "category": "小米",
        "price": 3999
      }
    }
    ```

  - 局部更新post http://127.0.0.1:9200/shopping/_update/1 

    ```json
    {
      "doc":{
        "title": "小米手机1",
        "category": "小米",
        "price": 2999
    	}
    }
    // 返回信息与全量更新类似
    ```

  - 删除数据 delete http://127.0.0.1:9200/shopping/book/1 

- 查询

  - 条件查询

    get http://127.0.0.1:9200/shopping/_search json请求体

    ```json
    {
      "query": {// 查询
        "match": {// 匹配查询
          "category": "小米"// 匹配品牌为小米的数据
        }
      }
    }
    
    {
      "query": {// 查询
        "match_all": {// 匹配所有文档
        }
      }
    }
    
    {
      "query": {// 查询
        "match_all": {// 匹配所有文档
        },
        "_source":["title"]//只查询字段为title的所有数据
      }
    }
    
    {
      "query": {// 查询
        "match_all": {// 匹配所有文档
        },
        "from":0,// 分页查询
        "to":2
      }
    }
    
    {
      "query": {// 查询
        "match_all": {// 匹配所有文档
        },
        "sort": {// 排序
          "price": {// 按照价格排序
            "order": "desc"// 倒序
          }
        }
      }
    }
    ```

  - 多条件查询

    ```json
    // 查询出品牌为小米并且价格为3999数据
    {
      "query": {// 查询
        "bool": {// 按照什么条件
          "must": [{// 数据库中的and
              "match": {
                "category": "小米"
              }
          	},{
              "match": {
                "price": 3999.00
              }
            }
          ]
        }
      }
    }
    // 查询出品牌为小米或是华为的数据并且价格大于2000的
    {
      "query": {// 查询
        "bool": {// 按照什么条件
          "should": [{// 数据库中的or 查询出品牌为小米并且价格为3999数据
              "match": {
                "category": "小米"
              }
          	},{
              "match": {
                "category": "华为"
              }
            }
          ]
        },
        "filter": {
          "range": {
            "price": {
              "gt": 2000
            }
          }
        }
      }
    }
    ```

  - 全文检索

    ```json
    {
      "query": {
        "match": {
          "category": "小华"// 将查询出品牌带有小或是华的数据
        }
      }
    }
    
    {
      "query": {
        "match_phrase": {
          "category": "华"// 将查询出品牌带有华的数据，如果改为小华，将查询出品牌带有小华的数据
        }
      }
    }
    
    {
      "query": {
        "match_phrase": {
          "category": "华"// 将查询出品牌带有华的数据，如果改为小华，将查询出品牌带有小华的数据
        },
        "highlight": {
          "fields": {
            "category":{}// 高亮字段值
          }
        }
      }
    }
    ```

  - 聚合查询

    ```json
    {
      "aggs": {// 聚合操作
        "price_group": {// 自定义聚合名称
          "terms": {// 分组
            "field": "price"// 分组字段
          }
        }
      },
      "size": 0 // 不带原始数据
    }
    
    {
      "aggs": {// 聚合操作
        "price_avg": {// 自定义聚合名称
          "avg": {// 平均值
            "field": "price"// 取平均值的字段
          }
        }
      },
      "size": 0 // 不带原始数据
    }
    ```

  - 映射mapping

    自定义文档属性的映射关系

    ```json
    {
        "properties":{
            "category":{
                "type":"text",// 可分词
                "index":true// 可查询
            },
            "image":{
                "type":"keyword",// 不可分词
                "index":true//可查询
            },
            "title":{
                "type":"text",
                "index":false// 不可查询
            }
        }
    }
    ```

    
    
    
