读书笔记 2018-10-06
=====================================

.. toctree::
   :maxdepth: 1

时间：10月06日

书籍：《精通Hadoop》

进度：142页 - 200页

----------

备忘
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
- **Hadoop** 与 **HDFS** 不是一回事。 **Hadoop** 还支持其他文件系统。
- **HDFS** 的“联合功能”，需要分布式部署。

第七章 基于YARN的Storm——Hadoop中的低延时处理
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
- **流式处理** 的有个缺点，就是如果我们在统计、分析中出了点错误，就没有办法从中恢复。但是如果用批处理的方法，我们则有机会对我们的计算做双重检查。
- **流式处理系统** 在那些以低延迟为主要目的的应用中大放光彩。比如说：股市中的算法交易、社交网络动态的分析、智能广告、基于位置的应用、基于传感器网络的应用等。
- Storm集群中有两种不同类型的节点： **Master节点** 、 **Worker节点** 。
- **Master节点** 其上面会运行 **Nimbus** 守护进程，是一种具有以下三大关键功能的中央节点：将执行代码分发给集群中不同的worker节点；调度任务，将任务分配给Apache Storm集群中空闲的worker节点；监控集群的错误，并采取相应的措施。
- **Worker节点** 其上面会运行 **Supervisor** 守护进程，其职责如下：听从 **Master Nimbus** 守护进程的指挥；基于Nimbus的指挥，启动和停止worker进程；每个worker进程执行topology中的一个子集。
- **Nimbus** 和 **Supervisor** 守护进程间实际的协调工作则通过 **Zookeeper** 集群完成。
- **Zookeeper** 是一个开源的集中的协调服务，关注于配置管理、节点的同步，以及分布式系统中服务的命名。它为大型分布式系统中的各种协调问题提供了一个开源的解决方案。
- **Storm** 中有三个抽象，分别是： ``spout`` 、 ``bolt`` 、 ``topology`` 。
- **Storm** 中，topology被定义为 **Thrift** 结构。
- **Storm** 中使用的数据抽象是流。流是一组有序且无限的tuple。

Apache Storm的开发
####################
具体代码见 `147页` 。

重点摘录：

- ``SpoutOutputCollector`` 对象的主要特征是它可以用ID给消息打上标记，标记消息是发送成功还是失败。
- ``BaseRichSpout.nextTuple()`` 方法是非阻塞调用。
- Apache Storm自带的七种流分区模式： **Shuffle grouping** 、 **Fields grouping** 、 **All grouping** 、 **Global grouping** 、 **None grouping** 、 **Direct grouping** 、 **Local or Shuffle grouping** 。
- 使用类似下面的命令可以将topology提交到Storm集群： ::

    storm jar MasteringStormOnYarn-1.0-SNAPSHOT-jar-with-dependencies.jar MasteringStorm.MasteringStormTopology worldcitiespop.txt

- 在YARN上安装Apache Storm的步骤可以在 `154页` 查看。

重点类：

- ``BaseRichSpout`` ：继承这个类，以便将数据以流的形式导入程序。重点方法： ``open(Map map, TopologyContext topologyContext, SpoutOutputCollector spoutOutputCollector)`` 、 ``nextTuple()`` 、 ``declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer)`` 。
- ``BaseRichBolt`` ： 继承这个类，以实现“计算单元”。重点方法： ``prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector)`` 、 ``execute(Tuple tuple)`` 、 ``declareOutputFields(OutputFieldsDeclarer declarer)`` 。
- ``TopologyBuilder`` ：此对象用来创建topoloty。重点方法： ``setSpout(String spoutName, BaseRichSpout spout, int i)`` 、 ``setBolt(String blotName, BaseRichBlot blot, int i)`` 。
- ``TopologyBuilder.BoltGetter`` ：其对象是 ``TopologyBuilder.setSpout()`` 、 ``TopologyBuilder.setBlot()`` 的返回值，这个对象允许不同类型的分组。分组也可以被认为就是流分区指令。重点方法： ``shuffleGrouping(String spoutOrBlogName)`` 、 ``fieldsGrouping(String spoutOrBlogName, Fields fields)`` 、 ``createTopology()`` 。
- ``StormSubmitter`` ：这是一个辅助类，其静态方法 ``submitTopology`` 可以将topology提交到Apache Storm集群。重点方法： ``submitTopology(String taskName, Config conf, Topology topology)`` 。

辅助类：

- ``SpoutOutputCollector`` ：它的对象用于将tuple发送到输出流去。在 ``BaseRichSpout`` 中需要用到它，此对象通过 ``BaseRichSpout.open(...)`` 传入 ``BaseRichSpout`` ，在 ``BaseRichSpout.nextTuple()`` 中一般还会被用到，所以常常需要保存在 ``BaseRichSpout`` 对象的属性中。重点方法： ``emit(Values values)`` 。
- ``OutputCollector`` ：它的对象用于将tuple发送到输出流去。在 ``BaseRichBlot`` 中需要用到它，此对象通过 ``BaseRichBlot.prepare(...)`` 传入 ``BaseRichBlot`` ，在 ``BaseRichBlot.nextTuple()`` 中一般还会被用到，所以常常需要保存在 ``BaseRichBlog`` 对象的属性中。重点方法： ``emit(Values values)`` 。
- ``OutputFieldsDeclarer`` ：此对象一般在 ``BaseRichSpout.declareOutputFields(OutputFieldsDeclarer declarer)`` 中使用，用于指定消息的模式和ID。重点方法： ``declare(Fields)`` 。
- ``Fields`` ：记住它， ``BaseRichSpout.declareOutputFields(...)`` 方法可以制定消息的 **模式** 。
- ``Values`` ： ``SpoutOutputCollector.emit()`` 和 ``OutputCollector.emit()`` 方法需要的参数，表示 **topology** 内部流动的数据对象，也可以将它们看做 **tuple** 。构造方法是 ``Values(Object ...)`` 。他一般与 ``Fields`` 像匹配。


视图
####################
Apache Storm集群的一个高层次视图： |storm_cluster_high_level_view|



  .. |storm_cluster_high_level_view| image:: /images/except_books_mastering_storm_cluster_high_level_view.png
     :width: 100%

第8章 云上的Hadoop
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
介绍在 **亚马逊AWS** 和 **微软Azure** 云上的Hadoop。略过。

第9章 HDFS替代品
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
- 底层文件系统对MapReduce计算模型的并行性和可伸缩性有极大的影响。大多数Hadoop发行版中默认的文件系统都是HDFS。
- HDFS的有点是物美价廉；有良好的社区支持；HDFS优化了MapReduce的工作负载，它支持性能极高的顺序读写，这是MapReduce作业典型的数据访问模式。
- HDFS的缺点： HDFS是不可变的，即不能修改文件； HDFS不可被挂载；HDFS对流式读取有所优化，但不擅长随机访问文件；尽管HDFS对MapReduce作业有所优化，但是引入YARN后Hadoop演变成了一个通用集群计算框架。
- 亚马逊的 **简单存储服务** 又称 **S3** ，是HDFS不错的替代品。
- Hadoop支持用户自己实现 **文件系统** ，具体操作/代码见 ``179页`` 。

第10章 HDFS联合
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
- **HDFS联合** 是Hadoop的一个特性，它使Hadoop能够管理多个命名空间，这样在共享集群场景下使用起来更方便。这个特性隔离了存储和命名空间管理。
- **HDFS联合** 的关键是允许集群中运行多个 **NameNode** ，这些 **NameNode** 彼此独立，却共享 **DataNode** 。
- 每个DataNode都向集群中的所有NameNode发送心跳和块报告信息。
- 块联合正是基于块池（block pool）的概念而提出的。（如 **架构图** ）
- 当一个NameNode退出集群或删除一个命名空间时， DataNode会删除与该命名空间卷块池相关的所有块。（一个命名空间连同其块池称为命名空间卷）

HDFS联合的架构图： |architecture_of_a_federated_HDFS_cluster|

  .. |architecture_of_a_federated_HDFS_cluster| image:: /images/except_books_mastering_hadoop_architecture_of_a_federated_HDFS_cluster.png
     :width: 100%

HDFS联合的好处
  最重要的优点是赋予了NameNode水平扩展的能力，使得拥有大量小文件的集群获益匪浅。

  扩展读写吞吐量这个优势是单点 NameNode无法做到的。

  拥有不同的NameNode和命名空间使隔离变得轻松简单。

  HDFS联合功能还能把块存储服务视为通用块存储。

  HDFS联合架构简单性的另一个体现是其向后兼容的特性。

部署联合NameNode
########################
部署步骤见 `193页` 。

HDFS的高可用性
########################
- HDFS中有两个重要的文件 **edits日志文件** 和 **fsimage文件** 。他们与HDFS的 **容灾** 、 **重启** 息息相关。具体见 `195页` 。
- **检查节点** ：不仅会定期地从NameNode那里获取fsimage和edits文件的更新，而且还合并edits内容到fsimage文件里并上传到NameNode。这有助于NameNode迅速从故障中恢复。
- ``Hadoop2.x`` 中引入了 **热备份NameNode** ，它的重要实现就是通过 **JournalNode** 和 **NFS** 实现 **共享edits** 。
- NameNode高可用性背后的总体策略是在活跃NameNode和热备NameNode之间共享edits文件。

脑裂现象
  如果ZKFailoverController模块遇到了故障，那么活跃和热备NameNode可能都会认为自己处于活跃状态，这种情况称为脑裂现象。

  脑裂现象会使命名空间处于不一致的状态，因为两个NameNode会产生有冲突的变化。

  解决这个问题的办法是让活跃NameNode停止对系统产生变化。 QJM对于故障转移的策略是构建起一组JournalNode作为围栏，并仅允许单个NameNode向这些节点写入信息。

HDFS 块放置策略
########################
- HDFS不会复制整个文件；相反，它会把文件分成固定大小的块并分散保存在集群中。
- HDFS默认复制因子是3。
- **智能化块放置** 是HDFS最显著的特性。放置策略是机架感知的，能识别块存放的物理位置。
- 当复制因子是3时，默认的机架放置策略是：第一个副本放置在机架内的节点上，第二个副本放置在同机架内的不同节点上，第三个副本整个放置在不同机架的节点上。
- 写的时候，第一个块会写在（集群内的）客户端所在的节点上。如果客户端不在集群中，会随机选出第一个节点。
- 当触发读操作时， NameNode会尝试把读操作指向最接近的节点。
- 现在HDFS提供了可插拔的块放置策略。具体实现方式见 `199页` 。
