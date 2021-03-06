Java调试工具
==========================
命令行工具
^^^^^^^^^^^^^^^
jps
:::::::::::::::
查看所有的jvm进程，包括进程ID，进程启动的路径等等。

jstack
:::::::::::::::
观察jvm中当前所有线程的运行情况和线程当前状态。

系统崩溃了？如果java程序崩溃生成core文件，jstack工具可以用来获得core文件的java stack和native stack的信息，从而可以轻松地知道java程序是如何崩溃和在程序何处发生问题。

系统hung住了？jstack工具还可以附属到正在运行的java程序中，看到当时运行的java程序的java stack和native stack的信息, 如果现在运行的java程序呈现hung的状态，jstack是非常有用的。

用法：

  - ``jstack [option] <pid>`` ：监听某进程
  - ``jstack [option] executable core``
  - ``jstack [ option ] [server-id@]remote-hostname-or-IP``

内容解析
##################
数据结果中，包含JVM线程与用户线程。这里说一下用户线程，其内容类似与： ::

  "qtp496432309-42" prio=10 tid=0x00002aaaba2a1800 nid=0x7580 waiting on condition [0x00000000425e9000]
     java.lang.Thread.State: TIMED_WAITING (parking)
          at sun.misc.Unsafe.park(Native Method)
          - parking to wait for  <0x0000000788cfb020> (a java.util.concurrent.locks.AbstractQueuedSynchronizer$ConditionObject)
          at java.util.concurrent.locks.LockSupport.parkNanos(LockSupport.java:198)
          at java.util.concurrent.locks.AbstractQueuedSynchronizer$ConditionObject.awaitNanos(AbstractQueuedSynchronizer.java:2025)
          at org.eclipse.jetty.util.BlockingArrayQueue.poll(BlockingArrayQueue.java:320)
          at org.eclipse.jetty.util.thread.QueuedThreadPool$2.run(QueuedThreadPool.java:479)
          at java.lang.Thread.run(Thread.java:662)
     Locked ownable synchronizers:
          - None

包含了：

- 线程的状态：waiting on condition(等待条件发生)
- 线程的调用情况；
- 线程对资源的锁定情况；

线程的状态有：

- ``Runnable`` ：正在运行。
- ``Waiton condition`` ：等待某些条件的达成中。最常见的情况是线程在等待网络的读写，比如当网络数据没有准备好读时，线程处于这种等待状态，而一旦有数据准备好读之后，线程会重新激活，读取并处理数据。还有可能是 ``Thread.sleep(5000)`` 。
- ``Waitingfor monitor entry`` ：资源被其他线程占有，等待其释放（注意没有 **wait()** ，它一直初一竞争状态）。
- ``in Object.wait()`` ：资源被其他线程占有，等待其释放，且本线程已经在该资源上执行了 **wait()** 方法。

jstat
:::::::::::::::::
jstat利用JVM内建的指令对Java应用程序的资源和性能进行实时的命令行的监控，包括了对进程的classloader，compiler，gc情况；

特别的，一个极强的监视内存的工具，可以用来监视VM内存内的各种堆和非堆的大小及其内存使用量，以及加载类的数量。

jstat -options
  ::

    -class
    -compiler
    -gc
    -gccapacity
    -gccause
    -gcmetacapacity
    -gcnew
    -gcnewcapacity
    -gcold
    -gcoldcapacity
    -gcutil
    -printcompilation

jstat -class <pid>
  显示加载class的数量，及所占空间等信息。::

    Loaded  Bytes  Unloaded  Bytes     Time
     15247 28498.8        0     0.0      10.20

  分别是：装载的类的数量、装载类所占用的字节数、卸载类的数量、卸载类的字节数、装载和卸载类所花费的时间

  ============= =============================================
  显示列名      具体描述
  ============= =============================================
  Loaded        装载的类的数量
  Bytes         装载类所占用的字节数
  Unloaded      卸载类的数量
  Bytes         卸载类的字节数
  Time          装载和卸载类所花费的时间
  ============= =============================================

jstat -compiler <pid>
  显示VM实时编译的数量等信息。::

    Compiled Failed Invalid   Time   FailedType FailedMethod
        6513      1       0    33.88          1 org/springframework/core/annotation/AnnotatedElementUtils searchWithFindSemantics

  ============= =============================================
  显示列名      具体描述
  ============= =============================================
  Compiled      编译任务执行数量
  Failed        编译任务执行失败数量
  Invalid       编译任务执行失效数量
  Time          编译任务消耗时间
  FailedType    最后一个编译失败任务的类型
  FailedMethod  最后一个编译失败任务所在的类及方法
  ============= =============================================

jstat -printcompilation <pid>
  当前VM执行的信息。 ::

    Compiled  Size  Type Method
        6691     64    1 java/io/BufferedOutputStream write

  ============= ==========================================================================================================================================
  显示列名      具体描述
  ============= ==========================================================================================================================================
  Compiled      编译任务的数目
  Size          方法生成的字节码的大小
  Type          编译类型
  Method        类名和方法名用来标识编译的方法。类名使用/做为一个命名空间分隔符。方法名是给定类中的方法。上述格式是由-XX:+PrintComplation选项进行设置的
  ============= ==========================================================================================================================================

jstat -gc <pid>
  可以显示gc的信息，查看gc的次数，及时间。 ::

     S0C    S1C    S0U    S1U      EC       EU        OC         OU       MC      MU      CCSC    CCSU      YGC     YGCT    FGC    FGCT    CGC    CGCT     GCT
     0.0   15360.0  0.0   15360.0 128000.0 89088.0   84992.0    30822.5   77824.0 75373.6 11008.0 10033.5   14      0.273   0      0.000   8      0.038    0.311

  ============= ===========================================================
  显示列名      具体描述
  ============= ===========================================================
  S0C           年轻代中第一个survivor（幸存区）的容量 (字节)
  S1C           年轻代中第二个survivor（幸存区）的容量 (字节)
  S0U           年轻代中第一个survivor（幸存区）目前已使用空间 (字节)
  S1U           年轻代中第二个survivor（幸存区）目前已使用空间 (字节)
  EC            年轻代中Eden（伊甸园）的容量 (字节)
  EU            年轻代中Eden（伊甸园）目前已使用空间 (字节)
  OC            Old代的容量 (字节)
  OU            Old代目前已使用空间 (字节)
  PC            Perm(持久代)的容量 (字节)
  PU            Perm(持久代)目前已使用空间 (字节)
  YGC           从应用程序启动到采样时年轻代中gc次数
  YGCT          从应用程序启动到采样时年轻代中gc所用时间(s)
  FGC           从应用程序启动到采样时old代(全gc)gc次数
  FGCT          从应用程序启动到采样时old代(全gc)gc所用时间(s)
  GCT           从应用程序启动到采样时gc用的总时间(s)
  ============= ===========================================================

jstat -gccapacity <pid>
  可以显示，VM内存中三代（young,old,perm）对象的使用和占用大小

  ============= ===========================================================
  显示列名      具体描述
  ============= ===========================================================
  NGCMN         年轻代(young)中初始化(最小)的大小(字节)
  NGCMX         年轻代(young)的最大容量 (字节)
  NGC           年轻代(young)中当前的容量 (字节)
  S0C           年轻代中第一个survivor（幸存区）的容量 (字节)
  S1C           年轻代中第二个survivor（幸存区）的容量 (字节)
  EC            年轻代中Eden（伊甸园）的容量 (字节)
  OGCMN         old代中初始化(最小)的大小 (字节)
  OGCMX         old代的最大容量(字节)
  OGC           old代当前新生成的容量 (字节)
  OC            Old代的容量 (字节)
  PGCMN         perm代中初始化(最小)的大小 (字节)
  PGCMX         perm代的最大容量 (字节)  PGC    perm代当前新生成的容量 (字节)
  PC            Perm(持久代)的容量 (字节)
  YGC           从应用程序启动到采样时年轻代中gc次数
  FGC           从应用程序启动到采样时old代(全gc)gc次数
  ============= ===========================================================

jstat -gcutil <pid>
  统计gc信息

  ============= ===========================================================
  显示列名      具体描述
  ============= ===========================================================
  S0            年轻代中第一个survivor（幸存区）已使用的占当前容量百分比
  S1            年轻代中第二个survivor（幸存区）已使用的占当前容量百分比
  E             年轻代中Eden（伊甸园）已使用的占当前容量百分比
  O             old代已使用的占当前容量百分比
  P             perm代已使用的占当前容量百分比
  YGC           从应用程序启动到采样时年轻代中gc次数
  YGCT          从应用程序启动到采样时年轻代中gc所用时间(s)
  FGC           从应用程序启动到采样时old代(全gc)gc次数
  FGCT          从应用程序启动到采样时old代(全gc)gc所用时间(s)
  GCT           从应用程序启动到采样时gc用的总时间(s)
  ============= ===========================================================

jstat -gcnew <pid>
  年轻代对象的信息。

  ============= ===========================================================
  显示列名      具体描述
  ============= ===========================================================
  S0C           年轻代中第一个survivor（幸存区）的容量 (字节)
  S1C           年轻代中第二个survivor（幸存区）的容量 (字节)
  S0U           年轻代中第一个survivor（幸存区）目前已使用空间 (字节)
  S1U           年轻代中第二个survivor（幸存区）目前已使用空间 (字节)
  TT            持有次数限制
  MTT           最大持有次数限制
  EC            年轻代中Eden（伊甸园）的容量 (字节)
  EU            年轻代中Eden（伊甸园）目前已使用空间 (字节)
  YGC           从应用程序启动到采样时年轻代中gc次数
  YGCT          从应用程序启动到采样时年轻代中gc所用时间(s)
  ============= ===========================================================

jstat -gcnewcapacity<pid>
  年轻代对象的信息及其占用量。

  ============= ===========================================================
  显示列名      具体描述
  ============= ===========================================================
  NGCMN         年轻代(young)中初始化(最小)的大小(字节)
  NGCMX         年轻代(young)的最大容量 (字节)
  NGC           年轻代(young)中当前的容量 (字节)
  S0CMX         年轻代中第一个survivor（幸存区）的最大容量 (字节)
  S0C           年轻代中第一个survivor（幸存区）的容量 (字节)
  S1CMX         年轻代中第二个survivor（幸存区）的最大容量 (字节)
  S1C           年轻代中第二个survivor（幸存区）的容量 (字节)
  ECMX          年轻代中Eden（伊甸园）的最大容量 (字节)
  EC            年轻代中Eden（伊甸园）的容量 (字节)
  YGC           从应用程序启动到采样时年轻代中gc次数
  FGC           从应用程序启动到采样时old代(全gc)gc次数
  ============= ===========================================================

jstat -gcold <pid>
  old代对象的信息。

  ============= ===========================================================
  显示列名      具体描述
  ============= ===========================================================
  PC            Perm(持久代)的容量 (字节)
  PU            Perm(持久代)目前已使用空间 (字节)
  OC            Old代的容量 (字节)
  OU            Old代目前已使用空间 (字节)
  YGC           从应用程序启动到采样时年轻代中gc次数
  FGC           从应用程序启动到采样时old代(全gc)gc次数
  FGCT          从应用程序启动到采样时old代(全gc)gc所用时间(s)
  GCT           从应用程序启动到采样时gc用的总时间(s)
  ============= ===========================================================

stat -gcoldcapacity <pid>
  old代对象的信息及其占用量。

  ============= ===========================================================
  显示列名      具体描述
  ============= ===========================================================
  OGCMN         old代中初始化(最小)的大小 (字节)
  OGCMX         old代的最大容量(字节)
  OGC           old代当前新生成的容量 (字节)
  OC            Old代的容量 (字节)
  YGC           从应用程序启动到采样时年轻代中gc次数
  FGC           从应用程序启动到采样时old代(全gc)gc次数
  FGCT          从应用程序启动到采样时old代(全gc)gc所用时间(s)
  GCT           从应用程序启动到采样时gc用的总时间(s)
  ============= ===========================================================

jstat -gcpermcapacity<pid>
  perm对象的信息及其占用量。

  ============= ===========================================================
  显示列名      具体描述
  ============= ===========================================================
  PGCMN         perm代中初始化(最小)的大小 (字节)
  PGCMX         perm代的最大容量 (字节)  
  PGC           perm代当前新生成的容量 (字节)
  PC            Perm(持久代)的容量 (字节)
  YGC           从应用程序启动到采样时年轻代中gc次数
  FGC           从应用程序启动到采样时old代(全gc)gc次数
  FGCT          从应用程序启动到采样时old代(全gc)gc所用时间(s)
  GCT           从应用程序启动到采样时gc用的总时间(s)
  ============= ===========================================================

此外，如果使用这样的命令 ``jstat -printcompilation <pid> <interval> <count>`` ，则表示 ``stat`` 命令会保持监听 ``<pid>`` 程序，且每个 ``<interval>`` 秒（或毫秒）打印一次，总共打印 ``<count>`` 次。具体查看 ``jstat -h`` 。

jmap
::::::::::::::::
监视进程运行中的jvm物理内存的占用情况，该进程内存内，所有对象的情况，例如产生了哪些对象，对象数量；

系统崩溃了？jmap 可以从core文件或进程中获得内存的具体匹配情况，包括Heap size, Perm size等等

jmap -histo <pid>
  查看某个Java进程中每个类有多少个实例，占用多少内存。

  ============ =================================
  显示列名     具体描述
  ============ =================================
  num          序号，无实际意义
  instances    实例数量
  bytes        对象实例占用总内存数，单位：字节
  class name   对象实例名称
  ============ =================================

  *最后一行* 会显示 *总实例数量* 与 *总内存占用数*

jmap -dump:format=b,file=文件名 [pid]
  导出整个JVM 中内存信息

  该输出文件可以使用 ``jhat -J-Xmx1024M [file]`` 进行解读，然后在浏览器中使用网址： ``<IP>:7000`` 进行查看。

jmap -histo:live <pid>
  使用jmap工具实现，手工触发fullGC，运维常备

jinfo
::::::::::::::::
观察进程运行环境参数，包括Java System属性和JVM命令行参数

系统崩溃了？jinfo可以从core文件里面知道崩溃的Java应用程序的配置信息。

可视化工具
^^^^^^^^^^^^^^^^^^
jprofile
::::::::::::::::
待续

Jconsole
::::::::::::::::
待续

辅助命令
^^^^^^^^^^^^^^^^^^^
指定端口号
  ``java -jar xxx.jar --server.port=8080``

获得程序的pid
  ``pidof java`` 、 ``pidof java -s``
