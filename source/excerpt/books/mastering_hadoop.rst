《精通Hadoop》
=====================================
.. toctree::
   :maxdepth: 1

随笔（待整理）
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
- Hadoop被应用与做不同的事情： `P23`

  - 用来产生图标和跟踪利用率的统计数据。
  - 云服务提供商用其做日志处理。
  - 用其创建网页索引，执行复杂的广告植入和内容优化算法。

- 几个重点概念（推荐对应记忆）：

  - 资源管理器(Resource Manager)、应用管理器(Application Manager)、节点管理器(Node Manager)、作业(Job)

    - 容器、节点
    - **资源请求** : 由谁发送资源请求呢？
    - 我需要理清楚：容器与节点的异同； ``Application Manager`` 与 ``Job`` 的异同。

  - ``NameNode`` 、 ``DataNode`` 、 ``JournalNode`` 、 ``block report`` 、 ``block storage``

    - **NameNode** 、 **DataNode** 与 **Resource Manager** 、 **Application Manager** 、 **Node Manager** 有什么关联？

  - **数据校验** 、 **数据复制** 、 **数据镜像** 、 **快照** 。

    - 注意他们的不同与互补。
    - 其中 **数据复制** 与 **数据镜像** 有什么区别与优劣？

- TODO: 去了解一下 ``MapReduce``
- Hadoop的 `1.x` 与 `2.x` 的区别之一就是，由 **NameNode冷备份节点** 改为了 **NameNode热备份节点** 。
- **JournalNode** 是什么东西？
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
