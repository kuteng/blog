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
- `window` (窗口）在这里是什么意思？

  - 聚合事件（比如计数、求和）在流上的工作方式与批处理不同。流上的聚合需要由 `窗口` 来划定范围，比如 “计算过去的5分钟” ，或者 “最后100个元素的和” 。
  - 窗口可以是 事件驱动的 （比如：每30秒）或者 数据驱动的 （比如：每100个元素）。窗口通常被区分为不同的类型，比如 滚动窗口 （没有重叠）， 滑动窗口 （有重叠），以及 会话窗口 （由不活动的间隙所打断）

`Event Time <https://ci.apache.org/projects/flink/flink-docs-release-1.5/dev/event_time.html>`_
  - 数据流模型的资料：
  
    - `Dataflow Model <https://www.oreilly.com/ideas/the-world-beyond-batch-streaming-101>`_
    - `The Dataflow Model paper <https://research.google.com/pubs/archive/43864.pdf>`_

  - `Watermarks` 的作用：表示流中所有 `Event Time` < `Watermarks Time` 的元素，都已经传输过来了。
  - `Note that the Kafka source supports per-partition watermarking, which you can read more about here.`

Flink的应用场景。
^^^^^^^^^^^^^^^^^^^^
- 种类繁多的（有时候不稳定的）数据源 —— 不同渠道上的广告创意。
- 有状态的应用 —— 我将其理解为：不同平台上的category分类等。 
- 数据需要被快速处理
- 数据体量大

疑问点
^^^^^^^^^^^^^^^^^^^^
- 在 `DataSet and DataStream <https://ci.apache.org/projects/flink/flink-docs-release-1.5/dev/api_concepts.html#dataset-and-datastream>`_ 中提及 ``DataSet`` 和 ``DataStream`` 是不可变的，无法之间add/remove数据，那么在“流”场景下，如何保证随着“流动”处理到新数据呢？
- 在 `Lazy Evaluation <https://ci.apache.org/projects/flink/flink-docs-release-1.5/dev/api_concepts.html#lazy-evaluation>`_ 中，这段话那我没有理解：
  `The lazy evaluation lets you construct sophisticated programs that Flink executes as one holistically planned unit.`

- ``In the following discussion we will use the DataStream API and keyBy. For the DataSet API you just have to replace by DataSet and groupBy.`` 是否意味着 ``DataStream`` 只能使用 ``keyBy`` 不能用 ``groupBy`` 、 ``DataSet`` 只能用 ``groupBy`` 不能用 ``keyBy`` 。例外我如何验证 ``keyBy`` 和 ``groupBy`` 的效果？另外， ``keyBy`` 的效果是否和 ``groupBy`` 的效果一样？如果一样，为什么取名不同呢？
- 在 ``StreamJob`` 中， ``print()`` 方法之后需要跟 ``env.execute("...")`` 。但是在 ``BatchJob`` 中这样做会报错。这一点确定吗？为什么？

`Event Time <https://ci.apache.org/projects/flink/flink-docs-release-1.5/dev/event_time.html>`_
  - `What's the event? The event is data object?` 这些 `Time` 又有什么用？只在 `Time Window` 是会用到吗？
  - 为什么： `ingestion time programs cannot handle any out-of-order events or late data` ？
  - 在 `Time Window` 中， `Processing Time` 、 `Event Time` 、 `Ingesion Time` 有什么区别？
  - `How does the operator advancess its event time?`
  - 关于 `Late Elements` 部分，没有懂。程序是舍弃这些 `Late Elements` ，还是包容它们？

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
- `Processing time refers to the system time of the machine that is executing the respective operation.`
- `An hourly processing time window will include all records that arrived at a specific operator between the times when the system clock indicated the full hour.`
- `However, in distributed and asynchronous environments processing time does not provide determinism, because it is susceptible to the speed at which records arrive in the system (for example from the message queue), to the speed at which the records flow between operators inside the system, and to outages (scheduled, or otherwise).`
- `Event time is the time that each individual event occurred on its producing device. This time is typically embedded within the records before they enter Flink, and that event timestamp can be extracted from each record. In event time, the progress of time depends on the data, not on any wall clocks. Event time programs must specify how to generate Event Time Watermarks, which is the mechanism that signals progress in event time. This watermarking mechanism is described in a later section, below.`
- `In a perfect world, event time processing would yield completely consistent and deterministic results, regardless of when events arrive, or their ordering. However, unless the events are known to arrive in-order (by timestamp), event time processing incurs some latency while waiting for out-of-order events. As it is only possible to wait for a finite period of time, this places a limit on how deterministic event time applications can be.`
- `Note that sometimes when event time programs are processing live data in real-time, they will use some processing time operations in order to guarantee that they are progressing in a timely fashion.`
- `Ingestion time sits conceptually in between event time and processing time. Compared to processing time, it is slightly more expensive, but gives more predictable results. Because ingestion time uses stable timestamps (assigned once at the source), different window operations over the records will refer to the same timestamp, whereas in processing time each window operator may assign the record to a different window (based on the local system clock and any transport delay).
  Compared to event time, ingestion time programs cannot handle any out-of-order events or late data, but the programs don’t have to specify how to generate watermarks.`
- `Note that in order to run this example in event time, the program needs to either use sources that directly define event time for the data and emit watermarks themselves, or the program must inject a Timestamp Assigner & Watermark Generator after the sources. Those functions describe how to access the event timestamps, and what degree of out-of-orderness the event stream exhibits.`
- `Event time can progress independently of processing time (measured by wall clocks). For example, in one program the current event time of an operator may trail slightly behind the processing time (accounting for a delay in receiving the events), while both proceed at the same speed. On the other hand, another streaming program might progress through weeks of event time with only a few seconds of processing, by fast-forwarding through some historic data already buffered in a Kafka topic (or another message queue).`
- `Watermarks are generated at, or directly after, source functions.`
- `As the watermarks flow through the streaming program, they advance the event time at the operators where they arrive. Whenever an operator advances its event time, it generates a new watermark downstream for its successor operators.`
- `Some operators consume multiple input streams; a union, for example, or operators following a keyBy(…) or partition(…) function. Such an operator’s current event time is the minimum of its input streams’ event times. As its input streams update their event times, so does the operator.`
- `It is possible that certain elements will violate the watermark condition, meaning that even after the Watermark(t) has occurred, more elements with timestamp t’ <= t will occur. In fact, in many real world setups, certain elements can be arbitrarily delayed, making it impossible to specify a time by which all elements of a certain event timestamp will have occurred. Furthermore, even if the lateness can be bounded, delaying the watermarks by too much is often not desirable, because it causes too much delay in the evaluation of event time windows.
  For this reason, streaming programs may explicitly expect some late elements. Late elements are elements that arrive after the system’s event time clock (as signaled by the watermarks) has already passed the time of the late element’s timestamp. See Allowed Lateness for more information on how to work with late elements in event time windows.`
