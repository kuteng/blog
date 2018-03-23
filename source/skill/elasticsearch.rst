ElasticSearch
==========================

.. toctree::
   :maxdepth: 3

知识点
^^^^^^^^^^^^^^^^^
- pretty: 格式化ES的输出。
- 同一个索引的不同type的请求格式： ::

    curl -XGET '[url]/ecommerce_creatives/creative,ecommerce_creative/_search?pretty' -d '{"size":10,"from":0}'

- explain：显示打分的详细细节。
- 索引的空间大小变化很大，原因是什么？
  这里的可能性很大：nodes.quyTWc1XSveBCNw4vS4nBw.jvm.mem.pools.old.used_in_bytes
- 分词效果 ::

    GET /_analyze
    {
      "analyzer": "standard",
      "text": "Text to analyze"
    }

- 获取索引/类型的映射定义。 ::

    GET /gb/_mapping/tweet

- 安装elasticsearch前需要放开的一些系统限制：linux
  `vi /etc/sysctl.conf` ::

    fs.file-max=65536
    vm.max_map_count=262144

  保存之后sysctl -p使设置生效

  `vi /etc/security/limits.conf` ::

    * soft nofile 65536
    * soft nofile 65536

  `vi /etc/security/limits.d/90-nproc.conf` ::

    * soft nproc 2048

  `vi config/jvm.options` ::

    -Xms2g
    -Xmx2g

  修改es服务用到的内存, 默认如上为2G，可以改为512M，如下： ::

    -Xms512m
    -Xmx512m

ES调优
^^^^^^^^^
- 增加刷新间隔：一旦刷新，缓存会随之失效。
- 尽量使用filter代替query。
- 增加副本数。 Elasticsearch 可以在主分片或副本分片上执行搜索。副本越多，搜索可用的节点就越多。
  (足够的数据节点来确保每个分片都有一个专有节点。)
- 根据我们的经验，如果索引小于 1G，可以将分片数设置为 1。对于大多数场景，我们可以将分片数保留为默认值 5，但是如果分片大小超过 30GB，我们应该增加分片 ，将索引分成更多的分片。创建索引后，分片数不能更改，但是我们可以创建新的索引并使用 reindex API 迁移数据。(测试结果150GB的最佳分片是11)(每个分片都要一个独占节点)
- 节点查询缓存。节点查询缓存只缓存过滤器上下文中使用的查询。与查询子句不同，过滤子句是“是”或“否”的问题。Elasticsearch 使用位集（bit set）机制来缓存过滤结果，以便后面使用相同的过滤器的查询进行加速。

  - 检查一个节点查询缓存是否生效: ``GET index_name/_stats?filter_path=indices.**.query_cache``

- 分片查询缓存。如果大多数查询是聚合查询，我们应该考虑分片查询缓存。分片查询缓存可以缓存聚合结果，以便 Elasticsearch 以低开销直接处理请求。有几件事情需要注意：

  - 设置“size”为 0。分片查询缓存只缓存聚合结果和建议。它不会缓存命中，因此如果将 size 设置为非零，则无法从缓存中获益。
  - 查询请求的负载（Payload）必须完全相同。分片查询缓存使用请求负载作为缓存键，因此需要确保后续查询请求的负载必须和之前的完全一致。由于负载中 JSON 键的顺序变化会导致负载变化，故建议对负载的键进行排序来确保顺序一致。
  - 处理好日期时间。不要直接在查询中使用像 Date.now 这样的变量。否则，每个请求的请求体都不同，从而导致缓存始终无效。我们建议将日期时间整理为小时或天，以便更有效地利用缓存。
