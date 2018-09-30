读书笔记 2018-09-29
=====================================
.. toctree::
   :maxdepth: 1

时间：9月26日 - 9月29日

书籍：《精通Hadoop》

进度：34页 - 66页

--------

**MapReduce** 中的 **Combiner**
  在本地节点将每个map任务输出的中间结果做本地聚合。它可以减少需要传递给reducer的数据量。

  Map任务的JVM的堆空间大小可以使用 ``mapreduce.map.java.opts`` 参数设置，默认值是–Xmx1024m。

  通过调用 ``setCombinerClass()`` 来指定一个作业的combiner。

  如果指定了Combiner，它可能在两个地方被调用：当缓存溢出线程将缓存存放到磁盘时；当缓存溢出文件正在被合并到单一输出文件以便给Reduce任务消费时。

  个人预测， **Combiner** 的输出的数据结构应该需要与 **Map** 保持一致。

--------

**MapReduce** 中数据连接的处理

**Reduce** 侧的连接
  这个连接的 **shuffle** 步骤会需要非常多的资源。

  基本的原理是，在每条记录添加一个标签指明数据的来源，并在Map任务中提取连接键。Reduce任务收到同一个连接键的所有记录并执行连接操作。具体实现见 `37页`

  如果参与连接的数据集非常小，可以通过一个旁路通道（side channel，例如分布式缓存）将数据派送给每个Reduce任务。—— 这个思路很棒。

**Map** 侧的连接
  Map侧的连接需要等待参与连接的数据集满足如下任一条件：除了参与连接的键外，所有的输入都必须按照连接键排序。输入的各种数据集必须拥有相同的分区数；所有具有相同键的记录需要放在同一个分区中；如果其中的一个数据集足够小，旁路的分布式通道（例如分布式缓存）可以用在Map侧的连接中。

  当Map任务对其他MapReduce作业的结果进行处理时， Map侧的连接格外具有吸引力，因为这种情况下上述的 ``条件一`` 就自动满足了。

  备注：Map侧连接的两个条件，细细领会感觉非常有道理。

--------

Pig
  运行在Hadoop上的应用程序，使用户可以仿照SQL对Hadoop中的数据（文件）进行操作。多为查询、聚合，鲜有插入、更新。示例： ::

    cc = load 'countrycodes.txt' using PigStorage(',') as (ccode:chararray, cname:chararray);
    ccity = load 'worldcitiespop.txt' using PigStorage(',') as (ccode:chararray, cityName:chararray, cityFullName:chararray, region:int, population:long, lat:double, long:double);
    filteredCcity = filter ccity by population is not null;
    joinCountry = join cc by ccode, ccity by ccode;
    generateRecords = foreach joinCountry generate cc::cname, ccity::cityName, ccity::population;
    groupByCountry = group generateRecords by cname;
    populationByCountry = foreach groupByCountry generate group, SUM(generateRecords.population);

Pig与SQL的不同
  书中提到几点两者的不同，但是我暂时认为没有记录的价值

Pig的三种执行模式
  交互模式、批处理模式、嵌入模式（UDF属于这种模式）

Pig中的数据类型
  原生数据类型：int、long、float、double、chararray、bytearray。

  复合数据类型：Map、Tuple、Bag。

  **注意** : Bag这种数据类型。另外：Pig中的Bag数据类型可以溢出保存到磁盘中，这样它就可以拥有数量庞大的tuple，而无需顾虑内存的限制。

  **疑问** : **GROUP** 操作的结果是那种类型？

编译和执行Pig脚本的三个主要阶段
  逻辑计划、物理计划、MapReduce计划

  具体执行细节中，Pig首先会解析语句的语法错误，同时验证输入文件和输入的数据结构。如果有模式 （schema）存在，那么本阶段还会进行类型检查。然后生成一个DAG的逻辑计划，之后一次经历三个阶段进行执行。

  这里将的 **模式** 可以理解为“表结构”。它定义了读取一个文件时，各列的名称和类型。

逻辑计划
  **DAG** 是指有向无环图。逻辑计划的DAG中，节点是操作符，边是数据流。可以仿照前面 `Pig的示例` 做一个DAG出来，具体图像查看 `51页` 。

物理计划
  物理计划中，除逻辑操作符以外，还有几个物理操作符： Local Rearrange（LR）、 Global Rearrange（GR）、和 Package（P）操作符。LR操作符对应洗牌的准备阶段，将数据按键进行分区。 GR操作符对应Map和Reduce任务之间真正的洗牌操作。 P操作符则是Reduce过程中的分区操作符（对于这一点有疑问，分区操作不是在Map里进行的吗？）。
  
  像 **GROUP** 、 **COGROUP** 或 **JOIN** 这样的逻辑操作符，会被转换成有序的LR、 GR和P操作符。

MapReduce计划
  将物理计划编译成真实的MapReduce作业。物理计划中的 **LR** 、 **GR** 和 **P** 操作符组成的序列至少需要一个 **Reduce** 任务。而且编译器也会寻找机会在合适的地方添加一些 **Combiner** 。

  必须要说明的是， **GROUP** 操作一般发生在 **Map** 任务中。

有助于我们开发，调试及优化Pig脚本的三个命令
  **DESCRIBE** 命令会显示一个关系的模式（schema）。

  **EXPLAIN** 命令会显示Pig脚本将如何执行。

  **ILLUSTRATE** 命令，据听说这个命令比前两个更加重要与常用。它会抽取部分“样本”数据，然后在这上面进行查询。

--------------------

PIG操作符部分

**FOREACH**
  主要用于将输入关系的每一条记录转换成别的记录。在某些情况下， FOREACH操作符会增加输出数据的记录条数。但这里不是将这些，而是说下面几个操作符与 **FOREACH** 组合使用，他们分别是 **FLATTEN** 、 **嵌套FOREACH** 、 **GOGROUP** 。

**FLATTEN**
  与 **FOREACH** 结合使用，用来解套那些检讨的tuple或bag。但是需要注意：当解套分别作用在tuple和bag上时，语义却是不同的。 **Tuple** 中， **FLATTEN** 只是将所有的嵌套 **Tuple** 都提升到最高级别，如： ``（ 1, （ 2, 3, 4））`` To ``(1,2,3,4)`` ；而对于 **Bag** ， **FLATTEN** 做了一次交叉乘积， bag中的每一元素都会产生一行记录。详见 `55页`

**嵌套FOREACH**
  又称为 **内FOREACH操作符** ，比如关系操作符可以应用在FOREACH操作符的每一条记录上。需要注意的是： `在嵌套的部分中，各种关系操作符被应用于分组bag` 。现在， Pig的嵌套FOREACH操作符支持 **LIMIT** 、 **ORDER** 、 **DISTINCT** 、 **CROSS** 、 **FOREACH** 及 **FILTER** 这些关系操作符。示例： ::

    cc = load 'countrycodes.txt' using PigStorage(',') as (ccode:chararray, cname:chararray);
    ccity = load 'worldcitiespop.txt' using PigStorage(',') as (ccode:chararray, cityName:chararray, cityFullName:chararray, region:int, population:long, lat:double, long:double);

    unionCountryCity = union cc, ccity;
    unionOnSchemaCountryCity = union onschema cc, ccity;
    describe unionCountryCity;
    describe unionOnSchemaCountryCity;

**COGOUP**
  有点类似GROUP操作。它按键聚积n组输入的记录，而不是只针对一组。用法示例： ``groupedCity = cogroup cc by ccode, ccity by ccode;`` ，完整代码详见 `57页` 。但是我对于它的具体输出数据结构很疑惑。

**UNION**
  用来连接两个或多个数据集。
  
  它对 **模式** 没有限制。两个数据集，如果它们模式相同，则结果也是同样的模式。如果一个模式通过强制转换可以变成另一个模式，那么结果将是这个模式。否则，结果就没有模式。

  **UNION** 操作符不保留tuple的顺序，它也不会剔除重复的tuple。

  **UNION** 操作符有一个叫作 **ONSCHEMA** 的关键字，它会给出结果集的模式。这个模式是数据集中所有命名字段的一个并集。 **ONSCHEMA** 关键字要求所有的输入关系有同一个模式。示例： ::

    cc = load 'countrycodes.txt' using PigStorage(',') as (ccode:chararray, cname:chararray);
    ccity = load 'worldcitiespop.txt' using PigStorage(',') as (ccode:chararray, cityName:chararray, cityFullName:chararray, region:int, population:long, lat:double, long:double);

    unionCountryCity = union cc, ccity;
    unionOnSchemaCountryCity = union onschema cc, ccity;
    describe unionCountryCity;
    describe unionOnSchemaCountryCity;

  它的结果是： ::

    Schema for unionCountryCity unknown.
    unionOnSchemaCountryCity: {ccode: chararray,cname:
        chararray,cityName: chararray,cityFullName: chararray,region:
        int,population: long,lat: double,long: double}

  模式的比较包括字段的名称。对于字段有不同名称的数据集，如果使用 **UNION** 操作符的话，结果集将没有模式。这种情况下的解决方法是在 **UNION** 语句前，先使用 **FOREACH** 操作符将那些字段改成相同的名称。

-------------------------

Pig的特殊连接

分段复制连接
  实际上就是 **MapReduce** 中 **Map** 侧的连接。

  当连接的某个输入数据集小到能够全部加载进内存时， Map侧就会将最小的数据集复制到所有的Map任务中，然后执行连接操作。

  分段复制连接可以被用于内连接（inner join）或是左外连接（left-outer join），不能用于右外（right-outer）或是全外连接（full-outer join）。 `我怀疑这段中，译者将“左外连接”与“右外连接”弄反了。`

  ``replicated`` 关键字来使用分段复制连接。示例： ::

    cc = load 'countrycodes.txt' using PigStorage(',') as (ccode:chararray, cname:chararray);
    ccity = load 'worldcitiespop.txt' using PigStorage(',') as (ccode:chararray, cityName:chararray, cityFullName:chararray, region:int, population:long, lat:double, long:double);
    joinCountryCity = join ccity by ccode, cc by ccode using 'replicated';

倾斜连接
  数据倾斜的存在会使某个Reduce任务超载运行，从而影响整个连接的性能。倾斜连接就是为了解决这个问题的。

  数据倾斜连接只可以对两个数据集进行操作。如果你有两个以上数据集需要连接，那么开发者的职责就是将它分解成多个双表连接。

  当使用这种连接时，由于需要采样和构造直方图，所以会增加一些性能开销。据观察，这个额外开销平均在5%左右。

  ``pig.skewedjoin.reduce.memusage`` 参数值用来决定需要多少额外的Reduce任务去处理数据倾斜键。这个属性的默认值是0.5，也就是说JVM堆的50%可分配给Reduce任务去运行这个连接

  通过 ``skewed`` 关键字来使用数据倾斜连接。示例： ::

    ......
    joinCountryCity = join cc by ccode, ccity by ccode using 'skewed';

合并连接
  又称为排序连接。它是在Map侧实现的连接。

  具体条件是：输入的数据都按连接键做了排序。

  这种连接实现中，加入了一个环节，就是对第二个关系通过另外的 **MapReduce** 作业创建了索引（索引是键和偏移（offset）之间的映射，其中的键记录了文件的开始位置）。然后主作业的 **Map** 就借助这个索引中的键与偏移量快速的对数据进行分组、分区。具体实现的描述请看 `60页` 。

  通过 ``merge`` 关键字来使用合并连接： ::

    ...
    joinCountryCity = join cc by ccode, ccity by ccode using 'merge';

-------------------------

疑问：

- **UNION** 操作符部分提到了它的输出没有模式，那么PIG针对这样“没有模式”的输入（ **UNION** 的输出回事下一步操作的输入）会如何处理？
- **UNION** 中的 **ONSCHEMA** 有何实际作用？感觉用到它的地方不会很多。
