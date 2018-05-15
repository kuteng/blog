《如何将一个100s的搜索降低到亚秒范围内》
===================================================================
.. toctree::
   :maxdepth: 1

文章网址
^^^^^^^^^^^^^^^^^^^
https://abhishek376.wordpress.com/2014/11/24/how-we-optimized-100-sec-elasticsearch-queries-to-be-under-a-sub-second/
需要翻墙。

摘要
^^^^^^^^^^^^^^^^^^^
讲述一个由于 `Memory` 使用不到，导致性能降低的案例。

笔记
^^^^^^^^^^^^^^^^^^^
- filters缓存充满 `Memory` 的时候，就会导致 ``Old GC`` 的启动。而一般情况下， ``Old GC`` 的启动应该是少见和只持续很短时间的。
- 垃圾回收的时间内，对应节点对于这个整个集群来说是 `罢工` 状态的。
- 在紧急处理 `缓存溢出` 的时候，可以直接考虑将 `Memory` 加倍。不提倡过多的增加 `JVM heap` ，我们可以考虑在一个Node上同时运行两个elasticsearch实例。 —— 但是我不知道为什么要这样做！？
- 用于 `aggregations` 的缓存默认是 `100%` `JVM heap` 。
- 减轻 `JVM heap` 的压力是需要注意的事情。

英语语句
^^^^^^^^^^^^^^^^^^^
- `lessons learnt along the way` : 经验教训
- `We used Marvel to dig in to the elasticsearch metrics to pinpoint the root cause.`
- `This memory space is shared by all customers in production and is managed by a least recently used schedule. When we fill up the filter cache space the least recently used filters are evicted.`
- `We can see from the above graph the thrashing of memory space where we are constantly building up and evicting filters over and over again for a continuous time period. This led to some very long garbage collections.`
- `That means that for the duration of the garbage collection that Elasticsearch node is dead to the rest of the cluster.`
- `We are mostly memory bound.`
- `We have seen that due to the file cache and SSD’s, 132GB of filter cache is being filled up in less than 4 seconds causing out of memory exceptions which it turn crashing the cluster.`
- `Upgrading hardware is not always a solution, but in our case since we are mostly memory bound we could just double the ram on the nodes. But its not recommended to allocate more than 32GB for JVM heap. So we decided to double our RAM and run two instances of Elasticsearch on each node, thanks to Elasticsearch rack awareness both primary and replica shards doesn’t live on the same box. Upgrading the ram bought us time to figure out what’s going on.`
- `Our first intuition is look at what we cache. When we looked at our queries we realized that we cache almost everything which is a lot, when we have thousands of queries per second. For example one of our queries look something like this`
- `At the time of slowness, we had about 64 billion documents in the cluster. Requests are executed in a map reduce fashion. Requests from the client are load balanced and distributed across all the nodes in the cluster. The node receiving the request sends the request to all the nodes containing the shards. Each shard caches the filter using bit sets.`
- `Since the filters are cached on the first requests, all the subsequents requests should be served from memory. But the problem, the request can be routed to both primary and replica and due to large number of requests, the filter cache is continuously teared up and rebuilt. In effort to decrease the number of queries we started looking in to aggregations.` —— 后面两句话，虽然能翻译出来，但是不懂！
- `Due to the distributed nature there are not always accurate. Elasticsearch has some documentation on how this works` http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/search-aggregations-bucket-terms-aggregation.html
- `All of this can be done using just one query and its executed parallel across several nodes in your cluster. Causing the number of queries go down and in turn decreasing the stress on memory.`
- `Which raises more concerns as it has to load all the field data in to memory mostly likely causing out of memory exceptions since we cannot predict the amount of memory that we would need. Which might lead to more stress on memory, leading to more old generation garbage collections leading to slow query times and even the risk of crashing the cluster.`
- `To avoid this, Elasticsearch has circuit breakers to safe guard the amount of memory being used by the field cache. Which can also be set at a request level, when a request is consuming more memory than a certain level, the requested is terminated. And we are trading more CPU for RAM. By default, field data is lazy loaded, but there are various workarounds in the Elasticsearch documentation. There are also plans to use file system cache for field data. File System Cache is managed by the OS and is much more efficient and no garbage collections. Distributed systems are slowly moving away from using JVM heap. Apache Kafka now completely depends on file system cache.` http://www.elasticsearch.org/guide/en/elasticsearch/guide/current/preload-fielddata.html
- `Bottom line, JVM heap can be a blessing and a curse.`
- `Memory is the one of reasons why elasticsearch is so fast but if not careful it might drastically affect the performance. Doesn’t necessarily mean to not use memory but to decrease the stress of JVM heap and use file system cache or similar.`

- `I'm reading a foreign technical article`
