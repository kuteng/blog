ElasticSearch
==========================

.. toctree::
   :maxdepth: 3

问题
^^^^^^^^^^^^^^^^^
- `unassigned_shards` 是什么东西，它是如何产生的。Elasticsearch的shards是字段产生/分配的吗？一创建新的索引，就会有新的shards，如果是这样，为什么会有 `unassigned_shards` ？
- `shards` 的作用是什么？
- ES是否可以更具数据特性，进行一些压缩？

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

- text/keyword类型的区别。
  - Text 数据类型被用来索引长文本，比如说电子邮件的主体部分或者一款产品的介绍。这些文本会被分析，在建立索引前会将这些文本进行分词，转化为词的组合，建立索引。允许 ES来检索这些词语。text 数据类型不能用来排序和聚合。
  - Keyword 数据类型用来建立电子邮箱地址、姓名、邮政编码和标签等数据，不需要进行分词。可以被用来检索过滤、排序和聚合。keyword 类型字段只能用本身来进行检索。

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

ES运维
^^^^^^^^^^^^^^^^
- ``[URL]:9200/_cat/nodes?help`` 查询节点状况
- ``[URL]:9200/_cat/nodes?v&h=id,pid,ip,host,disk.avail' && echo`` 查询磁盘可用空间大小
  ``[URL]:9200/_cat/nodes?v&h=ip,host,name,heap.current,heap.percent,heap.max,ram.max,disk.avail,node.role,master``
  结果： ::

    ip      name    heap.current heap.percent heap.max ram.max disk.avail node.role master
    x.x.x.x 2oElS91        3.4gb           43    7.9gb  15.6gb     57.1gb mdi       *
    x.x.x.x XaJf4Q0        1.2gb           15    7.9gb  15.6gb    221.2gb mdi       -
    x.x.x.x OQomrEd        1.1gb           13    7.9gb  15.6gb    229.6gb mdi       -
    x.x.x.x cFyDpHM        3.9gb           49    7.9gb  15.6gb    128.2gb mdi       -

- ``[URL]:9200/_cat/allocation?v`` 监控分配情况，其中也有磁盘大小和磁盘可用空间等信息 ::

    shards disk.indices disk.used disk.avail disk.total disk.percent host    ip      node
        30         44gb      52gb    105.2gb    157.3gb           33 x.x.x.x x.x.x.x cFyDpHM
        66         99gb   107.1gb     50.2gb    157.3gb           68 x.x.x.x x.x.x.x 2oElS91
        37       55.7gb    71.8gb    242.9gb    314.8gb           22 x.x.x.x x.x.x.x OQomrEd
        29       63.4gb    79.5gb    235.3gb    314.8gb           25 x.x.x.x x.x.x.x XaJf4Q0

- ``curl '[URL]:9200/_cat/indices?v'`` 在命令行查看所有索引。
- ``curl '[URL]:9200/_cat/indices?v' | grep ' \.' && echo`` 查看名称以"."开头的索引的信息。

思路
^^^^^^^^^^^^^^^^^
- 手动为文档生成id
