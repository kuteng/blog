相关知识
=======================
Write Ahead Log (WAL)
^^^^^^^^^^^^^^^^^^^^^^^^^^^
HBase的 **Write Ahead Log (WAL)** 的工作机制

它是一种高并发、持久化的日志保存与回放机制。需要支持多线程日志写入。同时对于单个HBase客户端，它在WAL中的日志顺序，应该与这个客户端发起的业务数据写入请求的顺序一致。

为了保证高可靠，日志不仅要写入文件系统的内存缓存，而且应该尽快、强制刷到磁盘上（即WAL的Sync操作）。但是Sync太频繁，性能会变差。所以：Sync应当在多个后台线程中异步执行；频繁的多个Sync，可以合并为一次Sync——适当放松对可靠性的要求，提高性能。

WAL机制中，只有一个WAL日志消费线程。这样一个多生产者，单消费者的模式，决定了WAL日志并发写入时日志的全局唯一顺序。

总结：线程并发写入文件时，用队列来协调，保证日志写入的顺序；同时提供Sync() API确保日志写入的可靠性，同时避免频繁的Sync()操作影响性能。

HBase WAL架构图 |hbase-wal-structure-chart|

每个HRegionServer中都有一个HLog对象，HLog是一个实现Write Ahead Log的类，在每次用户操作写入MemStore的同时，也会写一份数据到HLog文件中，HLog文件定期会滚动出新的，并删除旧的文件（已持久化到StoreFile中的数据）。当HRegionServer意外终止后，HMaster会通过Zookeeper感知到，HMaster首先会处理遗留的 HLog文件，将其中不同Region的Log数据进行拆分，分别放到相应region的目录下，然后再将失效的region重新分配，领取 到这些region的HRegionServer在Load Region的过程中，会发现有历史HLog需要处理，因此会Replay HLog中的数据到MemStore中，然后flush到StoreFiles，完成数据恢复

HBase WAL流程图 |hbase-wal-flow-chart|

# images/HBase WAL架构图.png 

.. |hbase-wal-structure-chart| image:: /note/images/HBase\ WAL架构图.png 
   :width: 100%
.. |hbase-wal-flow-chart| image:: /note/images/hbase-wal-flow-chart.jpeg
   :width: 100%

Hadoop Sequence
^^^^^^^^^^^^^^^^^^^^^^^^^^^
- 二进制格式。row key, family, qualifier, timestamp, value等HBase byte[]数据，都原封不动地顺序写入文件。
- Sequence文件中，每隔若干行，会插入一个16字节的魔数作为分隔符。这样如果文件损坏，导致某一行残缺不全，可以通过这个魔数分隔符跳过这一行，继续读取下一个完整的行。
- 支持压缩。可以按行压缩。也可以按块压缩（将多行打成一个块）

Kerberos安全体系
^^^^^^^^^^^^^^^^^^^^^^^^^^^
Kerberos的四张图片，很好的介绍了这个体系。 摘自 `Kerberos安全体系详解---Kerberos的简单实现 <https://www.cnblogs.com/wukenaihe/p/3732141.html>`_

Kerberos总体流程图 |kerberos_flow_chart_global|

Kerberos中用户与Authentication Server交互流程图 |kerberos_flow_chart_user_with_authentication_server|

Kerberos中用户与Ticket Granting Server交互流程图 |kerberos_flow_chart_user_with_ticket_granting_server|

Kerberos中用户与Http Server交互流程图 |kerberos_flow_chart_user_with_http_server|

.. |kerberos_flow_chart_global| image:: /note/images/kerberos_flow_chart_global.png 
   :width: 100%
.. |kerberos_flow_chart_user_with_authentication_server| image:: /note/images/kerberos_flow_chart_user_with_authentication_server.png 
   :width: 100%
.. |kerberos_flow_chart_user_with_ticket_granting_server| image:: /note/images/kerberos_flow_chart_user_with_ticket_granting_server.png 
   :width: 100%
.. |kerberos_flow_chart_user_with_http_server| image:: /note/images/kerberos_flow_chart_user_with_http_server.png 
   :width: 100%
