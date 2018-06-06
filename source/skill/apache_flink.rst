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

- 提交Flink程序的方式 ::

    ./bin/flink run examples/streaming/SocketWindowWordCount.jar --port 9000

- 借助Maven生成一个Flink项目 ::

    mvn archetype:generate -DgroupId=peter.flink.test -DartifactId=test -DarchetypeGroupId=org.apache.flink -DarchetypeArtifactId=flink-quickstart-java -DarchetypeVersion=1.5.0

API
^^^^^^^^^^^^^^^^^^^^^^^^
Stream与Batch
########################
- `Stream`

  - `StreamingExecutionEnvironment` 、 `DataStream`

- `Batch`

  - `ExecutionEnvironment` 、 `DataSet`

测试文件
^^^^^^^^^^^^^^^^^^^^^^^^
- SocketWindowWordCount.jar

  监控端口，统计5s内输入文本的单词个数。

- WordCount.jar

  统计文件的总的文本。

- Iteration.jar

  记住 `斐波那契数列` ，举例说明如何使用 `递归` ，更详细一些是： 教会你使用 `OutputSelector` ，这个东西可以将一次结算的结果作为 `源` ，重新放入到 `流` 中。

- IncrementalLearning.jar
- Kafka010Example.jar
- SessionWindowing.jar
- StateMachineExample.jar
- TopSpeedWindowing.jar
- Twitter.jar
- WindowJoin.jar

问题
^^^^^^^^^^^^^^^^^^^^^^^^^
- 很多时候在 ``Step`` (implements MapFunction) 中的log，没有打印到log文件中。这是为什么？

  - 此问题是在文件 ``org.apache.flink.streaming.examples.iteration.IterateExample`` 中发现的。
  - 此问题多出现在，jar任务第二次上传之后（完全不打印）；第一次上传运行的时候（只有部分打印）；对相关log文件clean数据之后（这个原因不太可能）。

- 了解 `StreamExecutionEnvironment` 、 `DataStream` 、 `IterativeStream` 、 `SplitStream` 、 `inputStream` 、 `inputStream` 的工作机制？
