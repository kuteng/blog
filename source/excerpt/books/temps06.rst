读书笔记 2018-10-05
=====================================

.. toctree::
   :maxdepth: 1

时间：10月05日

书籍：《精通Hadoop》

章节：YARN

进度：116页 - 141页

----------

备忘
^^^^^^^^^^^^^^^^^^^^^^^
- **YARN** 负责集群的资源管理和应用调度，它不知道正在运行的应用的类型，也不知道应用的任何内部信息。
- **RM** 在资源紧张的情况下，可以强制释放部分容器。不过再次之前 **RM** 会与 **AM** 进行一次通信，让 **AM** 自行释放或即使备份数据与工作状态，如果 **AM** 没有反馈，则一段时间后（给 **AM** 备份数据与状态的时间）强制执行。
- ``AMRMClient`` 和 ``NMClient`` 类都有对应的异步版本。
- ``AMRMClient`` 类中的allocate方法指示RM去分配容器，它同时也是发给RM的一个心跳信息。
- 容器的启动，是 **AM** 直接通知 **NM** 的。
- 应该避免并行的资源分配请求调用，因为这样可能导致请求丢失。

YARN 的架构
^^^^^^^^^^^^^^^^^^^^^^^
YARN的集群架构图 |yarn_structure_chart|

  .. |yarn_structure_chart| image:: /images/except_books_mastering_yarn_structure_chart.png
     :width: 100%

这个集群中的模块主要由五种类型构成： **资源管理器** （ **RM** ）、 **节点管理器** （ **NM** ）、 **Application Master** （ **AM** ）、 **容器** （container）、 **客户端** （client）。

**资源管理器** 又由 **调度器** （scheduler）和 **应用管理器** （ApplicationsManager）两个主要组件构成。

**调度器** 负责为集群中执行的各种应用分配资源，而且它执行纯粹的分配资源功能，不会关注应用内部状态相关的任何信息。在应用失败或者硬件故障的时候， **调度器** 不保证能够重启应用。调度是基于RM所了解的集群的全局状态进行的，分配资源的过程中使用了配置的队列和容量参数信息。调度的策略可以作为插件参与到调度器中，流行有 **容量调度器** 和 **公平调度器** 。

**应用管理器** 负责处理客户端提交的应用，并根据应用的要求和AM协商所需要的容器，并启动应用程序。在应用失败的情况， **应用管理器** 还提供重启AM的服务。

备忘：RM的调度器获得请求后，基于心跳信息获得的集群状态为AM分配容器，然后将容器转交给AM。在集群资源不足的情况下， RM可能要求AM归还一些容器。如果等待超过一定时间后依然没有容器被释放， RM可以终止容器的运行。 RM请求AM释放资源可以看成是警告正在执行的AM保存关键数据和工作状态。

在 **YARN** 中， **资源管理** 使用了 **延时绑定** 的模式。容器的产生可能和AM的请求没有关系，而仅仅是与AM给出的一个租约（lease）绑定。

**AM** 本身也是运行在容器中的。

**NM** 是每个节点的守护进程，负责本地容器的管理，管理范围从认证到资源监控。

**容器启动上下文** （Container Launch Context， **CLC** ）记录被用于指定容器的配置信息，例如依赖、数据文件路径、环境变量等。 NM可以根据CLC中的配置信息启动容器。

终止容器的操作包括清理操作，例如删除容器生成的本地数据。

NM向应用提供日志聚合的服务。标准输出和错误日志会在应用完成的时候输出到HDFS上。NM也能通过配置添加插件式辅助服务（auxiliary service）。

YARN客户端负责为AM提交合适的 **CLC** 。 YARN客户端同时还负责AM的注册，并且可以自由提供其他服务给它的消费者。

实现YARN客户端
^^^^^^^^^^^^^^^^^^^^^^^
重要的接口、类

- ``ApplicationClientProtocol`` ： **Client** 与 **RM** 通信。
- ``YarnConfiguration`` ：获取Yarn的配置。
- ``YarnClient`` ：客户端操作，创建、启动等。它封装了 ``ApplicationClientProtocol`` 对象。可以通过本身的静态方法创建。重要方法： ``createYarnClient()``  、 ``init(YarnConfiguration conf)`` 、 ``start()`` 、 ``createApplication`` 、 ``submitApplication(ApplicationSubmissionContext)`` 、 ``getApplicationReport(ApplicationId id)`` 。
- ``YarnClientApplication`` ： `AM` 对象的操作。可以通过 ``YarnClient`` 对象创建。重要方法： ``getApplicationSubmissionContext()`` 。
- ``ContainerLaunchContext`` ： 存放 **容器的规格** ，为创建容器做准备。可以通过 ``Records.newRecord()`` 创建。他可以存放 **ACL** 、 **命令行** 、 **环境变量** 、 **本地资源** 、 **服务的可执行数据** 和 **安全令牌** ，它们通过 ``setXXX()`` 传入。如： ``setCommands(String comm)`` 、 ``setLocalResources(Collections resources)`` 、 ``setEnvironment(Map envs)`` 。
- ``ApplicationSubmissionsContext`` ：其对象用于Client向RM提交参数，包括：容器的规格参数、提交的队列、一个合适应用的名字和一个启动容器所需要的Resource对象。可以通过 ``YarnClientApplication`` 对象创建。重点方法： ``setAMContainerSpec(ContainerLaunchContext context)`` 、 ``setQueue(String queue)`` 、 ``setApplicationName(String name)`` 、 ``setResource(Resource)`` 、 ``getApplicationId()`` 。
- ``ApplicationReport`` ： 其对象是RM的反馈，用于查询AM的运行状态。可以通过 ``YarnClient`` 创建。重点方法： ``getYarnApplicationState()`` 、 ``getDiagnostics()`` 。
- ``YarnApplicationState`` ： 存储了 **AM** 的状态。可以通过 ``ApplicationReport`` 获得。

辅助接口、类

- ``Resource`` ：通过这个对象，制定容器的CPU、内存等要求。主要方法： ``setVirtualCores(int)`` 、 ``setMemory(int)`` 。
- ``Records`` ：可以初始化不同的类。重点方法： ``newRecord(Class cls)`` 这是一个静态工厂，初始化不同的类，比如： ``ContainerLaunchContext`` 、 ``Resource`` 。
- ``LocalResource`` ： 描述YarnClient的本地资源（不在Hadoop集群中的）。
- ``ApplicationId`` ： 由RM给予Client的，用于标记对应的 ``AM`` ，在Client对 ``AM`` 进行操作（如查询、终止）时会使用。

实现AM实例
^^^^^^^^^^^^^^^^^^^^^^^^
重要接口、类

- ``ApplicationMasterProtocol`` ： **AM** 与 **RM** 通信。
- ``ContainerManager`` ： **AM** 与 **NM** 通信。
- ``YarnConfiguration`` ：获取Yarn的配置。
- ``AMRMClient`` ： **AM** 与 **RM** 交互的对象，封住了 ``ApplicationMasterProtocol`` 。可以通过本身的静态方法创建。重点方法： ``createAMRMClient()`` 、 ``init(YarnConfiguration conf)`` 、 ``start()`` 、 ``registerApplicationMaster(String ip, int port, String name)`` 、 ``addContainerRequest(AMRMClient.ContainerRequest request)`` 、 ``allocate(float progress)`` 、 ``unregisterApplicationMaster(FinalApplicationStatus status, String str, String str)``` 。
- ``NMClient`` ： **AM** 与 **NM** 通信的对象，封装了 ``ContainerManager`` 。可以通过本身的静态方法创建。重点方法： ``createNMClient()`` 、 ``init(YarnConfiguration conf)`` 、 ``start()`` 、 ``startContainer(Container container, ContainerLaunchContext context)`` 、 ``stopContainer(Container container)`` 。
- ``AMRMClient.ContainerRequest`` ： 该对象将封装创建容器所以需要的信息，被部署到 **RM** ，从而进行资源分配工作。注意：这里只涉及资源（CPU、内存、节点），不包含启动容器室运行的命令、环境变量、数据等。
- ``AllocateResponse`` ： 该对象包含新分配的容器的信息、完整的容器列表以及集群相关的信息。它同时也标明在集群中这个应用剩余的可以使用的资源数量。该对象是 ``AMRMClient.allocate(float progress)`` 的返回值。重要方法： ``getAllocatedContainers()`` 、 ``getCompletedContainersStatuses()`` 。

辅助接口、类

- ``Priority`` ：用于设置容器的 **优先级** 。可以通过 ``Records.newRecord(Priority.class)`` 创建。
- ``Resource`` ：通过这个对象，制定容器的CPU、内存等要求。主要方法： ``setVirtualCores(int)`` 、 ``setMemory(int)`` 。
- ``ContainerLaunchContext`` ：它的作用与在 **Yarn客户端实现** 中一样。

YARN 中的作业调度
^^^^^^^^^^^^^^^^^^^^^^^^
容量调度器
  在一个共享集群中保证承诺给某个租户的资源配额。如果一个租户使用的资源少于他们要求的配额，那调度会允许该租户继续使用空闲的资源。

  **容量调度器** 的首要目标就是不允许单一的应用或用户贪婪地占用集群的资源。调度器强制且严格限制集群中租户对共享资源的使用。

  容量调度器基于 **队列** 进行调度。每个队列拥有一个管理员可配置的配额，同时一个队列可以指定它能占用资源的最大值（针对队列配额的弹性机制）。另外，每个队列也可以指定每个用户使用资源的限制值。

  一个队列的配额是弹性的，所以调度器可以将没有被使用的配额从一个队列转移到另一个队列。这种被重新分配的资源，在它原属的队列需要资源来满足配额的时候可以被召回。

  容量调度器有一系列安全特性。

  容量调度器是动态的，它的属性（例如队列定义和ACL）可以在运行时改变。运行时删除队列是不允许的，但是添加新队列却可以。

  管理员可以停止队列，从而阻止新的作业被提交到这个队列及其子队列上。已存在的作业可以继续运行，但已经没有了资源的优先使用权。一旦队列中的作业都被执行结束，管理员可以启动这些被停止的队列。

  要求使用更多资源的应用也可以在容量调度器中运行。只要作业使用的资源没有超出队列的配额，容量调度器就会基于资源进行作业调度

公平调度器
  公平调度器这个概念背后是对所有运行的应用一般提供相同的资源。公平调度器将应用组织到池（pool）或队列，然后在不同的池之间共享对资源的使用时间。调度器周期性地检查每个应用在集群中已经得到的计算时间以及在理想条件下它应该得到的总时间。

  应用按照时间差额（deficit）降序排序，下一个被调度的应用将是差额最大的那个。层次化的池也存在于公平调度之中。

  公平调度器中，有 **权重** 。

YARN 命令行
^^^^^^^^^^^^^^^^^^^^^^^^
用户命令
  ``jar`` 命令行用于运行一个用户构建的自定义JAR文件。如： ``yarn jar <jar file path> [main class name] [arguments…]``

  ``application`` 命令用于操作YARN中正在运行的应用。它有三个操作：显示集群中正在运行的应用；获取应用的状态；终结一个正在运行的应用。显示操作可以使用应用的状态和应用类型作过滤。如： ``yarn application -list [-appStates <state identifiers> | -appTypes <type identifiers>] | -status <application id> | -kill <application id>``

  ``node`` 命令行用于报告集群中节点的状态。它有两个操作：显示所有节点的状态和获取某个节点的状态。 list命令也可以过滤特定状态的节点。如： ``yarn node –list [-all | -states <state identifiers> | -status <node id>``

  ``logs`` 命令行用于输出已经完成的应用的日志。它有两个操作：输出某个用户的日志；基于容器标识和节点地址输出日志。应用ID是必需的参数。如： ``yarn logs –applicationId <application Id> -appOwner <appOwner> | (-nodeAddress <node address> & -containerId <container Id>)``

管理员命令
  使用 ``resourcemanager`` 、 ``nodemanager`` 和 ``proxyserver`` 参数启动对应的守护进程。如： ``yarn resourcemanager | nodemanager | proxyserver`` 。

  ``rmadmin`` 命令：操作RM。具体：

  - ``-refreshQueues`` ：更新所有队列的ACL、状态和调度器属性。
  - ``-refreshNodes`` ：更新RM中特定节点的信息。
  - ``-refreshUserToGroupMappings`` ：更新用户成员关系的所有映射。
  - ``-refreshSuperUserGroupsConfiguration`` ：更新超级用户相关的映射。
  - ``-refreshAdminAcls`` ：更新ACL，用以决定访问RM管理员的权限。
  - ``-refreshServiceAcl`` ：重新加载RM中的授权文件。

  ``daemonlog`` 命令：获取并设置YARN守护进程的日志级别。如： ``yarn [-getLevel <daemon host:port> <name>| -setLevel <daemon host:port> <name> <level>]``

疑问
^^^^^^^^^^^^^^^^^^^^^^^^
- 对于 ``AMRMClient.registerApplicationMaster`` 方法有疑问，它注册的IP和端口号，是 **RM** 的还是 **AM** 的？
- **公平调度器** 不了解。“调度器”能够暂停正在运行的应用吗？或者一个应用会向 **NM** 请求多次调度吗？如果一个应用的调度被押后，它的容器占用的资源（CPU、内存）会“借给”其他容器吗？
- 客户端可以与AM直接交互吗？
- ``AMRMClient.allocate(float progress)`` 方法的参数有何用？
- 在 ``135页`` ，介绍 **容量调度器** 的时候提到了“其他租户”。YARN中除了“已申请此资源配额的租户”外，还有其他租户吗？找出个例子来。
