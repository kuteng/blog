读书笔记 2018-10-01
=====================================
.. toctree::
   :maxdepth: 1

时间：10月03日

书籍：《精通Hadoop》

进度：77页 - 86页

----------

- Map步骤是最高并行度的。它一般用来实现类似于过滤、排序和转换数据这样的操作。Reduce操作一般用于实现数据的汇总操作。 
- Hadoop也提供了 **分布式缓存** （DistributedCache）等特性作为分布数据的一个 **旁路通道** ，以及 **计数器** （counter）来收集作业相关的全局状态。
- **旁路通道** 是作用在 **Reduce** 端的，与 **Map** 无关。
- **压缩技术** ， **bizp2** 支持 **可分割** ， **LZO** 建索引之后支持可分割， **gzip** 压缩效果更高但耗资源也多。MapReduce时：输入选择可分割的压缩方式：Bzip2 ；中间数据的压缩选择压缩速度快的压缩方式：Snappy、LZO ；Reduce输出的压缩选择压缩比高的。
- **Sequence文件** ：这种文件是可切割的，可以被多个Map任务处理，所以在Hive中，表的底层数据使用 **Sequence文件** 储存。
- Hive中，默认的主要文件结构：warehouse目录 -> 数据库名称目录 -> 表名目录 -> 分区目录 -> hash后的文件名。
- 分区和分桶最大的区别就是分桶随机分割数据库，分区是非随机分割数据库。因为分桶是按照列的哈希函数进行分割的，相对比较平均；而分区是按照列的值来进行分割的，容易造成数据倾斜。其次两者的另一个区别就是分桶是对应不同的文件（细粒度），分区是对应不同的文件夹（粗粒度）。
- 在数据量足够大的情况下，分桶比分区，更高的查询效率。
- 注意：普通表（外部表、内部表）、分区表这三个都是对应HDFS上的目录，桶表对应是目录里的文件
- 索引和分区最大的区别就是索引不分割数据库，分区分割数据库。索引其实就是拿额外的存储空间换查询时间，但分区已经将整个表按照分区列拆分成多个小表了。

----------

4.1 Hive 架构

Hive元存储
  Hive的 **元存储** 是个数据库，用于保存系统相关的元数据，有关 **表** 、 **分区** 、 **表结构** 、 **字段类型** 以及 **表存放路径** 的详细信息都存放在这里。使用 **Thrift接口** ，许多不同编程语言写成的客户端都可以读取这些数据。这些数据保存在 **关系数据库** （RDBMS）中，并通过 **对象关系映射** （ORM）层进行读写。

  **元存储** 使用 **RDBMS** ，可以降低Hive查询编译器获取元数据信息的延迟。

  **元数据** 代表了原生HDFS文件的数据结构，所以定期备份或复制元数据，防止元存储崩溃，至关重要。

  只有在编译的时候才会访问 **元存储服务** ， MapReduce作业运行时绝不会访问它。

  疑问：上面提到的“编译期”是 `建表` 语句的编译期还是 `查询` 语句的编译期，亦或是 `数据导入` 的编译期？

Hive编译器
  编译器获取HiveQL查询语句，然后转换为MapReduce作业。解析器解析查询语句生成 **抽象语法树** （AST）。 AST根据从元存储获取的元数据来 `检查类型与语义一致性` ，检查完毕后生成可操作的 **DAG** 。之后DAG会经过一系列的优化变换。优化变换的过程是链式的，最终会生成优化后的 **操作树** 。优化后的DAG接着转换为物理计划。物理计划由一系列的MapReduce和HDFS作业组成。HDFS作业用来读写HDFS上的数据。

Hive执行引擎
  一旦执行引擎从编译器获取到物理计划，就会严格地按照依赖关系执行作业。物理计划以 ``plan.xml`` 文件为载体，分发（通过旁路通道）给Hadoop集群中的每个任务。作业执行结果会存放在临时路径。如果指定了存储路径，那么一旦整个查询全部执行完毕， Hive就会使用数据操作语言（DML）把这些文件移到指定路径。如果某个查询没有指定存储路径，那么直接访问临时路径就可以获得查询结果。

  注意：执行结果是以文件的形式存放到HDFS上的。那么临时路径的“执行结果”需要手动删除吗？（想来Hive会自动清理吧）

数据类型
  原生数值类型： ``TINYINT`` 、 ``SMALLINT`` 、 ``INT`` 、 ``BIGINT`` 、 ``FLOAT`` 、 ``DOUBLE`` 和 ``DECIMAL``

  原生字符类型： ``CHAR`` 、 ``VARCHAR`` 和 ``STRING``

  时间类型： ``TIMESTAMP`` 和 ``DATE``

  辅助类型： ``BOOLEAN`` 和 ``BINARY``

  符合类型： ``STRUCT`` 、 ``MAP`` 、 ``ARRAY`` 、 ``UNIONS`` 。

  ``MAP`` ： 它的键必须是“原生数据类型”。

  ``UNIONS`` : Hive支持联合类型，元素类型可以是联合中定义的数据类型中的一个。 语法： ``UNIONTYPE<data_type1, data_type2…>`` 

  Hive中的函数和数据类型是不区分大小写的。

----------

4.3 文件格式

压缩文件
  Hive支持把 `GZIP` 或 `BZIP2` 格式的文件直接 **导入** 表。在查询执行期间，解压后的文件会作为Map任务的输入。但是，压缩格式使 `GZIP` 的文件不可切分，所以只能由单个Map任务处理。

Sequence文件
  Hive表的底层数据格式是Sequence文件。 **Sequence文件** 是可切分的，所以可以被多个Map任务处理。Hive根据 ``io.seqfile.compression.type`` 配置项的值来确定Sequence文件的压缩方式。

Lempel-Ziv-Oberhumer（LZO）
  这是一个以解压缩速度见长的无损压缩编解码器。 **LZO** 编解码器需要安装在集群中的每个节点上，这样Hadoop集群才能使用它。

  **Map** 到 **Reduce** 之间的通信，推荐使用它。此外 **Reduce** 的输出是下一个 **MapReduce** 任务的输入时， **Reduce** 的输出也推荐使用它。

  相关的配置有： ``mapreduce.output.fileoutputformat.compress.codec`` 、 ``mapreduce.output.fileoutputformat.compress`` 、 ``hive.exec.compress.output`` 。

ORC文件
  这种文件格式是Hive的一大亮点。这种文件结构的具体详情请看 `大数据：Hive - ORC 文件存储格式 <https://www.cnblogs.com/ITtangtang/p/7677912.html>`_

  简单来说，它将一个表的数据分为N个文件，每个文件中除了存储数据外，还存储了一些文件的描述信息、文件meta信息（包括整个文件的统计信息）、所有 **段** 的信息、schema信息。每个文件中，每M行的数据何为一 **段** ，每 **段** 除了存储数据外，还有每列的统计信息、 **段** 位置、所有的stream类型和位置。 **段** 内的数据按照列进行存储。

  **ORC** 对于嵌套格式也能支持，具体实现可以参考 **指针** 、 **引用** 等名词。

Parquet文件
  实现结构与 **ORC文件** 类似，但是似乎没有相关的统计信息、Index信息。具体内容以后在补充。

  **Parquet** 对于嵌套格式也能支持，具体实现似乎与 **ORC** 不同，“不可以”参考 **指针** 、 **引用** 等名词。相关资料 `Parquet与ORC：高性能列式存储格式 <https://blog.csdn.net/yu616568/article/details/51868447/>`_

  **Parquet** 与 **ORC** 的对比有时间再去详细了解。

数据模型
  Hive数据模型中包含如下几个名词： **数据库** 、 **表** 、 **分区** 、 **桶** 或者 **聚集** 、 **记录** 。

数据库
  实际上它是 **warehouse** 目录的一个子目录（目录名就是数据库名字）。

表
  Hive中， **表** 分为 **内表** 、 **外表** 。它是数据库目录下的一个子目录。

分区
  就是根据表中某列不同的值对表进行切分。一旦指定 **分区列** ，所有记录会按照这些组合列的不同值或单个值，保存到表目录的 **子目录** 。

  分区列不能与其他列重名。当把数据导入到表的时候，分区列必须是单独的列。语句： ::

    create table t1(
        id      int
       ,name    string
       ,hobby   array<string>
       ,add     map<String,string>
    )
    partitioned by (pt_d string)
    row format delimited
    fields terminated by ','
    collection items terminated by '-'
    map keys terminated by ':'
    ;

    load data local inpath '/home/hadoop/Desktop/data' overwrite into table t1 partition ( pt_d = '201701');

  被装在的数据格式为： ::

    1,xiaoming,book-TV-code,beijing:chaoyang-shagnhai:pudong
    2,lilei,book-code,nanjing:jiangning-taiwan:taibei
    3,lihua,music-book,heilongjiang:haerbin

  Hive中分区分为： **静态分区** 、 **动态分区** 、 **混合分区** 。

聚集（cluster）
  又称为 **桶** （bucket）。

  记录根据某一列（需要制定）的值进行hash计算，然后根据hash值分别保存到对应叶级目录下的文件中。这个过程叫做 **聚集** 或 **桶** 。

  Hive用户可以指定每个分区的桶数，如果表没有分区，那么还可以指定每张表的桶数。 Hive会计算桶列的hash值，再以桶的个数取模来计算某条记录属于哪个桶。

  **分桶** 有利于数据采样。

  分桶时，Reduce任务数量要等于桶数，这很重要，只有这样才能得到正确的桶数。可以通过两种方法来实现，一种方法是为每个作业显式设定Reduce任务数，另一种方法是设置 ``hive.enforce.bucketing`` 为true，这样Hive会自动对数据分桶。

静态分区
  在编译期便可获取列的分区信息，建表时。

动态分区
  在查询执行的时候才会确定分区。

  所有的动态分区列应该出现在SELECT语句的尾部，并且先后顺序需符合PARTITION中指定的顺序。

  例子： ::

    set hive.enforce.bucketing = true;
    set hive.enforce.sorting = true;
    set hive.exec.dynamic.partition = true;
    set hive.exec.dynamic.partition.mode=nonstrict;
    set hive.exec.max.dynamic.partitions.pernode=1000;
    FROM MasteringHadoop.worldcities_external
    INSERT OVERWRITE TABLE MasteringHadoop.worldcities
    PARTITION(region_p)
    SELECT code, name, fullName, region, population, lat, long, region
    WHERE region IS NOT NULL;

  动态分区不允许主分区采用动态列而副分区采用静态列，这样将导致所有的主分区都要创建副分区静态列所定义的分区。

  Hive动态分区默认是关闭的。

  如果动态导入的数据与现有分区重复，那么会覆盖现有分区。

  ``hive.exec.default.partition.name`` 默认值为 ``__HIVE_DEFAULT_PARTITION__`` ，当动态分区列值为空值或者NULL时，使用此名称。

  动态分区有三个限制：DML语句可生成的总分区数上限，由配置 ``hive.exec.max.dynamic.partitions`` 限定；单个Map或Reduce任务所能产生的分区总数有上限，有 ``hive.exec.max.dynamic.partitions.pernode`` 限定；当执行DML语句时，由Map和Reduce任务总共产生的文件数有上限，有 ``hive.exec.max.created.files`` 。

  不允许所有分区列都是动态的。相关配置 ``hive.exec.dynamic.partition.mode`` 。

  分区既然是HDFS目录，那就可以通过hdfs put命令直接向HDFS添加分区。然而，元存储拥有所有表的元数据，它不会自动识别这些直接被添加到HDFS的分区。 Hive提供了命令 ``MSCK REPAIR TABLE tableName;`` ，可以自动地更新元存储来恢复分区。如果基于亚马逊EMR，这个命令是 ``ALTER TABLE tableName``

RECOVER PARTITIONS;。

Hive表索引
  分为 **紧凑索引** （compact）和 **位图索引** （bitmap）

   Hive索引也是一张表，存放在HDFS中。

----------

名词摘录

数据仓库
  数据仓库的数据主要供企业决策分析之用，所涉及的数据操作主要是数据查询，一旦某个数据进入数据仓库以后，一般情况下将被长期保留，也就是数据仓库中一般有大量的查询操作，但修改和删除操作很少，通常只需要定期的加载、刷新。

  数据仓库是面向主题的、是集成的、是不可更新的、是随时间而变化的。
  
  数据仓库中的数据是在对原有分散的数据库数据抽取、清理的基础上经过系统加工、汇总和整理得到的，必须消除源数据中的不一致性，以保证数据仓库内的信息是关于整个企业的一致的全局信息。

Apache Thrift
  一种可伸缩的跨语言服务开发框架。

  具体的应用场景可以查看 `Thrift应用举例 <https://www.ibm.com/developerworks/cn/java/j-lo-apachethrift/index.html>`_

  以Java语言为例，我们要创建一个名为 `Hello` 的服务。它会根据你注册的服务结构，创建 **服务接口定义** 即 ``Hello.Iface`` 接口、客户端的调用逻辑 ``*.Client`` 、服务器端的处理逻辑 ``Hello.Processor`` 。然后我们需要分别编写下面三个类：实现 ``Hello.Iface`` 接口的类 ``HelloServiceImpl.java`` ； 服务端实现类 ``HelloServiceServer.java`` ，它将调用 ``Hello.Processor`` 启动服务（包括端口监听）；客户端实现类 ``HelloServiceClient.java`` ，可以访问 `Hello` 服务（ ``Hello.Client`` 可以“访问” ``Hello.Iface`` 中声明的方法）。

Apache Derby
  一种开源的关系型数据库（RDBMS）。这个项目在Apache还存活着！（我还以为死了呢）

SerDe
  Serialize/Deserilize的简称。代表序列化（serialization）和反序列化（deserialization），用来读写Hive表中的行。 **SerDe** 组件是文件格式和行对象之间的桥梁。如： ``HDFS File → InputFileFormat → < key,value > → DeSerializer → Row Object`` ; ``Row Object → Serializer → < key,value > → OutputFileFormat → HDFS file`` 。

外表（external table）
  Hive中，通过 ``create external table`` 创建外表。

  通过建立 **外表** ，Hive可以在现存数据目录上创建表结构。

  Hive不允许对单个文件建立表结构。
  
  **外表** 的好处是：表结构和数据是解绑的，删除表并不会删除数据，表相当于就是定义了去解析相对应的文件时的规范而已。即你如果通过语句 ``del table`` 的话，之后将 **外表** 的“元数据”（表结构）删除掉，具体的数据不会被删除。

  与 **外表** 相对的就是 **内表** 了。创建语句： ::

    // 创建内表：
    create table zz (name string , age string) location '/input/table_data';
    load data inpath '/input/data' into table zz;

    // 创建外表：
    create external table et (name string , age string)
    load data inpath '/input/edata' into table et;

--------

配置摘录

io.seqfile.compression.type
  Hive根据io.seqfile.compression.type配置项的值来确定“表”底层存储—— **Sequence文件** 的压缩方式。
  
  此配置有两种值：
  
  - ``RECORD`` ——压缩每条记录；
  - ``BLOCK`` ——缓冲区文件大小达到1 MB就压缩一次。

mapreduce.output.fileoutputformat.compress.codec
  设置 **MapReduce** 输出的 **压缩编码器** 

mapreduce.output.fileoutputformat.compress
  设置 **MapReduce** 输出的 **压缩编码器** 功能是否启用： ``true`` or ``false``

hive.exec.compress.output
  Hive中，将这个值设为true后， Hive的查询结果便会保存为 **LZO** 压缩格式。

hive.metastore.warehouse.dir
  可以配置数据库和表在HDFS中的存放路径。 Hive安装配置目录（conf）中的hive-default.xml 或 hive-site.xml文件定义了这些配置。

hive.enforce.bucketing
  为true，这样Hive会自动对数据分桶。即Reduce任务数量要等于桶数。

hive.exec.default.partition.name
  默认值为 ``__HIVE_DEFAULT_PARTITION__`` ，当动态分区列值为空值或者NULL时，使用此名称。

hive.exec.max.dynamic.partitions
  决定了DML语句可生成的总分区数上限。默认为 ``1000`` 。

hive.exec.max.dynamic.partitions.pernode
  决定了单个Map或Reduce任务所能产生的分区总数上限。默认为 ``100`` 。

hive.exec.max.created.files
  当执行DML语句时，这个配置决定了“由Map和Reduce任务总共产生的文件数上限”。默认为 ``100000`` 。

hive.exec.dynamic.partition.mode
  是否允许所有分区列都是动态的。默认为 ``strict`` 。 ``strict`` 不允许； ``nonstrict`` 允许。

-------

数据定义语言（DDL）摘录

静态分区
  语句： ::

    create table t1(
        id      int
       ,name    string
       ,hobby   array<string>
       ,add     map<String,string>
    )
    partitioned by (pt_d string)
    row format delimited
    fields terminated by ','
    collection items terminated by '-'
    map keys terminated by ':'
    ;

    load data local inpath '/home/hadoop/Desktop/data' overwrite into table t1 partition ( pt_d = '201701');

  被装在的数据格式为： ::

    1,xiaoming,book-TV-code,beijing:chaoyang-shagnhai:pudong
    2,lilei,book-code,nanjing:jiangning-taiwan:taibei
    3,lihua,music-book,heilongjiang:haerbin

动态分区
  语句： ::

    set hive.enforce.bucketing = true;
    set hive.enforce.sorting = true;
    set hive.exec.dynamic.partition = true;
    set hive.exec.dynamic.partition.mode=nonstrict;
    set hive.exec.max.dynamic.partitions.pernode=1000;
    FROM MasteringHadoop.worldcities_external
    INSERT OVERWRITE TABLE MasteringHadoop.worldcities
    PARTITION(region_p)
    SELECT code, name, fullName, region, population, lat, long, region
    WHERE region IS NOT NULL;

创建外表
  语句： ::

    // 语句一
    create external table et (name string , age string)
    load data inpath '/input/edata' into table et;

    // 语句二
    CREATE EXTERNAL TABLE MasteringHadoop.worldcities_external(code
    VARCHAR(5), name STRING, fullName STRING, region INT, population
    BIGINT, lat FLOAT, long FLOAT)
    COMMENT 'This is the world cities population table'
    ROW FORMAT DELIMITED FIELDS TERMINATED BY ','
    STORED AS TEXTFILE
    LOCATION '/user/sandeepkaranth/worldcitiespop';

  ``ROW FORMAT`` 定义了此表序列化与反序列化的语义。如果没有指定 ``ROW FORMAT`` ，或者说只指定了 ``ROW FORMAT DELIMITED`` ，那么Hive会使用原生的 **SerDe** 来处理表中的行。 

  ``STORED AS`` 语句定义了表的底层文件格式为 ``TEXTFILE`` 。

  ``LOCATION`` 显示了表中HDFS中的存储位置。一旦使用了 ``EXTERNAL`` 关键字，就不会额外创建HDFS目录。

  疑问，这里使用了 ``EXTERNAL`` ，如果 ``LOCATION`` 指向的目录不存在，会出现什么问题？

创建内表，同事也是静态分区与分桶
  语句： ::

    // 语句一
    create table zz (name string , age string) location '/input/table_data';
    load data inpath '/input/data' into table zz;

    // 语句二
    CREATE TABLE MasteringHadoop.worldcities(code VARCHAR(5), name
    STRING, fullName STRING, region INT, population BIGINT, lat FLOAT, long FLOAT)
    COMMENT 'This is the world cities population table'
    PARTITIONED BY (region_p INT)
    CLUSTERED BY (code) SORTED BY (code) INTO 2 BUCKETS
    ROW FORMAT DELIMITED FIELDS TERMINATED BY ','
    STORED AS SEQUENCEFILE;

  ``STORED AS`` 定义此表的底层文件格式为 ``SEQUENCEFILE`` 。

  ``PARTITIONED BY`` 定义了分区。

  ``CLUSTERED BY`` 定义了分桶， ``SORTED BY`` 定义了分桶内的排序， ``INTO 2 BUCKETS`` 定义了分桶的数量。
  
数据导入
  使用DML语句，用之前建立的外表 `MasteringHadoop.worldcities_external` 对表 `MasteringHadoop.worldcities` 进行计算（即数据导入）

  这也涉及了 **动态分区**

  语句： ::

    set hive.enforce.bucketing = true;
    set hive.enforce.sorting = true;
    set hive.exec.dynamic.partition = true;
    set hive.exec.dynamic.partition.mode=nonstrict;
    set hive.exec.max.dynamic.partitions.pernode=1000;
    FROM MasteringHadoop.worldcities_external
    INSERT OVERWRITE TABLE MasteringHadoop.worldcities
    PARTITION(region_p)
    SELECT code, name, fullName, region, population, lat, long, region
    WHERE region IS NOT NULL;

  疑问：这段话中的“动态分区”，是针对表 ``worldcities_external`` ，还是针对表 ``worldcities`` ，如果这个“动态分区”与原表的“静态分区”冲突怎么办？

构建索引
  **紧凑索引** ： ::

    USE MasteringHadoop;
    CREATE INDEX worldcities_idx_compact ON TABLE worldcities (name) AS 'COMPACT' WITH DEFERRED REBUILD;

  **位图索引** ： ::

    USE MasteringHadoop;
    CREATE INDEX worldcities_idx_bitmap ON TABLE worldcities (name) AS 'BITMAP' WITH DEFERRED REBUILD;

  查询上面两个索引的结构： ::

    DESCRIBE masteringhadoop__worldcities_worldcities_idx_compact__;
    DESCRIBE masteringhadoop__worldcities_worldcities_idx_bitmap__;

-------

疑问

- 对已有数据的目录，创建 **外表** 并 **分区** 、 **分桶** 的时候，Hive会如何操作？如果在其他表的目录下，创建 **表** （甚至是 **外表** ) 并同样有 **分区** 、 **分桶** ，会出现什么样的情形？
- 对于 ``GROUP`` ，在Hive中有何特殊？
- 如果Hive中，一个表中同时执行两个不同动态分区的查询，会如何？如果对一个表，今天执行查询的动态分区与昨天不一样，那么昨天的数据分区会先“抹除”吗？
- Hive中， **紧凑索引** 与 **位图索引** 的不同在哪里？他们俩区别是什么？ **位图索引** 相对 **紧凑索引** 多出来的 ``_bitmaps`` 有何用？
- Hive的 **元存储** 部分： **表** 、 **分区** 、 **表结构** 、 **字段类型** 以及 **表存放路径**  等信息，不是在HDFS中就有了吗？为什么还要在 **元存储** （即 **RDBMS** ）中存一份？这个能优化多大的效率？维护两者的一致性不是要耗费更多的精力？
- 关于“分桶时，Reduce任务数量要等于桶数，这很重要，只有这样才能得到正确的桶数。可以通过两种方法来实现，一种方法是为每个作业显式设定Reduce任务数，另一种方法是设置 ``hive.enforce.bucketing`` 为true，这样Hive会自动对数据分桶。”这段话，如果我的语句中设计 **分桶** 了，但是“两种方法”都没有做，会如何？
