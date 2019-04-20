《精通Hadoop》
=====================================
.. toctree::
   :maxdepth: 1

   temps01
   temps02
   temps03
   temps04
   temps05
   temps06
   temps07
   temps08

随笔（待整理）
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
- Hadoop被应用与做不同的事情： `P23`

  - 用来产生图标和跟踪利用率的统计数据。
  - 云服务提供商用其做日志处理。
  - 用其创建网页索引，执行复杂的广告植入和内容优化算法。

- 几个重点概念（推荐对应记忆）：

  - ``NameNode`` 、 ``DataNode`` 、 ``JournalNode`` 、 ``block report`` 、 ``block storage``

    - **NameNode** 、 **DataNode** 与 **Resource Manager** 、 **Application Manager** 、 **Node Manager** 有什么关联？

  - **数据校验** 、 **数据复制** 、 **数据镜像** 、 **快照** 。

    - 注意他们的不同与互补。
    - 其中 **数据复制** 与 **数据镜像** 有什么区别与优劣？

- TODO: 去了解一下 ``MapReduce``
- **ZooKeeper** 似乎是一个“高可用的监听工具”，具体了解一下。
- 本书中，通过对比 **1.x** 与 **2.x** ，介绍了Hadoop的几个重点改进。这种方式值的借鉴，通过对比两个不同的东西，让新手也能了解内部构造。通过对比进行介绍（学习）比“直接”的、“单独”的介绍要更介绍（学习）让人理解！
- 函数式编程：一个将数据函数作为计算单元的计算机科学分支。（ **不懂** ）

  - 函数属性： **不可修改** 和 **无状态** 。

- Hadoop中 **块存储** 与 **文件** 这两个概念有什么联系？那么有没有 **数据块** 这个概念？另外在 ``MapReduce`` 的 ``InputFormat`` 中还提到了 **逻辑块** 这个概念又与前面几个概念有什么区别？
- **MapReduce进阶** 中提到的 **分片** 与 **逻辑分块** 应该是一组概念， **分片** 的结果是 **逻辑块** ，这个动作由 **InputFormat** 进行。
- 书中多次提到 **驱动类** ，什么是驱动类？
- 输入的过滤，即可以在 **Map** 中进行，又可以在 **Map** 前执行。我们推荐 ``尽量`` 使用后者，因为它能减轻Map的压力。书中通过 `继承` **Configured** 并 `实现` **PathFilter** ，使得类 ``MasteringHadoopPathAndSizeFilter`` 具备的过滤功能（在 **Map** 之前）。
  
  驱动类中的重要代码： ::

    FileInputFormat.setInputPathFilter(job, MasteringHadoopPathAndSizeFilter.class);

  类 ``MasteringHadoopPathAndSizeFilter`` 中重要的方法实现： ::

    public void setConf(Configuration conf){ ... }
    public boolean accept(Path path){ ... }

- Hadoop与Flink是如何配合的？
- **Hadoop** 中 **Map** 节点的 **数据本地化** 问题（locality problem），是什么东西？
- **Map** 的结果数据，是先进入缓存，在写入磁盘的。
- **Map** 的结果要先进行分区，为什么“分区”？分区的依据是什么？
- **Combiner** 与 **Reducer** 的区别。
  
  - **Combiner** 的目的：主要是为了削减Mapper的输出从而减少网络带宽和Reducer之上的负载。
  - **Combiner** 的输入和 **Reduce** 的完全一致，输出和 **Map** 的完全一致
  - **Combiner** 的应用场景示例：

    - 如果我们有10亿个数据，Mapper会生成10亿个键值对在网络间进行传输，但如果我们只是对数据求最大值，那么很明显的Mapper只需要输出它所知道的最大值即可。这样做不仅可以减轻网络压力，同样也可以大幅度提高程序效率；
    - 使用专利中的国家一项来阐述数据倾斜这个定义。这样的数据远远不是一致性的或者说平衡分布的，由于大多数专利的国家都属于美国，这样不仅Mapper中的键值对、中间阶段(shuffle)的键值对等，大多数的键值对最终会聚集于一个单一的Reducer之上，压倒这个Reducer，从而大大降低程序的性能。

  - **Combine** 操作类似于： ``opt(opt(1, 2, 3)`` , ``opt(4, 5, 6))`` 。如果opt为求和、求最大值的话，可以使用，但是如果是求中值的话，不适用。

- Reduce的内连接实现
  1. 实现一个 ``Writeable`` 数据类型，从而用 **键** 为数据集打标签。
  2. 实现一个自定义的 ``Partitioner`` 类。 ``Partitioner`` 必须只能基于连接的原始键做数据分区。
  3. 写一个自定义的分组比较器。同分区类一样，分组只能基于连接的原始键进行。
  4. 需要为每种输入数据集都写一个 ``Mapper`` 类。
  5. ``Reducer`` 类利用了二次排序的优点产生连接后的数据。
  6. 驱动程序需要指定所有Reduce侧连接需要的自定义数据类型。

- Map侧的连接
  - 除了参与连接的键外，所有的输入都必须按照连接键排序。输入的各种数据集必须拥有相同的分区数；所有具有相同键的记录需要放在同一个分区中
  - 如果其中的一个数据集足够小，旁路的分布式通道（例如分布式缓存）可以用在Map侧的连接中。

第三章

- Pig的示例 ::

    cc = load 'countrycodes.txt' using PigStorage(',') as (ccode:chararray, cname:chararray);
    ccity = load 'worldcitiespop.txt' using PigStorage(',') as (ccode:chararray, cityName:chararray, cityFullName:chararray, region:int, population:long, lat:double, long:double);
    filteredCcity = filter ccity by population is not null;
    joinCountry = join cc by ccode, ccity by ccode;
    generateRecords = foreach joinCountry generate cc::cname, ccity::cityName, ccity::population;
    groupByCountry = group generateRecords by cname;
    populationByCountry = foreach groupByCountry generate group, SUM(generateRecords.population);

- 执行模式： **交互式模式** 、 **批处理模式** 、 **嵌入式模式**
- 连接： **内连接** 、 **外连接** 、 **交叉连接** 、 **半连接** 、 **θ连接**  、 **模糊连接**

  - **内连接** 是基于相等的连接键， **θ连接** 是使用不等式来连接的。

- 特殊连接： **分段复制连接** 、 **倾斜连接**
- 数据类型： **int** 、 **long** 、 **float** 、 **double** 、 **chararray** 、 **bytearray** 、 **Map** 、 **Tuple** 、 **Bag**
- 命令： **DESCRIBE** 、 **EXPLAIN** 、 **ILLUSTRATE** , **REGISTER**
- 操作符： **LOAD** 、 **FILTER** 、 **FOREACH** 、 **FLATTEN** 、 **嵌套FOREACH操作符**  、 **COGROUP**  、 **CROSS**

  - 类SQL操作符： **LIMIT** 、 **ORDER** 、 **DISTINCT** 、 **join** 、 **GROUP**

- 运算函数：

  - 聚合函数: **Algebraic接口** 、 **Accumulator接口** 。
  - 过滤函数。

总结
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
- 对于 `MapReduce` 的优化中，比较重要的：

  - Combiner的使用。
  - 旁路通道
  - Map侧连接（前提条件：分区、排序）。
  - ``倾斜连接`` 的处理。
