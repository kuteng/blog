Redis总结
===================
Redis是一个基于内存的高性能 ``Key-Value`` 数据库。

基本概念
^^^^^^^^^^^^^^^^^^^
支持的数据类型
:::::::::::::::::::
字符串
  也包含数字。

  应用场景：用于常规key-value缓存应用，如：Session共享。

list(列表)
  数据结构为双向链表。

  应用场景：

  - 消息队列。Redis作为日志收集器就是消息队列的一个实现场景。
  - 同时它也可作为 *栈* 使用。

hash(散列)
  一个string类型的field和value的映射表，特别适合用于存储对象。

  应用场景：存储部分变更的数据，如用户信息等。

set(集合)
  集合的概念就是一堆不重复值的组合，同时这里面的元素是没有顺序的。

  应用场景：利用Redis提供的set数据结构，可以存储一些集合性的数据。

sorted set(有序集合)
  和set相比，sorted set增加了一个权重参数score，使得集合中的元素能够按score进行有序排列。

  应用场景：有优先级的队列系统。

分布式（主从模式）
:::::::::::::::::::::::
redis支持 **主从模式** 。其原则就是：Master会将数据同步到slave，而slave不会将数据同步到master。Slave启动时会连接master来同步数据。

典型的 **读写分离模型** 。利用master来插入数据，slave提供检索服务。有效减少单个机器的并发访问数量。

此外，通过增加Slave DB的数量，读的性能可以线性增长，为了避免 **Master DB** 的单点故障，集群一般都会采用两台 **Master DB** 做双机热备，所以整个集群的读和写的可用性都非常高。

不管是Master还是Slave，每个节点都必须保存 **完整** 的数据，如果在数据量很大的情况下，集群的扩展能力是受限于单个节点的存储能力，而且对于Write-intensive（密集写操作）类型的应用，读写分离架构并不适合。

为了解决读写分离模型的缺陷，可以将 **数分片模型** 应用进来。可以将每个节点看成都是master，然后 **通过业务实现数据分片** 。结合两种模型，可以将每个master设计成由一个master和多个slave组成的模型。

redis优点
:::::::::::::::::::
- 单线程，利用redis队列技术并将访问变为串行访问，消除了传统数据库串行控制的开销

- redis具有快速和持久化的特征，速度快，因为数据存在内存中。
- 分布式 读写分离模式
- 支持丰富数据类型
- 支持事务，操作都是原子性，所谓原子性就是对数据的更改要么全部执行，要不全部不执行。
- 可用于缓存，消息，按key设置过期时间，过期后自动删除

redis与memcache区别
  - 存储方式 memcache存在内存中，redis存在硬盘中，保证数据持久化
  - 数据类型 memcache对数据类型支持相对简单，redis有复杂的数据类型
  - 使用底层模型不同:底层实现方式以及客户端之间通信的应用协议不一样
  - redis最大可以达到1G而memcache只有1MB

Redis为什么这么快？
  - 完全基于内存，绝大部分请求是纯粹的内存操作，非常快速。数据存在内存中，类似于HashMap，HashMap的优势就是查找和操作的时间复杂度都是O(1)；
  - 数据结构简单，对数据操作也简单，Redis中的数据结构是专门进行设计的；
  - 采用单线程，避免了不必要的上下文切换和竞争条件，也不存在多进程或者多线程导致的切换而消耗 CPU，不用去考虑各种锁的问题，不存在加锁释放锁操作，没有因为可能出现死锁而导致的性能消耗；
  - 使用多路I/O复用模型，非阻塞IO；
  - 使用底层模型不同，它们之间底层实现方式以及与客户端之间通信的应用协议不一样，Redis直接自己构建了VM 机制 ，因为一般的系统调用系统函数的话，会浪费一定的时间去移动和请求；

多路 I/O 复用模型
::::::::::::::::::::
多路I/O复用模型是利用 select、poll、epoll 可以同时监察多个流的 I/O 事件的能力，在空闲的时候，会把当前线程阻塞掉，当有一个或多个流有 I/O 事件时，就从阻塞态中唤醒，于是程序就会轮询一遍所有的流（epoll 是只轮询那些真正发出了事件的流），并且只依次顺序的处理就绪的流，这种做法就避免了大量的无用操作。

这里“多路”指的是多个网络连接，“复用”指的是复用同一个线程。采用多路 I/O 复用技术可以让单个线程高效的处理多个连接请求（尽量减少网络 IO 的时间消耗），且 Redis 在内存中操作数据的速度非常快，也就是说内存内的操作不会成为影响Redis性能的瓶颈，主要由以上几点造就了 Redis 具有很高的吞吐量。

那么为什么Redis是单线程的？
  官方FAQ表示，因为Redis是基于内存的操作，CPU不是Redis的瓶颈，Redis的瓶颈最有可能是机器内存的大小或者网络带宽。既然单线程容易实现，而且CPU不会成为瓶颈，那就顺理成章地采用单线程的方案了（毕竟采用多线程会有很多麻烦！）。

  警告：这里我们一直在强调的单线程，只是在处理我们的网络请求的时候只有一个线程来处理，一个正式的Redis Server运行的时候肯定是不止一个线程的，这里需要大家明确的注意一下！例如Redis进行持久化的时候会以子进程或者子线程的方式执行。

**问题** ：不同的线程处理不同网络请求（ *I/O* ）对不同Key的操作，不是会更快吗？为什么非要只用一个线程处理所有网络请求？

参考： `Redis 和 I/O 多路复用 <https://www.cnblogs.com/wxd0108/p/7575214.html>`_

回收策略
::::::::::::::::::::
内存回收触发有两种情况。一种，也就是内存使用达到maxmemory上限时候触发的溢出回收；还有一种是我们设置了过期的对象到期的时候触发的到期释放的内存回收。

几种回收策略
  - ``volatile-lru`` ：根据LRU算法删除设置了超时属性（expire）的键，直到腾出足够空间为止。如果没有可删除的键对象，回退到noeviction策略。
    即从已设置过期时间的数据集（server.db[i].expires）中挑选最近最少使用的数据淘汰。
  - ``allkeys-lru`` ：根据LRU算法删除键，不管数据有没有设置超时属性，直到腾出足够空间为止。
    从数据集（server.db[i].dict）中挑选最近最少使用的数据淘汰
  - ``volatile-lfu`` ：根据LFU算法删除设置了超时属性（expire）的键，直到腾出足够空间为止。如果没有可删除的键对象，回退到noeviction策略。
  - ``allkeys-lfu`` ：根据LFU算法删除键，不管数据有没有设置超时属性，直到腾出足够空间为止。
  - ``volatile-ttl`` ：根据键值对象的ttl属性，删除最近将要过期数据。如果没有，回退到noeviction策略。
    即从已设置过期时间的数据集（server.db[i].expires）中挑选将要过期的数据淘汰
  - ``volatile-random`` ：从已设置过期时间的数据集（server.db[i].expires）中任意选择数据淘汰
  - ``allkeys-random`` ：从数据集（server.db[i].dict）中任意选择数据淘汰
  - ``no-enviction`` ：禁止驱逐数据

  redis默认的策略就是 ``no-eviction`` 策略，如果想要配置的话，需要在配置文件中写这个配置： ::

    maxmemory-policy volatile-lru

删除过期对象
  Redis所有的键都可以设置过期属性，内部保存在过期字典中。由于进程内保存大量的键，维护每个键精准的过期删除机制会导致消耗大量的 CPU，对于单线程的Redis来说成本过高，因此Redis采用 *惰性删除* 和 *定时任务删除* 机制实现过期键的内存回收。

  - 惰性删除：顾名思义，指的是不主动删除，当用户访问已经过期的对象的时候才删除，最大的优点是节省cpu的开销，不用另外的内存和TTL链表来维护删除信息，缺点就是如果数据到期了但是一直没有被访问的话就不会被删除，会占用内存空间。
  - 定时任务删除：为了弥补第一种方式的缺点，redis内部还维护了一个定时任务， *默认每秒运行10次* 。定时任务中删除过期逻辑采用了自适应算法，使用快、慢两种速率模式回收键。

  流程说明：

  1. 定时任务在每个数据库空间随机检查20个键，当发现过期时删除对应的键。
  2. 如果超过检查数25%的键过期，循环执行回收逻辑直到不足25%或 运行超时为止，慢模式下超时时间为25毫秒。
  3. 如果之前回收键逻辑超时，则在Redis触发内部事件之前再次以快模 式运行回收过期键任务，快模式下超时时间为1毫秒且2秒内只能运行1次。
  4. 快慢两种模式内部删除逻辑相同，只是执行的超时时间不同。

Redis 持久化
:::::::::::::::::::
Redis提供了不同级别的持久化方式，一种是 **RDB** ，一种 **AOF** 。 *可以同时开启两种持久化方式* , 在这种情况下, 当redis重启的时候会优先载入 *AOF文件* 来恢复原始的数据,因为在通常情况下AOF文件保存的数据集要比RDB文件保存的数据集要 *完整* .

RDB
  在指定的时间间隔能对数据进行 *快照存储* (隔一段时间,把内存里的数据转存在硬盘里的文件)

  优点：

  - RDB是一个 *非常紧凑* 的文件，它保存了某个时间点得数据集， *非常适用于数据集的备份* ，比如您可以在每个小时报保存一下过去24小时内的数据，同时每天保存过去30天的数据，这样即使出了问题您也可以根据需求恢复到不同版本的数据集。
  - RDB是一个 *紧凑* 的 *单一* 文件， *很方便传送* 到另一个远端数据中心或者亚马逊的S3（可能加密）， *非常适用于灾难恢复* 。
  - RDB在保存RDB文件时父进程唯一需要做的就是 *fork出一个子进程* ,接下来的工作全部由子进程来做，父进程不需要再做其他IO操作，所以RDB持久化方式可以 *最大化redis的性能* 。
  - 与AOF相比， *在恢复大的数据集的时候，RDB方式会更快一些* 。

  缺点：

  - 在数据恢复上，总会丢失一些数据。如果您希望在redis意外停止工作（例如电源中断）的情况下丢失的数据最少的话，那么RDB不适合您.虽然您可以配置不同的save时间点(例如每隔5分钟并且对数据集有100个写的操作),是Redis要完整的保存整个数据集是一个比较繁重的工作,您通常会每隔5分钟或者更久做一次完整的保存,万一在Redis意外宕机,您可能会丢失几分钟的数据。
  - RDB 需要经常fork子进程来保存数据集到硬盘上, *当数据集比较大的时候,fork的过程是非常耗时的* ,可能会导致Redis在一些毫秒级内不能响应客户端的请求.如果数据集巨大并且CPU性能不是很好的情况下,这种情况会持续1秒,AOF也需要fork,但是您可以调节重写日志文件的频率来提高数据集的耐久度.

  如何触发RDB？参见 :ref:`the_snapshot_of_redis`

AOF
  每次对服务器写的操作,当服务器重启的时候会重新执行这些命令来恢复原始的数据,AOF命令 *以redis协议追加保存每次写的操作到文件末尾* 。 *Redis还能对AOF文件进行后台重写,使得AOF文件的体积不至于过大* 。

  优点：

  - 使用AOF 会让您的Redis更加耐久: 您可以使用不同的fsync策略：无fsync,每秒fsync,每次写的时候fsync.使用默认的每秒fsync策略,Redis的性能依然很好(fsync是由后台线程进行处理的,主线程会尽力处理客户端请求),一旦出现故障，您最多丢失1秒的数据.
  - AOF文件是一个只进行追加的日志文件,所以不需要写入seek,即使由于某些原因(磁盘空间已满，写的过程中宕机等等)未执行完整的写入命令,您也也可使用 ``redis-check-aof`` 工具修复这些问题.
  - Redis 可以在 AOF 文件体积变得过大时，自动地在后台对 AOF 进行重写： 重写后的新 AOF 文件包含了恢复当前数据集所需的最小命令集合。 整个重写操作是绝对安全的，因为 Redis 在创建新 AOF 文件的过程中，会继续将命令追加到现有的 AOF 文件里面，即使重写过程中发生停机，现有的 AOF 文件也不会丢失。 而一旦新 AOF 文件创建完毕，Redis 就会从旧 AOF 文件切换到新 AOF 文件，并开始对新 AOF 文件进行追加操作。
  - AOF 文件有序地保存了对数据库执行的所有写入操作， 这些写入操作以 Redis 协议的格式保存， 因此 AOF 文件的内容非常容易被人读懂， 对文件进行分析（parse）也很轻松。 导出（export） AOF 文件也非常简单： 举个例子， 如果您不小心执行了 FLUSHALL 命令， 但只要 AOF 文件未被重写， 那么只要停止服务器， 移除 AOF 文件末尾的 FLUSHALL 命令， 并重启 Redis ， 就可以将数据集恢复到 FLUSHALL 执行之前的状态。

  缺点：

  - 对于相同的数据集来说，AOF 文件的体积通常要大于 RDB 文件的体积。
  - 根据所使用的 ``fsync`` 策略，AOF 的速度可能会慢于 RDB 。 在一般情况下， 每秒 fsync 的性能依然非常高， 而关闭 fsync 可以让 AOF 的速度和 RDB 一样快， 即使在高负荷之下也是如此。 不过在处理巨大的写入载入时，RDB 可以提供更有保证的最大延迟时间（latency）。

参考文章：
  - `redis 持久化 AOF和 RDB 引起的生产故障 <https://www.cnblogs.com/yangxiaoyi/p/7806406.html>`_

.. _the_snapshot_of_redis:

Redis快照
:::::::::::::::::
在默认情况下， Redis 将数据库快照保存在名字为 dump.rdb的二进制文件中。您可以对 Redis 进行设置， 让它在“ N 秒内数据集至少有 M 个改动”这一条件被满足时， 自动保存一次数据集。您也可以通过调用 SAVE或者 BGSAVE ， 手动让 Redis 进行数据集保存操作。

比如说， 以下设置会让 Redis 在满足“ 60 秒内有至少有 1000 个键被改动”这一条件时， 自动保存一次数据集:save 60 1000

这种持久化方式被称为快照 snapshotting.

快照的触发方式：

  - 配置文件中默认的快照配置： ::

      save 900 1
      save 300 10
      save 60 10000

  - 命令 ``save`` 或者是 ``bgsave`` ：

    - **SAVE** ：save时只管保存，其它不管，全部阻塞
    - **BGSAVE** ：Redis会在后台异步进行快照操作，快照同时还可以响应客户端请求。可以通过lastsave命令获取最后一次成功执行快照的时间

应用场景
^^^^^^^^^^^^^^^^^^^
- 数据库缓存
- 共享Session
- 队列

数据库缓存
:::::::::::::::::::
什么数据可以放缓存？
###########################
- 不需要实时更新但是又极其消耗数据库的数据。
- 需要实时更新，但是数据更新的频率不高的数据。
- 每次获取这些数据都经过复杂的处理逻辑，比如生成报表。

什么数据不可以放缓存？
###########################
这类数据包括比如涉及到钱、密钥、业务关键性核心数据等。
但是，如果你发现系统里面的大部分数据都不能使用缓存，这说明架构本身很可能出了问题。

如何解决一致性和实时性的问题？
##################################
具体请看这里： :ref:`the_example_of_date_update_in_redis`

使用技巧
^^^^^^^^^^^^^^^^^^^

批量提交
:::::::::::::::::::
以String类型为例

可以使用 ``multiSet`` 方法，一次提交多个修改。

.. code-block:: java

  HashMap<String, String> content = new HashMap<String, String>();
  content.put("stringkey1", "value1");
  content.put("stringkey2", "value2");
  content.put("stringkey3", "value3");
  content.put("stringkey4", "value4");
  content.put("stringkey5", "value5");
  content.put("stringkey6", "value6");
  content.put("stringkey7", "value7");
  content.put("stringkey8", "value8");
  content.put("stringkey9", "value9");
  redisTemplate.opsForValue().multiSet(content);

也可以使用通道，一次连接交互多个命令：

.. code-block:: java

  List<Object> list = redisTemplate.executePipelined(new RedisCallback<String>() {
      @Override
      public String doInRedis(RedisConnection connection) throws DataAccessException {
          StringRedisConnection conn = (StringRedisConnection)connection;

          for(String key: keys) {
              conn.get(key);
          }

          return null;
      }
  });

  // return strs;
  List<String> strlist = list.stream()
          .map(obj -> obj.toString())
          .collect(Collectors.toList());

  return strlist.toArray(new String[strlist.size()]);

问题总结
^^^^^^^^^^^^^^^^^^^
- 缓存雪崩：因为数据过期引起的。——结果是加重数据库负担
- 缓存穿透：因为Redis不存储“select结果为空”的数据，引起的。——结果是加重数据库负担。

缓存雪崩
:::::::::::::::::::
缓存雪崩是由于原有缓存失效（过期），新缓存未到期。所有请求都去查询数据库，而对数据库CPU和内存造成巨大压力，严重的会造成数据库宕机。从而形成一系列连锁放映，造成整个系统的崩溃。

解决方法：

- 一般并发量不是很多的时候，使用最多的解决方案是加锁排队。

  不过，加锁排队只是为了减轻数据库的压力，并没有提高系统吞吐量。假设在高并发下，缓存重建期间key是锁着的，这是过来1000个请求999个都在阻塞的。同样会导致用户等待超时，这是个治标不治本的方法。

- 给每一个缓存数据增加相应的缓存标记，记录缓存的是否失效，如果缓存标记失效，则更新数据缓存。

  - **缓存标记** ：记录缓存数据是否过期，如果过期会触发通知另外的线程在后台去更新实际key的缓存。
  - **工作机制** ：缓存数据的过期时间比缓存标记过期时间的 **两倍** ，例：标记缓存时间30分钟，数据缓存设置为60分钟。 这样，当缓存标记key过期后，实际缓存还能把旧数据返回给调用端，直到另外的线程在后台更新完成后，才会返回新缓存。

缓存穿透
:::::::::::::::::::
缓存穿透是指用户查询数据，在数据库没有，自然在缓存中也不会有。这样就导致用户查询的时候，在缓存中找不到，每次都要去数据库再查询一遍，然后返回空。这样请求就绕过缓存直接查数据库，这也是经常提的缓存命中率问题。

解决方法：

- 如果查询数据库也为空，直接设置一个默认值存放到缓存，这样第二次到缓冲中获取就有值了，而不会继续访问数据库，这种办法最简单粗暴。
- 也可以单独设置个缓存区域存储空值，对要查询的key进行预先校验，然后再放行给后面的正常缓存处理逻辑。

缓存预热
:::::::::::::::::::
缓存预热就是系统上线后，将相关的缓存数据直接加载到缓存系统。这样避免，用户请求的时候，再去加载相关的数据。

解决思路

- 直接写个缓存刷新页面，上线时手工操作下。
- 数据量不大，可以在WEB系统启动的时候加载。
- 定时刷新缓存。

Session过时时间更新问题
::::::::::::::::::::::::::
Redis存储Session，需要每次调用Session时更新Redis里Session的过时时间，这样无疑增加了Redis的服务。推荐做法是：

- 借助Spring MVC本地的Session管理机制。将本地Session的有效时间调整为Redis中Session有效时间的一半，这样在本地Session失效的情况下才会访问Redis的Session，同时维持一个Session更新队列，每个一段时间（如五分钟）向Redis更新一次Session的有效时间。

  Spring Boot 的 ``@Cacheable`` 提供类似的功能。

缓存更新
:::::::::::::::::::
策略：

- 定时去清理过期的缓存。

  缺点：维护大量缓存的key是比较麻烦的

- 当有用户请求过来时，再判断这个请求所用到的缓存是否过期，过期的话就去底层系统得到新数据并更新缓存。

  缺点：每次用户请求过来都要判断缓存失效，逻辑相对比较复杂

两者各有优劣，具体用哪种方案，需要根据自己的应用场景来权衡。

- 预估失效时间
- 版本号（必须单调递增，时间戳是最好的选择）
- 提供手动清理缓存的接口。

.. _the_example_of_date_update_in_redis:

方案举例
#####################
Redis（主从）+ RabbitMQ + 缓存清理服务
  缓存清理作业订阅 RabbitMQ消息队列，一有数据更新进入队列，就将数据重新更新到Redis缓存服务器。

  |example_for_data_update_in_redis|

  当然，也有的方案是数据库更新完成之后，立马去更新相关缓存数据。这样就不需要MQ 和 缓存清理作业。不过，这同时也增加了系统的耦合性。具体得看自己的业务场景和平台大小。

字符串的序列化
^^^^^^^^^^^^^^^^^^^^^
使用 ``redisTemplate.opsForValue().set("testValue", "peter is a gread man.");`` 后，到 ``redis-cli`` 中查询会发现，新建的 *key* 并非 ``testValue`` ，而是 ``\xac\xed\x00\x05t\x00\ttestValue`` 。原因是 *spring-redis* 默认使用 *jedis* 的 ``JdkSerializationRedisSerializer`` 。

解决方法是：

#. 直接使用： ``org.springframework.data.redis.core.StringRedisTemplate`` 。

#. 手动定义序列化的方法。 *spring-data-redis* 中还提供了一个序列化的类专门针对string类型的序列化 ``org.springframework.data.redis.serializer.StringRedisSerializer`` 。

   配置文件中这样使用：

   .. code-block:: xml

     <bean id="redisTemplate" class="org.springframework.data.redis.core.RedisTemplate"
       p:connection-factory-ref="jedisConnectionFactory">
       <property name="keySerializer">
         <bean class="org.springframework.data.redis.serializer.StringRedisSerializer" />
       </property>
       <property name="valueSerializer">
         <bean class="org.springframework.data.redis.serializer.StringRedisSerializer" />
       </property>
       <property name="hashKeySerializer">
         <bean class="org.springframework.data.redis.serializer.StringRedisSerializer" />
       </property>
       <property name="hashValueSerializer">
         <bean class="org.springframework.data.redis.serializer.StringRedisSerializer" />
       </property>
     </bean>

.. |example_for_data_update_in_redis| image:: /images/special_subject/distributed/002_example_for_data_update_in_redis.png
   :width: 80%
