Apache Hadoop
==============================================

.. toctree::
   :maxdepth: 1
   :caption: 细说:

   hadoop/code_example
   hadoop/deploy_guide

概念
^^^^^^^^^^^^^^^^^^^
- **MapReduce** ：是一种为并行和分布式数据处理而设计的编程模式,它包含两个步骤: *Map* 和 *Reduce* 。
- Hadoop中 *MapReduce* 的过程简述：每个 *MapReduce* 的作业都有输入的数据,一个 *Map* 任务对应这些数据的一个 **分片** ( *split*)。 *Map* 任务循环调用 ``map`` 函数处理数据,而这些数据是以 *键-值对* ( *key-value pair* )的方式呈现的。 ``map`` 函数将数据从一种形式转换为另一种形式,每个 *Map* 任务中间输出的数据将被 **shuffle** (一种打散操作,类似于扑克牌的洗牌)并排序,然后传送给下游的 *Reduce* 任务。拥有相同键值的中间数据(intermediate data)将集中到同一个 *Reduce* 任务。 *Reduce* 任务调用 ``reduce`` 函数处理键及其对应的所有值,然后将输出聚集并排序。

  Map步骤是最高并行度的。它一般用来实现类似于过滤、排序和转换数据这样的操作。

  Reduce操作一般用于实现数据的汇总操作。

  Hadoop也提供了 *分布式缓存* (DistributedCache)等特性作为分布数据的一个 *旁路通道* ,以及 *计数器* (counter)来收集作业相关的全局状态。

- **Shuffle** ：一种打散操作,类似于扑克牌的洗牌。它在 *Map* 中进行。
- **InputSplit** ：数据 **分片** 又称为 **逻辑块** ，用于盛放文件里的数据，以便发送给Map处理。
- **InputFormat** 的主要功能是：

  - 输入数据的有效性检测。例如,检查指定路径的文件是否存在。
  - 将输入数据切分为逻辑块( InputSplit )，并把它们分配给对应的Map任务。
  - 实例化一个能在每个 InputSplit 类上工作的 RecordReader 对象,并以 *键-值对* 方式生成数据。

- **RecordReader** 类：RecordReader 在 InputSplit 类内部执行,并将数据以 *键-值对* 的形式产生一条条的记录，将这些记录向 Map 传递。
- 其他


概念区分
^^^^^^^^^^^^^^^^^^^
- HDFS中的 **块** 概念与 MapReduce 中的 **InputSplit** 。

知识点
^^^^^^^^^^^^^^^^^^^
- 对并行和分布式处理而言,函数的属性(例如,不可修改和无状态)是非常有吸引力的, 它能在更低成本和复杂语义的情况下,提供很高的并行度和很强的容错性。
- 在实际应用中我们最好会约束每个文件的大小，避免出现大量过小文件。如果真出现这种情况，最好的选择是在 *MapReduce* 之前将它们合并为一个文件；次要选择才是使用 ``CombineFileInputFormat`` 类。

关于 InputSplit
:::::::::::::::::::::
- ``InputSplit`` 的主要属性：

  - 输入文件名
  - 分片数据在文件中的偏移量
  - 分片数据的长度(以字节为单位)
  - 分片数据所在的节点的位置信息

- ``CombineFileSplit`` 相比 ``InputSplit`` ，里面存储了包含多个偏移量和长度的多个路径。
- 基于分片所在位置信息和资源的可用性,调度器将决定在哪个节点上为一个分片执行对应的Map任务,然后分片将与执行任务的节点进行通信。
- 在HDFS中,当一个文件的大小少于HDFS的块容量时,每个文件都将创建一个 ``InputSplit`` 实例。
- 一般情况下, InputSplit 类受限于HDFS块容量的上限,除非最小的分片也比块容量还大(这是很罕见的情况,并且可能导致 **数据本地化** 的问题)。
- 对于那些被分割成多个块的文件(文件的大小多于块的容量)，将使用一个更为复杂的公式来计算 InputSplit 的数量。公式如下：

  ``InputSplitSize = Maximum(minSplitSize, Minimum(blocksize, maxSplitSize))`` ，其中：

  - minSplitSize: ``mapreduce.input.fileinputformat.split.minsize``
  - blocksize: ``dfs.blocksize``
  - maxSplitSize: ``mapreduce.input.fileinputformat.split.maxsize``

关于 RecordReader
:::::::::::::::::::::
- ``RecordReader`` 的边界会参考 ``InputSplit`` 的边界,但不是强制一致的。

  极端情况下,一个自定义的 RecordReader 类可以对整个文件进行读或写(但我们不建议这么做)。

  大部分时候,在 RecordReader 类与 InputSplit 类重合的情况下, RecordReader 类将对应一个 InputSplit 类,从而为Map任务提供完整的数据记录。

- 我们通常会在 ``RecordReader`` 类的实现中，使用 ``FSDataInputStream`` 对象以字节方式从 ``InputSplit`` 类的实例中读取数据。

  虽然这种方式不会感知数据的位置信息,但是通常情况下,它仅从下一个分片中获取很少字节的数据,所以不会有很明显的负载过高的问题。

  但当一条记录很大时,由于节点间要传输大量的数据, 因此会对性能造成很大的影响。

  如下图，这个文件有两个HDFS块，并且记录R5跨越了两个块。假设最小的分片小于块的容量，在这种情况下，RecordReader 需要读取第二个块的数据来收集完整的记录。

  |hadoop_RecordReader_example_01|

Hadoop的“小文件”问题
:::::::::::::::::::::::::
当输入文件明显小于HDFS的块容量时,Hadoop会出现一个众所周知的“小文件”问题。小文件作为输入处理时,Hadoop将为每个文件创建一个Map任务,这将引入很高的任务注册开销。

小文件问题的危害：

- 从 *MapReduce* 角度：小文件作为输入处理时,Hadoop将为每个文件创建一个Map任务,这将引入很高的任务注册开销。这些任务能够在大约几秒钟内完成,然而产生任务和清理任务的时间要比执行任务的时间长得多。
- 从 *HDFS* 角度：每个文件在NameNode中大约要占据150字节的内存,如果大量的小文件存在,将使得这种对象的数量激增,严重影响NameNode的性能和可扩展性。读取大量的小文件也使效率很低,因为有大量的磁盘寻道(seek)操作,并且需要跨越不同的DateNode去读取。

解决方案：
- 在存储文件和执行作业之前,先执行预处理步骤,即把小文件合并成一个更大的文件。 **SequenceFile** (序列文件)和 **TFile** 格式是比较受欢迎的将小文件合并为大文件的方法。
- 使用 **Hadoop Archive File** (HAR),它能减轻 *NameNode* 的内存压力。 **HAR** 是基于HFDS的元文件系统(meta-filesystem)。
- 使用 ``CombineFileInputFormat`` 将多个小文件合并到一个 ``InputSplit`` 中。同时也可以考虑用这个方法来提高处在相同节点或机架的数据的处理性能。

  注意：由于这种方法没有改变 *NameNode* 中的文件数量,所以它不能减轻NameNode的内存需求量的压力。

结构图
^^^^^^^^^^^^^^^^^^^
1. ``InputSplit`` 、 ``InputFormat`` 、 ``RecordReader`` 的类图。

   |mapreduce_class_diagram_for_inputformat_inputsplit_recordreader|

类
^^^^^^^^^^^^^^^^^^^
InputSplit
:::::::::::::::::::
- ``getLength()`` 获取Split的大小，支持根据size对InputSplit排序。
- ``getLocations()`` 获取存储该分片的数据所在的节点位置。

.. |mapreduce_class_diagram_for_inputformat_inputsplit_recordreader| image:: /images/hadoop/hadoop_mapreduce_class_diagram_for_inputformat_inputsplit_recordreader.png
   :width: 100%
.. |hadoop_RecordReader_example_01| image:: /images/hadoop/hadoop_RecordReader_example_01.png
   :width: 100%
