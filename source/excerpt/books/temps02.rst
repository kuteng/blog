读书笔记 2018-09-30
=====================================
.. toctree::
   :maxdepth: 1

时间：9月30日

书籍：《精通Hadoop》

进度：61页 - 76页

--------

3.7 用户自定义函数

- 有一个Java **UDF** 存储库称为 `piggy bank` 。
- 每个Map和Reduce任务都有自己的UDF副本。跨Map和Reduce任务是无法共享状态的，不过，在同一个Map或Reduce任务中是可以共享的
- 如果需要递归地读取HDFS文件夹中的文件，则可以使用 ``PigFileInputFormat`` 和 ``PigTextInputFormat`` 。 你可以在 ``org.apache.pig.backend.hadoop.executionengine.mapReduceLayer`` 包里找到这些Pig 所特有的InputFormat类。Hadoop自带的TextInputFormat和FileInputFormat只能读取一层目录的文件。

用户定义函数
  **UDF** : User-Defined Functions

  由开发者自行实现，用以扩展Pig的功能，添加自定义处理。

  在Pig中使用UDF之前，需要在Pig的脚本中先注册这个JAR文件。使用 **REGISTER** 命令可以进行注册。

  三种类型： **运算函数** 、 **加载函数** 、 **存储函数** 。

运算函数
  这些函数都是用于计算的。如： `UPPER` ，后者是将字符转换为大写。

  所有的运算函数都从 ``org.apache.pig.EvalFunc`` 基类派生而来，重写方法 ``exec()`` 。

  EvalFunc类的返回值是一个泛型，我们需要明确UDF的返回类型。 exec方法的输入是一个Tuple类型。

  具体实现方法请查看 `61页` 。在脚本中的用法如下： ::

    register MasteringHadoop-1.0-SNAPSHOT-jar-with-dependencies.jar;
    cc = load 'countrycodes.txt' using PigStorage(',') as (ccode:chararray, cname:chararray);
    ccCapitalized = foreach cc generate MasteringHadoop.UPPER(cc.cname);

聚合函数
  是针对组（group）的运算函数。如 `SUM` 和 `COUNT` 之类。

  聚合UDF接受一个bag的输入，返回一个标量（scalar）。

  整条记录可以用*传递给UDF。当整条记录被传递后，它会被包裹进别的tuple。比如，执行 ``input.get(0).get(1)`` 就可以得到一条记录的第二个元素。第一个get()调用可以从tuple中得到整条记录。

  聚合函数有两中接口，分别是： **Algebraic接口** 、 **Accumulator接口** 。

Algebraic接口
  如果一个聚合函数实现了可用于本地聚合处理的Algebraic接口，那么Combiner就会被使用。可以认为 **Algebraic接口** 是为 **Combiner** 准备的。

  须实现的接口是 ``org.apache.pig.Algebraic`` 。 实现这个接口可以使UDF具有代数特性。重点方法： ``getInitial()`` 、 ``getIntermed()`` 、 ``getFinal()`` 、 ``exec(Tuple objects)`` ，同事需要为前三个方法准备 ``Initial`` 、 ``Intermed``、 ``Final`` 三个类，他们都需要实现 ``EvalFunc<...>`` 。具体实现情况 `63页` 。

  使用Combiner的Map任务将执行 **Initial** 和 **Intermediate** 静态类的exec方法， Reduce任务将执行 **Final** 类的exec方法。具体实现情况 `63页` 。

  疑问： ``Algebraic`` 的 ``exec()`` 方法何时执行？

Accumulator接口
  在很多情况下，当使用GROUP或COGROUP操作符时， tuple中的所有bag不能按某个特定键全部加载到内存里。而且， UDF也并不需要一次性访问所有的tuple。 Pig允许UDF通过实现Accumulator 接口去处理这些情况。 Pig并不是一次性传递全部记录，而是通过这个接口，针对某个给定的键，增量地传递记录的子集。

  需要实现类 ``org.apache.pig.Accumulator`` 。重点方法： ``accumulate(Tuple objects)`` 、 ``getValue()`` 和 ``cleanup()`` 。

  当一个中间记录集被传递给UDF时会调用 ``accumulate`` 方法，当每个键被处理后会调用 ``cleanup`` 方法。详见 `65页`` 。

过滤函数
  过滤函数（filter function）也是运算函数，只不过它返回的是布尔值（Boolean）。只要是布尔表达式运算的地方就可以使用它们。它们最常被用作FILTER操作符的一部分。它们实现了 ``FilterFunc`` 接口。

加载函数
  Pig脚本中的加载函数是用来处理输入数据的。
  
  须实现抽象类 ``org.apache.pig.LoadFunc`` 。 重点方法： ``setLocation(String s, Job job)`` 、 ``getInputFormat()`` 、 ``prepareToRead(RecordReader recordReader, PigSplit pigSplit)`` 和 ``getNext()`` 。详见 `66页`

  ``setLocation`` 告知加载的路径，随后加载器（loader）将这个信息通知给 ``InputFormat`` 。 ``setLocation`` 方法可以被Pig多次调用。

  ``prepareToRead`` 得到 ``InputFormat`` 类的 ``RecordReader`` 对象。

  ``getNext`` 方法中，可以用 ``RecordReader`` 来读取并解析记录。 ``getNext`` 方法将记录解析成Pig的复合数据类型。

  ``getInputFormat`` 方法通过加载器将 ``InputFormat`` 类交给Pig。 Pig同样以Hadoop MapReduce作业的方式调用 ``InputFormat`` 。

存储函数
  Pig脚本中的存储函数是用来处理数据（结果）持久化的。

  须实现抽象类 ``org.apache.pig.StoreFunc`` 。内部方法的含义与 **加载函数** 类似。

--------

3.8 Pig的性能优化

- 我认为：Pig中 **Filter** 、 **Group** 语句会在 ``Map`` 中执行。
- 紧随 **join** 的 **Foreach** 会放在 **Reducer** 中执行。
- 注意 **Combiner** 只用于聚合，所以不好将 **Filter** 、 **Group**  与 **Combiner** 混为一谈。
- ``order`` 、 ``join`` 、 ``group`` 、 ``distinct`` 、 ``limit`` 、 ``cogroup`` 、 ``cross`` 会强制执行一个reduce阶段。
- 优化规则适用于为Pig脚本而生成的 **逻辑计划** 。默认情况下，所有规则都是打开的。
- 默认情况下， FilterLogicExpressionSimplifier是关闭的。可以通过将属性pig.exec.filterLogicExpressionSimplifier的值设为true来打开它。
- 存储结构的语句： ``store joinCountry into 'country-code-join-pig' using PigStorage(',');``
- 当DISTINCT是嵌套中唯一一个操作符时， Combiner也可以被用在嵌套FOREACH语句中。
- **MapReduce** 中， **Map** 的个数由“输入文件数目”、“输入文件的大小”、“配置参数”三个因素决定；而 **Reduce** 的个数默认为“1”，使用过多的Reduce任务则意味着复杂的shuffle，并使输出文件的数量激增。
- 应避免使用DUMP，因为它禁用multiquery执行，而这会导致重新评估关系，使得Pig脚本变得低效。相反，使用STORE不失为一个好办法。交互式命令DUMP会强制Pig编译器避免multiquery执行

优化规则
  优化规则适用于为Pig脚本而生成的 **逻辑计划** 。默认情况下，所有规则都是打开的。关闭方式将配置 ``pig.optimizer.rules.disabled`` 。

  这些优化规则包括： **PartitionFilterOptimizer** 、 **FilterLogicExpressionSimplifier** 、 **SplitFilter** 、 **PushUpFilter** 、 **MergeFilter** 、 **PushDownForEachFlatten** 、 **LimitOptimizer** 、 **ColumnMapKeyPrune** 、 **AddForEach** 、 **MergeForEach** 、 **GroupByConstParallelSetter** 。

PartitionFilterOptimizer
  这个规则将所有上游的过滤都下推到加载器。很多加载器都是分区敏感的，并且会被指示用过滤条件加载一个分区。

  疑问： 什么样的过滤是“分区敏感”的？

FilterLogicExpressionSimplifier
  打开这个规则可以简化过滤语句表达式。如这些简化处理（目前已经完成的）：常量预计算、去除否定、去除AND中的隐含表达式、去除OR中的隐含表达式、去除等价、去除OR互补表达中的过滤、去除“ 总是为真” 的表达式。

SplitFilter
  这个优化规则尝试分割过滤语句。这个SplitFilter优化与其他的过滤优化（比如 **PushUpFilter** ）组合使用时，对于提升性能将会非常有效。

PushUpFilter
  这种优化背后的思想是将数据管道中的过滤语句推往上游。这样做的好处是减少了将要被处理的记录条数。比如它与 **SplitFilter** 相配合使用。

MergeFilter
  是 **SplitFilter** 的补充 。 **SplitFilter** 应用在 **PushUpFilter** 之前，而 **MergeFilter** 是应用在 **PushUpFilter** 之后。多个相同数据集的过滤被合并成一个单一的过滤。

PushDownForEachFlatten
  FOREACH语句中的FLATTEN操作通常会产生比输入更多的tuple。 **PushDownForEachFlatten** 优化将这些FOREACH语句推往下游。

  疑问：在 ``71页`` 针对这个优化的例子，我有一个疑问。

LimitOptimizer
  和 **PushUpFilter** 类似，这里的思想是将LIMIT操作符语句往上游推动。这样可以减少下游需要处理的记录条数。

ColumnMapKeyPrune
  这种优化背后的思想是让加载器只加载需要的数据列。如果加载器无法做到这一点，那么就在加载调用之后插入一条FOREACH语句。这个优化可以很好地作用在map键上。

AddForEach
  用于将脚本不再需要的列尽快裁剪掉。在下面的例子中，Column1在ORDER语句之后不再被使用。

MergeForEach
  这个优化将多个FOREACH语句合并成一个FOREACH语句。这样可以不必多次遍历数据集。这个优化只有当下面的三个条件都满足时才生效。

  - FOREACH中不包含FLATTEN操作符
  - FOREACH语句是连续的。
  - FOREACH语句中没有嵌套。序列中第一个FOREACH语句除外。

GroupByConstParallelSetter
  在一个执行GROUP ALL的语句中，即使将PARALLEL设置成Reduce任务的数量，仍然只会使用一个Reduce任务。其余的Reduce任务会返回空的结果。这个优化自动将Reduce任务的数量设置为1。

  疑问：为什么会有这个优化，搞不懂。

Pig脚本性能的测量
  UDF是开发者所写的函数，这些函数可能需要性能分析来识别热点。 Pig提供了一些使用了Hadoop计数器的UDF统计功能。可以把 ``pig.udf.profile`` 设为true。一旦这个设置有效以后，Pig会跟踪执行某个特定UDF所花的时间，以及UDF的调用频率。 ``approx._microsecs`` 测量UDF中大致花费的时间， ``approx._invocations`` 则测量UDF在执行过程中被调用的次数。

  ``pig.udf.profile = true`` 最好只在测试时设置。因为这样会在Hadoop作业执行过程中启用计数器，而计数器是全局的，并且计数器在跟踪Hadoop作业时会增加额外开销。

Pig的Combiner
  Hadoop中 **Combiner** 可以减少磁盘I/O，同时减少通过网络从Map任务发送到Reduce任务的数据量。在Pig中，基于脚本的结构， Combiner也可能会被调用。下面是一些调用Combiner的条件。

  - 使用无嵌套的FOREACH语句。
  - 一条FOREACH语句中的所有投影都是分组表达式，或者说所使用的UDF都是代数函数，也就是说它们实现了Algebraic接口。

  在以下条件下， Combiner不会被使用。

  - 脚本在执行前面提到的规则时失败。
  - 在GROUP和FOREACH之间存在任何语句； Pig 0.9以后， LIMIT操作符除外。

  当DISTINCT是嵌套中唯一一个操作符时， Combiner也可以被用在嵌套FOREACH语句中。

  注意：逻辑优化器可能会使用PushUpFilter优化将任何紧随FOREACH的FILTER操作符推往上游。这可能会阻碍Combiner的使用。

Bag数据类型的内存
  Bag是唯一一种当数据不能全部加载到内存时，会被保存到磁盘的复合数据类型。 ``pig.cachedbag.memusage`` 参数决定了分配给bag的内存百分比。默认值是0.2，也就是说应用中的所有bag可以共享20%的的内存。

Pig的reducer数量
  不同于原始的MapReduce， Pig会根据输入数据的大小来决定Reduce任务的数量。输入的数据根据 ``pig.exec.reducers.bytes.per.reducer`` 参数的值来进行切分， 从而得到Reduce任务的数量。Reduce任务的最大数量由 ``pig.exec. reducers.max`` 参数的值决定。

  此外还可以将实现计算Reduce数量的类进行覆盖，相关的配置是 ``pig.exec.reducer.estimator`` 、 ``pig.exec.reducer.estimator.arg`` ，相关的接口是 ``org.apache.pig.backend.hadoop.executionengine.mapReduceLayer.PigReducerEstimator``

Pig的multiquery模式
  默认的情况下， Pig以multiquery模式执行。一个Pig脚本中所有的语句将作为一个Pig作业执行。相关语句 ::

    #使用-M或–no_multiquery参数来关闭multi-query模式的执行.
    pig –M <script> or pig –no_multiquery <script>

  应避免使用DUMP，因为它禁用multiquery执行，而这会导致重新评估关系，使得Pig脚本变得低效。相反，使用STORE不失为一个好办法。交互式命令DUMP会强制Pig编译器避免multiquery执行

  疑问：**multiquery模式** 是什么？

--------

3.9 最佳实践

- 类型的正确使用可以加快你的脚本，有时甚至能达到2倍。比如，在Pig中，所有没有类型声明的数值计算都默认为double计算。Pig中的double类型占用8个字节的存储，而int类型只占4个字节。 int的计算速度比double类型的更快。
- 在Pig中，对于“找出某个字段中有多少不同的元素”，使用 ``Distinct`` 要比使用 ``Group`` 要更高效，原因是： ``Distinct`` 会在map阶段执行了 **Combiner** ，以清理多余的记录，提高了效率。
- 使用 ``FLATTEN`` 将分组解开，那么所有的空记录也将被剔除。但是，这个剔除是发生在Reduce任务执行以后。在 **JOIN** 或 **GROUP** / **COGROUP** 操作符之前主动过滤掉空记录，去除那些需要处理空键的Reduce任务，可以显著地提高脚本的性能。
- 普通连接的第二个输入被作为流传输，而不是被加载到内存中。这在Pig中是一种常规的连接优化。当连接不同大小的数据集时，更高效的做法是将数据量大的数据集作为连接的最后一个输入。比如这样： ``C = JOIN small_file BY s, large_file by F;``
- Pig中用于压缩的编码器有两种常见的，分别为 **gz** 和 **lzo** 。需要注意：虽然GZIP压缩编码提供了更好的压缩，但它并不是首选，因为它的执行时间相对比较慢。
- 自带的 ``PigStorage`` 加载器对于合并小文件很有效。如果你要写一个自定义的加载器，它必须是无状态地调用 ``prepareToRead`` 方法。此外，这个加载器不能实现 ``IndexableLoadFunc`` 、 ``OrderedLoadFunc`` 和 ``CollectableLoadFunc`` 接口。

这里列举了一些最佳实践：

- 明确地使用类型
- 更早更频繁地使用投影
- 更早更频繁地使用过滤
- 使用LIMIT操作符
- 使用DISTINCT操作符：“找出某个字段中有多少不同的元素”时
- 减少操作：类似 **MergeForEach** 和 **MergeFilter** 这样的优化规则。
- 使用Algebraic UDF：当“处理过程是代数性质”时，因为它将调用Combiner。
- 使用Accumulator UDF：通过将输入数据分块， Accumulator UDF可以减少UDF所需的内存数。
- 剔除数据中的空记录
- 使用特殊连接
- 压缩中间结果
- 合并小文件

投影
  是指像 ``foreach joinCountry generate cc::cname, ccity::cityName, ccity::population;`` 这样的语句。

数据压缩
  Pig脚本可能被编译成多个MapReduce作业。每个作业都可能产生中间输出。可以用LZO压缩编码来压缩这些中间输出。这不仅有助于节省HDFS的存储，还可以帮助减少加载时间从而更快地执行作业。

  ``pig.tmpfilecompression`` 属性决定了是否压缩中间文件。默认情况下，该值为false。

  ``pig.tmpfilecompression.codec`` 属性的值表示用于压缩的编码器。目前可用值为gz和lzo。

--------

命令摘录

REGISTER
  注册jar，用法后续跟进。

  脚本中的用法： ``register MasteringHadoop-1.0-SNAPSHOT-jar-with-dependencies.jar;``

--------

配置摘录

pig.optimizer.rules.disabled
  用于关闭某些 **优化规则** ，如： ``set pig.optimizer.rules.disabled <comma-separated rules list>`` 。同样的效果也可以通过命令行参数： ``–optimizer_off`` 实现，如： ``pig –t|–optimizer_off [rule name | all]`` 。

pig.udf.profile
  为 ``True`` 时，Hadoop计数器的UDF统计功能。

  不过， ``pig.udf.profile = true`` 最好只在测试时设置。因为这样会在Hadoop作业执行过程中启用计数器，而计数器是全局的，并且计数器在跟踪Hadoop作业时会增加额外开销。

pig.cachedbag.memusage
  决定了分配给bag的内存百分比，默认值是0.2。

pig.exec.reducers.bytes.per.reducer
  与 **Pig的reducer数量** 相关，输入的数据根据这个参数的值来进行切分，默认值是1000000000（1 GB）。

pig.exec. reducers.max
  与 **Pig的reducer数量** 相关，Reduce任务的最大数量由这个参数的值决定，默认值是999。

pig.exec.reducer.estimator
  与 **Pig的reducer数量** 相关，实现计算 Reduce数量算法的类由这个参数决定。

  与之相关的接口是 ``org.apache.pig.backend.hadoop.executionengine.mapReduceLayer.PigReducerEstimator`` 。

  与之相关的接口是 ``pig.exec.reducer.estimator.arg`` 。

pig.exec.reducer.estimator.arg
  与 **Pig的reducer数量** 相关，与接口 ``pig.exec.reducer.estimator`` 配合使用。

pig.tmpfilecompression
  与Pig的 **数据压缩** 有关。决定了是否压缩中间文件。默认情况下，该值为false。

pig.tmpfilecompression.codec
  与Pig的 **数据压缩** 有关。决定了用于压缩的编码器。目前可用值为gz和lzo。

pig.splitCombination
  与Pig的 **合并小文件** 有关。决定了是否启用 **合并小文件** 。

pig.maxCombinedSplitSize
  与Pig的 **合并小文件** 有关。决定了每个分片的大小。

--------

接口（类）摘录

org.apache.pig.EvalFunc
  类，为了 **运算函数** 而设立，关键方法是： ``exec`` 。

  EvalFunc类的返回值是一个泛型，我们需要明确UDF的返回类型。 exec方法的输入是一个Tuple类型。

org.apache.pig.Algebraic
  接口，为了 **聚合函数** 而设立。

  重点方法： ``getInitial()`` 、 ``getIntermed()`` 、 ``getFinal()`` 、 ``exec(Tuple objects)`` ，同事需要为前三个方法准备 ``Initial`` 、 ``Intermed``、 ``Final`` 三个类，他们都需要实现 ``EvalFunc<...>`` 。具体实现情况 `63页` 。

org.apache.pig.Accumulator
  接口，为了 **聚合函数** 而设立。

  重点方法： ``accumulate(Tuple objects)`` 、 ``getValue()`` 和 ``cleanup()`` 。

org.apache.pig.FilterFunc
  接口，为了 **过滤函数** 而设立。

org.apache.pig.LoadFunc
  接口，为了 **加载函数** 而设立。
  重点方法： ``setLocation(String s, Job job)`` 、 ``getInputFormat()`` 、 ``prepareToRead(RecordReader recordReader, PigSplit pigSplit)`` 和 ``getNext()`` 。

org.apache.pig.StoreFunc
  重点方法： ``putNext`` 、 ``getOutputFormat`` 、 ``setStoreLocation`` 和 ``prepareToWrite`` 。

org.apache.pig.backend.hadoop.executionengine.mapReduceLayer.PigReducerEstimator
  与 **Pig的reducer数量** 相关，实现计算 Reduce数量算法的类需要实现这个接口。与它相关的配置是 ``pig.exec.reducer.estimator`` 、 ``pig.exec.reducer.estimator.arg`` 。

--------

名词摘录

代数函数
  任何代数（algebraic）函数都可以分解成三个函数： **初始函数** （initial function）， **中间函数** （intermediate function）， **最终函数** （final function）。如果这三个函数以级联方式连接，它就被标记为一个 **代数函数** 。 

  COUNT函数就是一个代数函数的例子。详细解释情况 `62页` 。

  注意，这个定义的使用范围是Pig中的UDF。

  **分布（distributive）函数** 是一种特殊的代数函数。所有的三个子函数都做同样的计算。 **SUM** 就是分布函数的一个例子。

--------

疑问：

- Pig的 `嵌入式模式` 如何启动运行？
- Pig的 `multiquery模式` 是什么鬼？网上的资料都很少！
- 什么叫 **代数性质** ，太抽象了！
- 对于优化规则 **PartitionFilterOptimizer** 我不是很明白。有没有例子呢？什么样的过滤是分区敏感的？
- 对于优化规则 **GroupByConstParallelSetter** ，我同样搞不懂。
- 在 `70页` 有一个关于 ``Filter`` 执行位置的疑问。
- **ColumnMapKeyPrune** 的介绍中，“如果加载器无法做到这一点，那么就在加载调用之后插入一条FOREACH语句。这个优化可以很好地作用在map键上”这句话是什么意思？是让我们手动敲Foreach，还是说 **ColumnMapKeyPrune** 优化本身就是应对“加载器无法做到这一点”的状况的？
- **FOREACH** 是在 **Map** 侧执行的还是 **Reducer** 侧？如果有两个以上紧挨着的 **FOREACH** 操作，而且他们都无法被 **MergeForEach** 优化，那么它们会没有一个都对应一个 **MapReduce** 吗？ 
- 
