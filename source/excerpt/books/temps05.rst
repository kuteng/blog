读书笔记 2018-10-04
=====================================

.. toctree::
   :maxdepth: 1

时间：10月04日

书籍：《精通Hadoop》

章节：序列化和Hadoop I/O

进度：96页 - 115页

----------

重点摘录
^^^^^^^^^^^^^^^^^^^^^

- Hadoop中不同的组件使用远程调用（Remote Procedure Call， RPC）进行交互。而这个过程也是涉及 **序列化** 及 **反序列化** 的。
- Hadoop中，序列化/反序列化都需要通过接口 ``org.apache.hadoop.io.WritableComparable`` 。
- ``org.apache.hadoop.util.StringUtils`` 工具类中有一些静态函数，可以帮助我们把字节数组转换为十六进制字符串。如 ``byteToHexString``
- ``IntWritable`` 与 ``VIntWritable`` 的区别，一个固定长度，一个边长。
- ``Text`` 是 `Writable` 版的 ``String`` 类型。它代表UTF-8字符集合。相比Java的 ``String`` 类， Hadoop中的 ``Text`` 类是可变的。
- 对比Java序列化与Hadoop序列化：Java序列化因为要标识对象，所以消耗了更多的字节。 ``Writable`` 类则从字节流中读取字段，并假设字节流的类型。Writable类只适用于Java编程语言
- Java不假设序列化的值类型，这是Java序列化低效的根本原因。
- gzip算法较好地平衡了存储和速度。其他算法，如LZO、 LZ4、 Snappy，虽然压缩速度很快，但它们的压缩率不高。 Bzip2算法压缩速度较慢，但压缩率最高。

Avro 序列化
  **Apache Avro** 是一个流行的数据序列化框架。使用 ``Schema`` 来读取和写入数据， ``Schema`` 使用 ``JSON`` 进行描述。在处理数据时，通过新旧schema并存的方式来应对schema的变化。

  它支持多种数据结构的序列化；支持多种编程语言，而且序列化速度快、字节紧凑；Avro代码生成功能是可选的，即无需生成类或代码，也可读写数据或使用RPC传输数据。

  **Avro** 有两种序列化方案：二进制和JSON。

  **Avro** 序列化数据的代码在 `101页` 。需要注意的类有 ``Schema`` 、 ``GenericData.Record`` 、 ``GenericRecord`` 、 ``DataFileWriter`` 。

  Hadoop广泛支持在MapReduce作业中使用Avro序列化和反序列化。在Hadoop 2.X中，只需重用内置的 ``Mapper`` 与 ``Reducer``` 类即可。 ``AvroKey`` 可以作为 ``Mapper`` 与 ``Reducer`` 类的输入或输出类型。 ``AvroKeyInputFormat`` 是一个特殊的 ``InputFormat`` 类，用于从输入文件中读取 ``AvroKey`` 。 实现代码在 `101页` 。

  Pig通过扩展可以支持Avro。  ``AvroStorage`` 实现了 ``LoadFunc`` 与 ``StoreFunc`` 接口，来支持读取Avro文件。然而， Pig和Avro集成后有一些限制和假设。

  Hive有个SerDe模块称为 ``AvroSerde`` ，可以使用Avro来读写Hive表。它能自动从Avro的输入推断出Hive表的schema。大多数的Avro类型都有对应的Hive表类型。如果某些Avro类型在Hive中不存在，它们会被自动转换为Hive中已有的类型。比如Avro有枚举的概念，但Hive没有。所有Avro中的枚举类型会转换成Hive中的字符串类型。

Pig和Avro集成后有一些限制和假设
  ``AvroStorage`` 不支持内嵌记录类型；

  ``AvroStorage`` 不支持数据类型map；

  联合（union）只支持null值；

  假设目录或子目录中的所有文件都有同样的schema；

  当使用 ``AvroStorage`` 将数据保存为 Avro格式的时候，所有字段都将是 null值联合（null-valued union），因为Pig中没有非null值（non-null-valued）的字段；

  当在Pig关系中调用STORE时， Avro文件中可能会含有TUPLE包装类；

  不支持基于JSON编码的Avro文件；

  使用 ``AvroStorage`` 后，不会进行列裁剪（column-pruning）优化。

与 **Protocol Buffers** 、 **Thirft** 比较， **Avro** 的优点
  **Avro** 支持动态类型，如果为了满足性能要求也支持静态类型。 

  **Avro** 内置于Hadoop，但是其他的框架并非如此。

  **Avro** 的schema定义用JSON描述，而不是任何专有的IDL。

Sequence 文件
  Sequence文件是包含二进制键值对的一种文件格式 

  Sequence文件中的每条记录都含有一个键和键对应的值。

  Sequence文件把多个较小的文件合并成单个较大的文件，这样可以缓解Hadoop中由于小文件过多而产生的问题。（此时，文件名作为键，键的值就是文件内容）

  Sequence文件还能和快速压缩方法集成，如LZO或Snappy，从而在提高处理速度的同时，还能减少存储和带宽的消耗。

  Sequence文件中，因为 **同步标记** 会有存储开销，所以它们的总大小不会超过总文件大小的1%。

  Sequence中，当启用压缩的时候（非 **块压缩** ），键不会被压缩。

  Sequence文件的读写代码示例，参见 `109页` 。

  Sequence文件的结构如下图： |hadoop_sequence|

  .. |hadoop_sequence| image:: /images/except_books_mastering_hadoop_sequence.png
     :width: 100%

MapFile 格式文件
  这种文件格式会生成两个文件，一个存放数据、一个存放索引。两个的格式都是 ``Sequence`` ，存放数据的与 ``Sequence文件`` 一样，存放索引的内容是 **键** 与 **偏移量** 。

  MapFile格式有助于进行Map侧连接。

  MapFile格式文件的读写参见 `112页` 。

SetFile文件
  这种文件格式保存键集合，并可对键进行集合操作。

ArrayFile文件
  这个特别的文件格式可视为SetFile的补充。它只存有值，不存键。

BloomMapFile文件
  这种文件格式是MapFile的变体。除了索引和数据文件之外，它还包含一个布隆文件（Bloom file）。

  对于基于 **&键-值** 格式的大文件，如果键很稀疏，使用这种格式的查询速度比 **MapFile** 要快得多。

分片与压缩
  Map任务处理数据的单个 **分片** ， **分片** 通常是保存在HDFS中的一个 **文件块** 。但是，大多数压缩算法并不允许在任意位置读取文件。在这种情况下， Hadoop不会切分文件，而是把整个文件交给单个Map任务处理。在多数情况下，这种处理方式是不恰当的。

  尽管有些压缩算法（如gzip）是基于 **块** 的压缩技术，但这里所说的块与HDFS中的块没有关系，或者说HDFS的块对算法透明。

  Hadoop通过文件的扩展名来识别不同的压缩格式。

Hadoop启用压缩后，一些推荐的策略
  应用程序可以在预处理阶段切分文件，然后使用流行的压缩技术对文件分片进行压缩。压缩后的文件块大小要几乎等于HDFS块大小，这样才算最优。这种情况下，不用担心压缩算法是否可切分

  对文件可直接应用可切分的压缩算法，如bzip2或LZO（需要在LZO文件上构建索引）。

  有些文件格式，如SequenceFile、 MapFile、 RCFile，天生就支持切分。它们也可被压缩。

  把数据保存在专门的文件格式中，这是行业首选的实践方法。这样可以获得速度与压缩之间的平衡。如Hive的 ``ORC`` 。

----------

DML摘录
^^^^^^^^^^^^^^^^^^^

- Pig使用Avro扩展 ::

    REGISTER avro-1.4.0.jar
    REGISTER json-simple-1.1.jar
    REGISTER piggybank.jar
    avroCountry = LOAD ‘countrycodes.avro’ USING AvroStorage(‘{"namespace": "MasteringHadoop.avro",
      "type": "record",
      "name": "Country",
      "fields": [
          {"name": "countryCode", "type": "string"},
          {"name": "countryName",  "type": "string"}
      ]
    }’);

- Hive中使用 ``Avro`` ::

    CREATE EXTERNAL TABLE avrocountry
    ROW FORMAT SERDE 'org.apache.hadoop.hive.serde2.avro.AvroSerDe'
    STORED AS
    INPUTFORMAT 'org.apache.hadoop.hive.ql.io.avro.AvroContainerInputFormat'
    OUTPUTFORMAT 'org.apache.hadoop.hive.ql.io.avro.AvroContainerOutputFormat'
    LOCATION '/user/sandeepkaranth/avrocountrydata'
    
    TBLPROPERTIES ( 'avro.schema.literal'='
    {"namespace": "MasteringHadoop.avro",
      "type": "record",
      "name": "Country",
      "fields": [
          {"name": "countryCode", "type": "string"},
          {"name": "countryName",  "type": "string"}
      ]
    }')
    ;

  此 ``DDL`` 语句中：使用 ``AvroContainerInputFormat`` 作为表的InputFormat；使用 ``AvroContainerOutputFormat`` 作为表的OutputFormat； ``TBLPROPERTIES`` 中有schema的定义，此外schema还可以写在文件中，通过URL或链接来定义schema。如果是文件URL或路径的话，属性名应使用 ``avro.schema.url`` 而不是 ``avro.schema.literal`` 。

----------

名词摘录
^^^^^^^^^^^^^^^^^^^^^

序列化
  把结构化的数据转换为原始形式（Byte）的过程。

反序列化
  把数据从原始比特流形式重建为结构形式

RPC
  远程调用

----------

疑问

- 在 **远程调用** 领域 **Avro** 与 **Thrift** 的优劣？
- **Avro** 、 **ORC** 、 **Sequence** 的比较。 **Sequence** 与其他两个的不同，它是基于“键值对”存储数据的。
- 序列化、压缩和文件格式的不同之处是？
- Pig对Null的处理是需要注意一下的。比如多文件的时候，有些情况它会将字段的值读为“空字符串”，有些时候会读为“NULL”。


- Hadoop组件之间使用 **Aveo** ； Map、Reduce之间使用 **Sequence** ；Hive持久化数据使用 **ORC** 。
