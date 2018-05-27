读书笔记
=======================
链接： http://flink-china.org/

- 回顾一下数据仓库的概念。当前产品中是否可以触及这个概念？
- 了解一些技术： `HDFS` , `Apache NiFi` , `Apache Cassandra`, `Apache Zeppelin`, `Apache Mahout`, `Apache Beam`, `Apache SAMOA`, `Apache Ignite`.
- 一些不懂的概念： `流重放`
- Flink中的时间概念，我还是有些不明白： `事件时间` 、 `摄入时间` 、 `处理时间` 。
- 教程中， `事件` 指的似乎就是一个数据/任务（类似于队列中的一个消息）。不知我说的是否正确？
- `有状态操作` 部分的内容我没有看清楚。
- `容错检查点` 部分的内容我没有看清楚。

Flink的应用场景。
^^^^^^^^^^^^^^^^^^^^
- 种类繁多的（有时候不稳定的）数据源 —— 不同渠道上的广告创意。
- 有状态的应用 —— 我将其理解为：不同平台上的category分类等。 
- 数据需要被快速处理
- 数据体量大

英语
^^^^^^^^^^^^^^^^^^^
- `For distributed execution, Flink chains operator subtasks together into tasks.`
- `Chaining operators together into tasks is a useful optimization: it reduces the overhead of thread-to-thread handover and buffering, and increases overall throughput while decreasing latency. `
