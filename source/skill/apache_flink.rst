Apache Flink
=======================

.. toctree::
   :maxdepth: 1
   :caption: 视频教程:

链接： http://flink-china.org/

初次操作
^^^^^^^^^^^^^^^^^^^^^^^^
- 启动任务 ::
  
    ./bin/start-cluster.sh

  监控网址 ::

    http://localhost:8081

- 监控网页对外部可见

  修改文件 `conf/masters` 将 ``localhost:8081`` 改为 ``0.0.0.0:8081``

  修改文件 `conf/flink-conf.yaml` 将 ``jobmanager.rpc.address: localhost`` 改为 ``jobmanager.rpc.address: 0.0.0.0`` （这个改动似乎没有必要）

测试文件
^^^^^^^^^^^^^^^^^^^^^^^^
- SocketWindowWordCount.jar

  监控端口，统计5s内输入文本的单词个数。

- WordCount.jar
- IncrementalLearning.jar
- Iteration.jar
- Kafka010Example.jar
- SessionWindowing.jar
- StateMachineExample.jar
- TopSpeedWindowing.jar
- Twitter.jar
- WindowJoin.jar
