读书笔记 2018-10-03
=====================================
.. toctree::
   :maxdepth: 1

时间：10月01日

书籍：《精通Hadoop》

进度：87页 - 94页

----------

- **分桶** 并不意味着排序，这一点需要注意。
- **UNION** 并非是 **连接** ( **JOIN** ) 两个数据集，而是 **合并** 两个数据集。
- **Hive** 中， **Sequence文件** 与 **ORC** 应该是不冲突的吧！
- **Hive** 连接中，MapReduce作业数取决于连接列的数量。如果多表的连接列都一样，那么只会生成单个MapReduce作业。
- 使用单个reducer的时候要避免使用ORDER语句。第2章中所提的优化方法，都可以应用到基于Hadoop集群的Hive查询，这会对查询性能产生积极的影响。

----------

Hive的优化器
  Hive的集成优化器的思想与 **MapReduce** 、 **Pig** 类似，从减少字段、减少数据传输量、减少数据运算量、聚合尽量通过Map、过滤投影前移、减少操作（合并投影、过滤）、针对连接优化、利用 **桶** 和 **索引** 进行聚合等角度进行优化。
  Hive中集成了如下十三种优化器。 **ColumnPruner** 、 **GlobalLimitOptimizer** 、 **GroupByOptimizer** 、 **JoinReorder** 、 **PredicatePushdown** 、 **PredicateTransitivePropagate** 、 **BucketingSortingReduceSinkOptimizer** 、 **LimitPushdownOptimizer** 、 **NonBlockingOpDeDupProc**  、 **PartitionPruner** 、 **ReduceSinkDeDuplication** 、 **RewriteGBUsingIndex** 、 **StatsOptimizer** 。下面几个是需要详细说明的优化器。

GroupByOptimizer
  如果GROUP BY的键是分桶排序键的超集，那么会在Map端进行聚集。相应地，优化器会小心地修改执行计划。另外，两者键的排列顺序必须一致。

JoinReorder
  基于用户提示，流化后的表会在连接操作的最后阶段被处理。

PredicateTransitivePropagate 
  此优化规则把谓词传递给连接操作中的另一张连接表。当两表互连，其中一张表通过谓词对连接键进行数据过滤，那么这表的过滤谓词也可以应用到另一张表。

BucketingSortingReduceSinkOptimizer
  如果源表和目标都基于相同顺序的键分桶排序，那么不需要Reduce任务便可对这些表进行连接或插入操作。如： ``INSERT OVERWRITE A SELECT * FROM B;`` ，如果表A和表B的键都是一样分桶排序过，那么只需Map任务就可处理。

  这样可以避免洗牌与排序的操作。

LimitPushdownOptimizer
  如果带有LIMIT操作符的语句没有过滤条件，那么Map任务就可优化为只查询前K条记录。这K条记录接着传给LIMIT操作符。这极大地减少了洗牌/排序阶段需要处理的记录数。

  疑问：对此我抱有极大的疑问，如果语句中有排序的话，这个优化不是出问题了吗？

PartitionPruner
  为避免云存储发生内存溢出，会首先获取分区名，然后按需获取分区详情信息。

ReduceSinkDeDuplication
  如果两个Reduce任务拥有相同的分区列且顺序也相同，那么它们会被合并成单个任务。（这里说的应该是在执行之前、生成DAG的时候）

RewriteGBUsingIndex
  如果列有索引，那么GROUP BY可以不扫描基表而只扫描索引表来实现对该列的聚合操作。

  如果“有桶”的话， **桶** 和 **索引** 哪个更高效？

StatsOptimizer
  有许多查询结果可以直接从元存储的统计数据中获得，比如MIN、MAX、COUNT之类的语句，而不用产生任何MapReduce任务。这个优化器会识别和优化此类查询语句。

DML进阶
  Hive的数据操作语言功能与其他顶级SQL系统一样，提供标准操作。不过下面几点需要特殊注意。

Group By
  **Multi-Group-By Inserts** ：单个查询可以包含多个GROUP BY语句，其结果可以写入多张表或多个HDFS文件。例如： ::

    FROM src_table 
    INSERT OVERWRITE TABLE id_count SELECT id,COUNT(id) GROUP BY id 
    INSERT OVERWRITE TABLE id_sum SELECT id,SUM(id_value) GROUP BY id;
    
  **Map侧进行GROUP BY聚合** ：配置 ``hive.map.aggr`` 为true，可以让Map任务先进行聚合，从而提高查询性能。

ORDER BY与SORT BY
   ``ORDER BY`` 保证查询结果整体有序，
   
   ``SORT BY`` 保证单个Reduce任务中的数据有序，所以如有多个Reduce任务，那么 ``SORT BY`` 只能保证部分有序。

   当使用 ``ORDER BY`` 语句的时候， Hive默认必须要有LIMIT操作符，对应的配置项是 ``hive.mapred.mode`` ，其默认值是strict。

JOIN类型
  Hive支持内连接、外连接、左半连接。所有连接类型都基于等值连接，不支持模糊连接和θ连接。

  支持多表连接。 MapReduce作业数取决于连接列的数量。如果多表的连接列都一样，那么只会生成单个MapReduce作业。

  连接中的最后一张表会流化到Reduce任务，而其余表被缓存起来。用户可以使用 ``STREAMTABLE`` 提示来覆盖默认行为，例如： ``SELECT /*+ STREAMTABLE(A)*/ A.x, B.x, C.x FROM A JOIN B ON (A.key = B.key) JOIN C ON (B.key = C.key);`` 。

  JOIN语句中的WHERE子句会在JOIN操作完成之后过滤数据行。把WHERE过滤条件都写在对应JOIN的ON子句（JOIN的连接列位于此处）中不失为一种好做法。（这一点与 ``Pig`` 似乎不一样。

Hive中“Map侧的连接”
  如果连接中有一张表很小，那么可以在Map任务中直接进行连接操作， Hive中可以使用 ``MAPJOIN`` 提示来实现此功能。例如 ``SELECT /*+ MAPJOIN(A) */ A.x, B.x FROM A JOIN B ON (A.key = B.key)`` 。

  Map侧可以进行 **桶化Map侧连接** 。

  Map侧可以进行 **桶化归并排序连接** 。

桶化Map侧连接
  如果连接表都 `对连接列分桶` ，并且其中一张表的桶数与另一张表的桶数 `相同或是其倍数` ，那么就可以在Map侧进行连接操作。配置 ``hive.optimize.bucketmapjoin`` 为true（默认false）可以开启此功能。此功能又称为桶化Map侧连接（bucketized map-side join）。

  此功能默认关闭

桶化归并排序连接
  如果连接表 `都对连接列分桶` ， `桶数相同` ，并且桶中数据 `已经依照连接列排好序` ，那么两表相连可以使用归并排序（sort-merge）。

  此功能默认关闭。

  相关配置 ``hive.optimize.bucketmapjoin`` 、 ``hive.optimize.bucketmapjoin.sortedmerg`` 、 ``hive.input.format``

高级聚合
  Hive主要用于数据仓库分析，这就需要高级聚合功能来多维度地分析和统计数据。

  **GROUPING SETS** 、 **数据立方体**

  具体详情请看 `90页` 。

用户自定义函数
  Hive中，用户也可以自定义函数。分为 **UDF** 、 **UDAF** 、 **UDTF** 。

UDF
  读入一行，应用自定义逻辑后，产出一行结果。须继承类 ``org.apache.hadoop.hive.ql.exec.UDF`` ，并实现方法 ``evaluate`` 。

UDAF
  读入多行，进行聚合计算后，产出一行结果。例如内带的UDAF， `SUM` 和 `COUNT` 。
  
  须继承类 ``org.apache.hadoop.hive.ql.exec.UDAF`` ，并在此类内构建类 ``UDAFEvaluator`` 。详情查看 `92页`

UDTF
  这是一种生成函数，即读入一行后，输出多行结果。 `EXPLODE` 就是UDTF。

----------

“市井”摘录

Group By
  使用了reduce操作，受限于reduce数量，通过参数mapred.reduce.tasks设置reduce个数。

  输出文件个数与reduce数量相同，文件大小与reduce处理的数量有关。

----------

DML摘录

STREAMTABLE
  只能特定表做“流化”。如： ``SELECT /*+ STREAMTABLE(A)*/ A.x, B.x, C.x FROM A JOIN B ON (A.key = B.key) JOIN C ON (B.key = C.key);`` 。

MAPJOIN
  告诉编译器，指定表较小所以本次连接(Join)可以Map任务中进行。如： ``SELECT /*+ MAPJOIN(A) */ A.x, B.x FROM A JOIN B ON (A.key = B.key);`` 。

GROUPING SETS
  ``SELECT x, y, SUM(z) FROM X GROUP BY x, y GROUPING SETS( (x,y), y);``

数据立方体
  ``SELECT x, y, z, SUM(a) FROM X GROUP BY x, y, z WITH CUBE;`` 它相当于 ``SELECT x,y,z, SUM(a) FROM X GROUP BY x,y,x GROUPING SETS ((x,y,z), (x,y), (y,z), (x,z), (x),(y), (z), ());``

----------

“市井”摘录

接口与类

org.apache.hadoop.hive.ql.optimizer.Transform
  接口，通过此接口，我们可以实现对Hive的自定义优化器。

----------

配置摘录

hive.map.aggr
  与 **Map侧进行GROUP BY聚合** 有关。此配置为true时，可以让Map任务先进行聚合，

hive.mapred.mode
  默认值是 ``strict`` 。表示：当使用 ``ORDER BY`` 语句的时候， 必须要有 `LIMIT` 操作符。
  如果设置为 ``nonstrict`` ，编译器便不会强制要求有 `LIMIT` 操作符了，但不推荐这么设置。

hive.optimize.bucketmapjoin
  如果设为 ``True`` ，开启 **桶化Map侧连接** 。

hive.optimize.bucketmapjoin
  与 **桶化归并排序连接** 相关。设为 ``True`` 才能开启此功能。

hive.optimize.bucketmapjoin.sortedmerg
  与 **桶化归并排序连接** 相关。设为 ``True`` 才能开启此功能。

hive.input.format
  与 **桶化归并排序连接** 相关。设为 ``org.apache.hadoop.hive.ql.io.BucketizedHiveInputFormat`` 才能开启此功能。

hive.auto.convert.join
  与Hive的 **Map侧连接** 相关。

  设为 ``True`` ，可以自动把连接转换为 **Map侧连接** ，不再需要 ``MAPJOIN`` 提示。然而，如果要使用桶化Map侧连接和桶化归并排序连接，这个提示是必需的。

hive.new.job.grouping.set.cardinality
  在 **数据立方体** 中有个隐式假设，层级x能下钻到层级y，层级y可以反钻到层级z，这会导致ROLLUP为每行输入产生多行输出。在上例中，对于每个输入行，会产生四行带有三个聚合键的输出。聚合键的基数越大， Map和Reduce任务的处理边界越难界定。这种情况下，最好生成多个MapReduce作业。基数阈值可以通过本配置设置， 如果超过此值，会启动额外的作业。

----------

疑问

- **Map侧进行GROUP BY聚合** 中如果配置了 ``set hive.map.aggr=true`` ，某些语句是否会造成性能的下降？如果在脚本中只对个别语句使用此设置，该如何是好？
- Hive中，多个Reduce是，能够保证 ``ORDER BY`` 的有效性吗？
