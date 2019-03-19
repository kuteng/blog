Redis
============================
安装
^^^^^^^^^^^^^^^^^^^^^^^^^^^^

常用配置
^^^^^^^^^^^^^^^^^^^^^^^^^^^^
- 查询数据库数量: ``config get databases`` 。默认是 ``16`` 。
- 查询本地数据库文件名称: ``config get dbfilename``
- 查询本地数据库文件存放目录: ``config get dir``
- 查询在多长时间内，有多少次更新操作，就将数据同步到数据文件: ``config get save``

  - 返回结果可能如下： ``900 1 300 10 60 10000`` 。即：900秒内一次更改、300秒内10次更改、60秒内10000次更改。

- 查询本机是否为 slave 服务: ``config get slaveof`` 。如果是 `slave` 服务，它会返回 `master` 服务的IP和port。
- 当本机是master服务时，可以查询本机的 `master` 密码，及 `slave` 端连接 `master` 端时需要使用的密码: ``config get masterauth`` 。默认为 ``""`` （空），即关闭此功能。
- 查询 `Redis` 的连接密码，及 `redis-cli` 连接 `Redis` 时需要使用 ``auth <password>`` 输入的密码: ``config get requirepass`` 。默认为 ``""`` （空），即关闭此功能。
- 查询同一时间最大客户端连接数: ``config get maxclients`` 。默认为 ``0`` （无限制）。
- 查询Redis最大内存限制: ``config get maxmemory`` 。

  当“清除已到期或即将到期的Key”操作之后，占用内存还是超过此值时，Redis将只能进行读操作，无法写入新内容。

- 查询是否在每次更新操作后进行日志记录: ``config get appendonly`` ，默认为 `no` 。如果不开启可能会在断电时导致一段时间内的数据丢失。
- 查询更新日志的文件名: ``config get appendfilename`` ，默认为 ``appendonly.aof`` 。
- 查询更新日志条件: ``config get appendfsync`` ，默认为 ``everysec`` 。取值范围是: ``no`` 、 ``always`` 、 ``everysec`` 。

数据结构
^^^^^^^^^^^^^^^^^^
基本的数据结构
  - 字符串：

    ``set [key] [value]`` 、 ``getset [key] [value]`` 、 ``setnx [key] [value]`` 、 ``mset [key1] [value1] [key2] [value2]...`` 、 ``msetnx [key1] [value1] [key2] [value2]...`` 、 ``append [key] [value]`` 。

    ``setex [key] [seconds] [value]`` 。

    ``get [key]`` 、 ``getrange [key] [start] [end]`` 、 ``getbit [key] [offset]`` 、 ``mget [key1] [key2]...`` 。

    ``strlen [key]`` 。

    ``incr [key]`` 、 ``incrby [key] [increment]`` 、 ``incrbyfloat [key] [increment]`` 、 ``decr [key]`` 、 ``decrby [key] [decrement]`` 。

  - 哈希（Hash）：

    ``hset [key] [field] [value]`` 、 ``hmset [key] [field1] [value1] [field2] [value2]...`` 、 ``hsetnx [key] [field] [value]``

    ``hdel [key] [field1] [field2]...``

    ``hget [key] [field]`` 、 ``hgetall [key]`` 、 ``hmget [key] [field] [field2]``

    ``hincrby [key] [field] [increment]`` 、 ``hincrbyfloat [key] [field] [increment]``

    ``hexists [key] [field]`` 、 ``hkeys [key]`` 、 ``hvals [key]`` 、 ``hlen [key]`` 、 ``hscan key cursor [MATCH pattern] [COUNT count]``

  - 列表（List）：

    ``lpush key value1 [value2]...`` 、 ``rpush key value1 [value2]`` 、 ``lpushx key value`` 、 ``RPUSHX key value`` 、 ``linsert key before|after privot value`` 、 

    ``lrem key count value`` 、 ``ltrim key start stop`` 、 

    ``lset key index value`` 、 

    ``lpop key`` 、 ``rpop key`` 、 ``blpop key1 [key2] timeout`` 、 ``brpop key1 [key2] timeout`` 、 ``lindex key index`` 、 ``lrange key start stop`` 、 

    ``llen key`` 、 ``brpoplpush srcKey descKey timeout`` 、 ``rpoplpush srcKey destKey``

  - 集合（Set）：

    ``sadd key member1 [member2]``

    ``srem key member1 [member2]`` 、 

    ``smembers key`` 、 ``spop key`` 、 ``srandmember key [count]`` 、 ``

    ``sdiff key1 [key2]`` 、 ``sdiffstore destkey key1 [key2]`` 、 ``sinter key1 [key2]`` 、 ``sinterstore destKey key1 [key2]`` 、 ``sunion key1 [key2]`` 、 ``sunionstore destKey key1 [key2]`` 、 

    ``scard key`` 、 ``sismember key member`` 、 ``smove source destination member`` 、 ``sscan key cursor [match pattern] [count count]``

  - 有序集合(sorted set)

    ``zadd key score1 member1 [score2 member2]``

    ``zrem key member [member ...]`` 、 ``zremrangebylex key min max`` 、 ``zremrangebyrank key start stop`` 、 ``zremrangebyscore key min max``

    ``zincrby key increment member``

    ``zrange key start stop [withscores]`` 、 ``zrangebylex key min max [limit offset count]`` 、 ``zrangebyscore key min max [withscores] [limit]`` 、 ``zrevrange key start stop [withscores]`` 、 ``zrevrangebyscore key max min [withscores]``

    ``zinterstore destKey numkeys srcKey1 [srcKey2 ...]`` 、 ``zunionstore destKey numkeys srcKey1 [srcKey2 ...]``

    ``zcard key`` 、 ``zcount key min max`` 、 ``zlexcount key min max`` 、 ``zrank key member`` 、 ``zrevrank key member`` 、 ``zscore key member`` 、 ``zscan key cursor [match pattern] [count count]``

其他
  - 对 `Key` 进行管理：

    ``del [key]`` 、 ``keys [pattern]``

    ``dump [key]`` 、 ``exists [key]`` 、 ``move [key] [database]`` 、 ``rename [key] [newname]`` 、 ``randomkey [key]`` 、 ``type [key]``

    ``expire [key] [seconds]`` 、 ``expireat [key] [timestamp]`` 、 ``pexpire key [millisseconds]`` 、 ``pexpireat [key] [m-timestamp]`` 、 ``persist [key]`` 、 ``ttl [key]`` 、 ``pttl [key]`` 、  。

其他
^^^^^^^^^^^^^^^^^^
- 每个 hash 可以存储 232 -1 个键值对（40多亿）。
- Master最好不要做任何持久化工作，如RDB内存快照和AOF日志文件
- 为了主从复制的速度和连接的稳定性，Master和Slave最好在同一个局域网内
- 尽量避免在压力很大的主库上增加从库
- 主从复制不要用图状结构，用单向链表结构更为稳定，即：Master <- Slave1 <- Slave2 <- Slave3…
- 可以使用 `Redis` 作为中间件，解决数据库频繁更新压力过大的问题。
- `Redis` 可以作为 `共享Session` 的解决方案。
- Redis直接自己构建了VM 机制 ，因为一般的系统调用系统函数的话，会浪费一定的时间去移动和请求。
- redis中value最大可以达到1GB，而memcache中value只有1MB的容量。
- redis 提供 6种数据淘汰策略，借助这些策略我们可以在 `Mysql+Redis` 框架中保证redis中的数据都是热点数据：

  - ``voltile-lru`` ：从已设置过期时间的数据集（server.db[i].expires）中挑选最近最少使用的数据淘汰
  - ``volatile-ttl`` ：从已设置过期时间的数据集（server.db[i].expires）中挑选将要过期的数据淘汰
  - ``volatile-random`` ：从已设置过期时间的数据集（server.db[i].expires）中任意选择数据淘汰
  - ``allkeys-lru`` ：从数据集（server.db[i].dict）中挑选最近最少使用的数据淘汰
  - ``allkeys-random`` ：从数据集（server.db[i].dict）中任意选择数据淘汰
  - ``no-enviction`` ：禁止驱逐数据。（envition ? ）

- 有序集合操作的时间复杂度：查找（ `O(1)` ）、修改/删除（ `O(log(n))` ）。因为后者涉及到修改 `skiplist` 。
