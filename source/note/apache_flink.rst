Apache Flink
=======================

.. toctree::
   :maxdepth: 1
   :caption: 视频教程:

链接： http://flink-china.org/

- 回顾一下数据仓库的概念。当前产品中是否可以触及这个概念？
- `RocksDB`: 是一个可嵌入的，持久型的key-value存储.
- 了解一些技术： `HDFS` , `Apache NiFi` , `Apache Cassandra`, `Apache Zeppelin`, `Apache Mahout`, `Apache Beam`, `Apache SAMOA`, `Apache Ignite`.
- 一些不懂的概念： `流重放`
- Flink中的时间概念，我还是有些不明白： `事件时间` 、 `摄入时间` 、 `处理时间` 。
- 教程中， `事件` 指的似乎就是一个数据/任务（类似于队列中的一个消息）。不知我说的是否正确？
- `有状态操作` 部分的内容我没有看清楚。
- `容错检查点` 部分的内容我没有看清楚。
- `Distributed Runtime Environment` 部分的内容，实际上我并没有看懂。
- `window` 在这里是什么意思？

Flink的应用场景。
^^^^^^^^^^^^^^^^^^^^
- 种类繁多的（有时候不稳定的）数据源 —— 不同渠道上的广告创意。
- 有状态的应用 —— 我将其理解为：不同平台上的category分类等。 
- 数据需要被快速处理
- 数据体量大

英语
^^^^^^^^^^^^^^^^^^^
- `For distributed execution, Flink chains operator subtasks together into tasks.`
- `Chaining operators together into tasks is a useful optimization: it reduces the overhead of thread-to-thread handover and buffering, and increases overall throughput while decreasing latency.`
- `To control how many tasks a worker accepts, a worker has so called task slots (at least one).`
- `Slotting the resources means that a subtask will not compete with subtasks from other jobs for managed memory, but instead has a certain amount of reserved managed memory.`
- `Note that no CPU isolation happens here; currently slots only separate the managed memory of tasks.`
- `The result is that one slot may hold an entire pipeline of the job. Allowing this slot sharing has two main benefits:`
- `A Flink cluster needs exactly as many task slots as the highest parallelism used in the job. No need to calculate how many tasks (with varying parallelism) a program contains in total.`
- `Without slot sharing, the non-intensive source/map() subtasks would block as many resources as the resource intensive window subtasks. With slot sharing, increasing the base parallelism in our example from two to six yields full utilization of the slotted resources, while making sure that the heavy subtasks are fairly distributed among the TaskManagers.`
- `The exact data structures in which the key/values indexes are stored depends on the chosen state backend.`
- `In addition to defining the data structure that holds the state, the state backends also implement the logic to take a point-in-time snapshot of the key/value state and store that snapshot as part of a checkpoint.`
- `Programs written in the Data Stream API can resume execution from a savepoint. Savepoints allow both updating your programs and your Flink cluster without losing any state.`
- `They rely on the regular checkpointing mechanism for this. During execution programs are periodically snapshotted on the worker nodes and produce checkpoints. For recovery only the last completed checkpoint is needed and older checkpoints can be safely discarded as soon as a new one is completed.`
- `Savepoints are similar to these periodic checkpoints except that they are triggered by the user and don’t automatically expire when newer checkpoints are completed.`
