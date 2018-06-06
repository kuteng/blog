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

transformation的常用类型
##############################
- ``map(MapFunction<T, R>)``
- ``filter(FilterFunction<T>)``
- ``flatMap(FlatMapFunction<T,R>)``

备忘
^^^^^^^^^^^^^^^^^^^^^^^^
- 使用 ``FlatMapFunction`` 时需要注意，类里面的两个“泛型”，分别对应输入数据的格式和输出数据的格式。
- 注意 ``keyBy`` 和 ``groupBy`` 方法的参数是“可变参数”，即不止有一个参数。类似于下面： ::

    dataStream.keyBy("name");
    dataStream.keyBy("class", "name");
    dataStream.keyBy(0);
    dataStream.keyBy(0, 1);

  对于 `Tuples` 类型的数据似乎可以通过这两种方式使用 `keyBy` ::

    dataStream.keyBy(0);
    dataStream.keyBy("f0");
    dataStream.keyBy("f0.f2");

  后两种需要验证！(TODO)

- 如果“采集结果”是Object，那么 ``keyBy`` 的参数类型是 ``String`` ；如果“采集结果”是 ``Tuples`` ，那么 ``keyBy`` 的参数类型是 ``Integer`` 。
- ``keyBy()`` 方法第三种用法： ::

    public class WC {public String word; public int count;}
    DataStream<WC> words = // [...]
    KeyedStream<WC> keyed = words
      .keyBy(new KeySelector<WC, String>() {
         public String getKey(WC wc) { return wc.word; }
       });

- Flink程序中，转换的用法：

  - 实现接口（如 ``MapFunction`` ） ::

      class MyMapFunction implements MapFunction<String, Integer> {
        public Integer map(String value) { return Integer.parseInt(value); }
      };
      data.map(new MyMapFunction());

  - 匿名实现接口 ::

      data.map(new MapFunction<String, Integer> () {
        public Integer map(String value) { return Integer.parseInt(value); }
      });

  - Java 8 Lambdas ::

      data.filter(s -> s.startsWith("http://"));
      data.reduce((i1,i2) -> i1 + i2);

  - 集成 ``Rich functions`` （如 ``RichMapFunction`` ） ::

      class MyMapFunction extends RichMapFunction<String, Integer> {
        public Integer map(String value) { return Integer.parseInt(value); }
      };
      data.map(new MyMapFunction());

    ::

      data.map(new RichMapFunction<String, Integer>() {
        public Integer map(String value) { return Integer.parseInt(value); }
      });

- Flink对于数据类型进行了一些限制：

  - Java Tuples and Scala Case Classes
  - Java POJOs

    - 这个类的权限必须是 `public` 的。
    - 这个类必须有一个默认构造器（public且无参数）
    - 所有的属性必须是 `public` 或者具备 `get` 、 `set` 方法。
    - 所有属性的类型必须是 `Flink` 支持的。主要是 `Flink` 使用 `Avro <http://avro.apache.org>`_ 序列化这些对象（如 ``Date`` ）。

  - Primitive Types

    - 比如： ``String`` 、 ``Integer`` 、 ``Double`` 。

  - Regular Classes

    这是指 `Java` 或 `Scala` API中的常规类。不过这部分的内容我没有完全理解。这里是 `链接 <https://ci.apache.org/projects/flink/flink-docs-release-1.5/dev/api_concepts.html#general-class-types>`_

  - Values

    这部分其实我也没有懂。 `链接 <https://ci.apache.org/projects/flink/flink-docs-release-1.5/dev/api_concepts.html#values>`_

  - Hadoop Writables

    这部分其实我也没有懂。 `链接 <https://ci.apache.org/projects/flink/flink-docs-release-1.5/dev/api_concepts.html#hadoop-writables>`_

  - Special Types

    这部分其实我也没有懂。 `链接 <https://ci.apache.org/projects/flink/flink-docs-release-1.5/dev/api_concepts.html#special-types>`_

  - 这里还有一部分： `链接 <https://ci.apache.org/projects/flink/flink-docs-release-1.5/dev/api_concepts.html#type-erasure--type-inference>`_


- ``Tuples`` 调用内容的方式有： 

  - tuple.f4
  - tuple.getField(int position)

- 累加器(``Accumulator``)在你调试程序或者想了解更多内部数据的时候非常有用。 `更多细节 <https://ci.apache.org/projects/flink/flink-docs-release-1.5/dev/api_concepts.html#accumulators--counters>`_

  - ``IntCounter``, ``LongCounter`` and ``DoubleCounter``
  - ``Histogram``, 我特别渴望了解它的内部。(TODO)
  - 累加器需要在 ``AbstractRichFunction`` 的 ``open`` 方法中执行类似这样的操作： ::

      private IntCounter numLines = new IntCounter();
      ......
      getRuntimeContext().addAccumulator("num-lines", this.numLines);
      ....
      // other class
      myJobExecutionResult.getAccumulatorResult("num-lines");

   这里的 ``open`` 方法在 `API <https://ci.apache.org/projects/flink/flink-docs-release-1.5/api/java/org/apache/flink/api/common/functions/AbstractRichFunction.html#open-org.apache.flink.configuration.Configuration->`_ 中有更加“落地”的实现。我可以去参考一下。(TODO)

   同时 ``getRuntimeContext`` 的 `更多细节 <https://ci.apache.org/projects/flink/flink-docs-release-1.5/api/java/org/apache/flink/api/common/functions/AbstractRichFunction.html#getRuntimeContext-->`_ ，我也想了解一下。(TODO)

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
