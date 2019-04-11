《精通Java并发编程》
======================================

.. toctree::
   :maxdepth: 1

备忘
^^^^^^^^^^^^^^
- *设计并发算法的方法论* ：串行版本 -> 分析 -> 设计 -> 实现 -> 测试 -> 调整 。
- 在 *设计并行算法* 时，我们应重点关注 *那些执行过程花费时间最多或者执行代码较多的部分* 。
- 在 *设计并行算法* 时，我们可以优先考虑进行两种分解： **任务分解** 、 **数据分解** 。另外我们需要注意分解的 **粒度** 。
- 并非每一个算法都可以进行并行化处理，例如某些 *循环* 与 *递归* 是无法进行并发优化的。
- 并发优化是 **因需开展** 的。对性能良好的串行版算法实现并行处理，实际上是个糟糕的出发点。
- *并发优化* 的结果应该是： *高效* 、 *简单* 的。听说还有 *可移植性* ，但我没感觉到这一点有多重要。
- Java并发的基本类： ``Thread`` 、 ``Runable`` 、 ``ThreadLocal`` 、 ``ThreadFactory`` 

  - `Thread` 的 ``run()`` 不会另起线程， ``start()`` 才可以。
- **同步机制** ： *synchronized 关键字* 、 *Lock 接口* 、 *Semaphore 类* 、 *CountDownLatch 类* 、 *CyclicBarrier 类* 、 *Phaser 类*
- **执行器** ： *Executor 接口* 、 *ExecutorService 接口* 、 *ThreadPoolExecutor 类* 、 *ScheduledThreadPoolExecutor 类* 、 *Executors* 、 *Callable 接口* 、 *Future 接口* 。

  - *Executor 接口* ：只能接收一个 ``Runnable`` 对象。没有其他控制接口。
  - *ExecutorService 接口* ：借助执行器可返回任务的结果；通过单个方法调用执行一个任务列表；结束执行器的执行并且等待其终止。
  - *ThreadPoolExecutor 类* ：实现了 Executor 接口和 ExecutorService 接口；提供了获取执行器状态（工作线程的数量、已执行任务的数量等）的方法；确定执行器参数（工作线程的最小和最大数目、空闲线程等待新任务的时间等）的方法，以及支持编程人员扩展和调整其功能的方法。
  - *Executors 类* ：该类为创建 Executor 对象和其他相关类提供了实用方法。

- ``sleep()`` 方法没有释放锁，而 ``wait()`` 方法 **释放** 了锁，使得其他线程可以使用同步控制块或者方法。
- ``notify()`` 方法唤醒的对象（被 ``wait()`` 方法阻塞的）未必会立刻运行。因为他们可能正在被当前运行的线程占有资源。
- ``wait()`` 、 ``notify()`` 、 ``notifyAll()`` 必须在同步（Synchronized）方法/代码块中调用。
- *Fork/Join 框架* 定义了一种特殊的执行器，尤其针对采用分治方法进行求解的问题。提供了一种优化其执行的机制，为细粒度并行处理量身定制的。主要接口如下：

  - ``ForkJoinPool`` ：该类实现了要用于运行任务的执行器。
  - ``ForkJoinTask`` ：这是一个可以在 ForkJoinPool 类中执行的任务。
  - ``ForkJoinWorkerThread`` ：这是一个准备在 ForkJoinPool 类中执行任务的线程。

- **流** 流已经被增加为 Collection 接口和其他一些数据源的方法，它允许处理某一数据结构的所有元素、生成新的结构、筛选数据和使用 MapReduce 方法来实现算法。
- *并行流* 是一种特殊的流，它以一种并行方式实现其操作。使用并行流时涉及的最重要的元素如下：

  - Stream 接口：该接口定义了所有可以在一个流上实施的操作。
  - Optional：这是一个容器对象，可能（也可能不）包含一个非空值。
  - Collectors：该类实现了约简（reduction）操作，而该操作可作为流操作序列的一部分使用。
  - lambda 表达式：流被认为是可以处理 lambda 表达式的。大多数流方法都会接收一个 lambda 表达式作为参数，这让你可以实现更为紧凑的操作。

- *并发数据结构* 

  - **原子变量** ： ``AtomicBoolean`` 、 ``AtomicInteger`` 、 ``AtomicLong`` 、 ``AtomicReference`` 、 ``LongAdder`` 、 ``DoubleAdder`` 、 ``ConcurrentHashMap`` 。
  - *非阻塞型* ： ``ConcurrentLinkedDeque`` 、 ``ConcurrentLinkedQueue`` 、 ``ConcurrentSkipListMap`` 、 ``ConcurrentHashMap``

     ``add()`` 、 ``addFirst()`` 、 ``addLast()`` ：这些方法将一个元素插入数据结构。如果该数据结构已满，则会抛出一个 IllegalStateException 异常。

     ``remove()`` 、 ``removeFirst()`` 、 ``removeLast()`` ：这些方法将返回并且删除数据结构中的一个元素。如果该结构为空，则这些方法将抛出一个 IllegalStateException 异常。

     ``element()`` 、 ``getFirst()`` 、 ``getLast()`` ：这些方法将返回但是不删除数据结构中的一个元素。如果该数据结构为空，则会抛出一个 IllegalStateException 异常。

     ``offer()`` 、 ``offerFirst()`` 、 ``offerLast()`` ：这些方法可以将一个元素插入数据结构。如果该结构已满，则返回一个 Boolean 值 false。

     ``poll()`` 、 ``pollFirst()`` 、 ``pollLast()`` ：这些方法将返回并且删除数据结构中的一个元素。如果该结构为空，则返回 null 值。

     ``peek()`` 、 ``peekFirst()`` 、 ``peekLast()`` ：这些方法返回但是并不删除数据结构中的一个元素。如果该数据结构为空，则返回 null 值。

  - *阻塞型* ： ``LinkedBlockingDeque`` 、 ``LinkedBlockingQueue`` 、 ``PriorityBlockingQueue``

    ``put()`` 、 ``putFirst()`` 、 ``putLast()`` ：这些方法将一个元素插入数据结构。如果该数据结构已满，则会阻塞该线程，直到出现空间为止。

    ``take()`` 、 ``takeFirst()`` 、 ``takeLast()`` ：这些方法返回并且删除数据结构中的一个元素。如果该数据结构为空，则会阻塞该线程直到其中有元素为止。

- 设计并发算法中的一些注意事项与技巧：

  - 正确识别独立任务
  - 在尽可能高的层面上实施并发处理
  - 考虑伸缩性：如不同机器核心数不同时
  - 使用线程安全 API
  - 绝不要假定执行顺序
  - 在静态和共享场合尽可能使用局部线程变量
  - 寻找更易于并行处理的算法版本：如排序算法的几个版本的选择
  - 尽可能使用不可变对象
  - 通过对锁排序来避免死锁
  - 使用原子变量代替同步
  - 占有锁的时间尽可能短
  - 谨慎使用延迟初始化
  - 避免在临界段中使用阻塞操作：除非必要，否则不要在临界段中加入阻塞操作。

- 关于 *Optional* 类型数据的用法备忘：

  - 没有 ``isPresent()`` 作铺垫的 ``get()`` 调用是不被推荐的。在 *获取* 之前，我们应该检验一下它是否 *可用* 。
  - 不推荐把 ``Optional`` 类型用作属性或是方法参数。 ``Optional`` 只设计为类库方法的, 可明确表示可能无值情况下的返回类型. ``Optional`` 类型不可被序列化, 用作字段类型会出问题的。
  - ``Optional.of(obj)`` : 它要求传入的 obj 不能是 ``Null`` 值的, 否则还没开始进入角色就倒在了 ``NullPointerException`` 异常上了.
  - 这里有三个比较相近的方法： ``orElse()`` 、 ``orElseGet()`` 、 ``orElseThrow()`` 。
  - ``filter()`` 把不符合条件的值变为 ``empty()`` ,   ``flatMap()`` 总是与 ``map()`` 方法成对的,

  代码示例：

  .. code-block:: java

    // 如果userOptional可用，在打印输出。
    userOptional.ifPresent(System.out::println);

    // 当 user.isPresent() 为真, 获得它关联的 orders , 为假则返回一个空集合时
    return userOptional.map(u -> u.getOrders()).orElse(Collections.emptyList())

    // map()方法可以无限级联下去
    // 当 user.isPresent() 为真, 获得用户名大写形式；否则获得空对象。
    return user.map(u -> u.getUsername())
               .map(name -> name.toUpperCase())
                          .orElse(null);

- 数组的并行排序方法 ``Arrays.parallelSort()`` 。
- ``Semaphore`` 、 ``CountDownLatch`` 、 ``CyclicBarrier`` 三个同步控制类的区别：

  - ``Semaphore`` 有点像锁，它一般用于控制对某租资源的访问权限。与后两个类很好区分。
  - ``CountDownLatch`` 一般用于某个线程等待若干个线程执行完毕后，再执行。同时它不能被重用。
  - ``CyclicBarrier`` 一般用于一组线程互相等待至某个状态，然后这组线程再同时执行。

- 每个 Java 应用程序都有一个默认的 ``ForkJoinPool`` ，称作 **公用池** 。可以通过调用静态方法 ``ForkJoinPool.commonPool()`` 获得这样的公用池，而不需要采用显式方法创建（尽管可以这样做）。
- **Fork/Join框架**

  - 该框架有一个关键特性，即工作窃取算法。该算法确定要执行的任务。当一个任务使用 join()方法等待某个子任务结束时，执行该任务的线程将会从任务池中选取另一个等待执行的任务并且开始执行。通过这种方式， Fork/Join 执行器的线程总是通过改进应用程序的性能来执行任务。
  - Java 8在 *Fork/Join框架* 中提供了一种新特性。现在，每个 Java应用程序都有一个默认的 ForkJoinPool，称作 **公用池** 。可以通过调用静态方法 ``ForkJoinPool.commonPool()`` 获得这样的公用池，而不需要采用显式方法创建（尽管可以这样做）。这种默认的 Fork/Join 执行器会自动使用由计算机的可用处理器确定的线程数。
  - Arrays类中的 ``parallelSort()`` 方法以及 **并行流** 都是使用此框架实现的。
  - ``fork()`` 方法：可以将本任务发送给 Fork/Join 执行器。
  - ``invoke()`` 方法：可以将本任务发送给 Fork/Join 执行器，并等待任务执行结束。
  - ``invokeAll(...)`` 方法：可以将多个子任务发送给 Fork/Join 执行器，并等待任务执行结束。
  - ``join()`` 方法：可以等待本任务执行结束后返回其结果。
  - 不再进行细分的基本问题的规模既不能过大也不能过小。按照 Java API 文档的说明，该基本问题的规模应该介于 100 到 10 000 个基本计算步骤之间。
  - 数据可用前，不应使用阻塞型 I/O 操作，例如读取用户输入或者来自网络套接字的数据。这样的操作将导致 CPU 核资源空闲，降低并行处理等级，进而使性能无法达到最佳。
  - 不能在任务内部抛出校验异常，必须编写代码来处理异常（例如，陷入未经校验的RuntimeException）。对于未校验异常有一种特殊的处理方式。
  - 该类任务的入口可以通过 *Pool* 完成，如 ``ForkJoinPool.commonPool().execute(task)`` 等。 **注意** 不要忘记通过 ``task.join()`` 或 ``task.quietlyJoin()`` 来等待任务执行结束。
  - ``join()`` 方法和 ``quietlyJoin()`` 方法之间的区别在于， ``join()`` 启动之后，如果任务撤销 或 在方法内部抛出一个未校验异常时，将抛出异常，而 ``quietlyJoin()`` 方法则不抛出任何异常。同时后者没有返回值，如果希望得到返回值，还需要再调用前者。类似区别的还有 ``invoke()`` 方法和 ``quietlyInvoke()`` 方法。

    注意：对于这里提到的 **未校验异常** 即使我们在中途的 *Task* 中进行了捕获，它也依旧会传播到 **父Task** 和 **根Task** 。

  - 该类任务分发的方式可以通过 ``invokeAll(...)`` 或 ``fork()`` 。
  - **思考一下** 如果一批 *Fork/Join* 的任务因为某个任务有结果或抛出异常，我们希望其他未执行的任务或正在执行的任务都 **结束** ，应该怎么办？如果同一个 ``ForkJoinPool`` 中可能同时运行两批 *Fork/Join* 任务，前面的问题又如何解决？找一个容器，存放并管理所有放入 *Pool* 的任务吗？

- **Fork/Join 框架** 的组成

  - ``ForkJoinPool`` 类：该类实现了 ``Executor`` 接口和 ``ExecutorService`` 接口，而执行 ``Fork/Join`` 任务时将用到 `Executor` 接口。 Java 提供了一个默认的 ``ForkJoinPool`` 对象（称作公用池），但是如果需要，你还可以创建一些构造函数。你可以指定并行处理的等级（运行并行线程的最大数目）。默认情况下，它将可用处理器的数目作为并发处理等级。

    **注意** ： ``execute()`` 方法、 ``invoke()`` 方法 和 ``submit()`` 方法都可以将 `Task` 发送给线程池，但是 ``execute()`` 没有返回值； ``invoke()`` 会等待任务结束并给出返回值；而 ``submit()`` 不会等待任务结束，而是立即返回一个 ``Future`` 对象。
  - ``ForkJoinTask`` 类：这是所有 *Fork/Join* 任务的基本抽象类。该类是一个抽象类，提供了 ``fork()`` 方法和 ``join()`` 方法，以及这些方法的一些变体。该类还实现了 ``Future`` 接口，提供了一些方法来判断任务是否以正常方式结束，它是否被撤销，或者是否抛出了一个未校验异常。 ``RecursiveTask`` 类、 ``RecursiveAction`` 类和 ``CountedCompleter`` 类提供了 ``compute()`` 抽象方法。为了执行实际的计算任务，该方法应该在子类中实现。

    注意： ``ForkJoinTask`` 可没有 ``compute()`` 方法。所以理论上，他应该不能被直接 *实现* ，或者我们应该仿照 ``RecursiveTask`` 等类去 *实现* 。

  - ``RecursiveTask`` 类：该类扩展了 ``ForkJoinTask`` 类。 ``RecursiveTask`` 也是一个抽象类，而且应该作为实现 **返回结果** 的 *Fork/Join* 任务的起点。
  - ``RecursiveAction`` 类：该类扩展了 ``ForkJoinTask`` 类。 ``RecursiveAction`` 类也是一个抽象类，而且应该作为实现 **不返回结果** 的 *Fork/Join* 任务的起点。
  - ``CountedCompleter`` 类：该类扩展了 ``ForkJoinTask`` 类。 ``CountedCompleter`` 类也是一个抽象类，而且应该作为实现 **任务完成时触发另一任务** 的 *Fork/Join* 任务的起点。 *完成任务后触发另一个任务* 是通过重写方法 ``tryComplete()`` 、 ``onCompletion()`` 、 ``onExceptionalCompletion(...)`` 实现的，个人推荐重写 ``onCompletion()`` 方法。


代码
^^^^^^^^^^^^^^^^^
- ``ThreadLocal`` 是使用 ``ThreadLocal.ThreadLocalMap.Entry`` 存储数据的，而它继承自 ``WeakReference`` 。在内存紧张的情况下，这些保存的数据还安全吗？

  注意垃圾回收中的描述： **只被** 弱引用引用的数据，每次垃圾回收才会被 *必定* 回收。

- ``Thread`` ：

  - ``getId()``
  - ``getName()``
  - ``setName()``
  - ``getPriority()``
  - ``setPriority()``
  - ``isDaemon()``
  - ``setDaemon()``
  - ``getState()``
  - ``interrupt()`` ：中断线程，但如果线程中没有sleep 、wait、Condition、定时锁等应用, interrupt()方法是 **无法中断** 当前的线程的。
  - ``interrupted()`` ：静态方法。检测当前线程是否被中断，并且中断状态会被清除（即重置为false）。
  - ``isInterrupted()`` ：检测调用该方法的线程是否被中断，中断状态不会被清除。线程一旦被中断，该方法返回true，而一旦sleep等方法抛出异常，它将清除中断状态，此时方法将返回false。
  - ``sleep()``
  - ``join()``
  - ``currentThread()``
  - ``setUncaughtExceptionHandler()``

- ``Callable`` 接口。与 ``Runnable`` 类似，却有如下主要特征：

  - 它有一个 *泛型参数* ，与 ``call()`` 方法的返回类型相对应。
  - 它声明了 ``call()`` 方法。执行器运行任务时，该方法会被执行器执行。它必须返回声明中指定类型的对象。
  - ``call()`` 方法可以抛出任何一种校验异常。你可以实现自己的执行器并重载 ``afterExecute()`` 方法来处理这些异常。

- ``Future`` ： ``AbstractExecutorService.submit(...)`` 的返回值，可追踪/操作已放入执行器的任务。

  当你向执行器发送一个 ``Callable`` 任务时，它将为你返回一个 ``Future`` 接口的实现，这允许你控制任务的执行和任务状态，使你能够获取结果。

  - ``get(...)`` 获取任务返回的值。注意不是任务实体。
  - ``isDone()`` 检验任务是否完成。
  - ``isCanneled()`` 检验任务是否被撤销。
  - ``cannel(...)`` ：撤销任务。方法的参数标识如果任务正在执行，是否进行 *中断* 以便执行撤销操作。但是如下场景下， **任务无法撤销** ：

    - 任务已经被撤销。
    - 任务已经完成了执行。
    - 任务正在执行而提供给 ``cancel()`` 方法的参数为 false。
    - 在 API 文档中并未说明的其他原因。

- ``ThreadPoolExecutor`` 

  - ``execute()`` 添加任务，没有返回值。
  - ``submit()`` 添加任务，有返回值，也可追踪/操作任务（如撤销该任务）。
  - ``invokeAll()`` 触发执行任务列表，返回的结果顺序也与任务在任务列表中的顺序一致.所有线程执行完任务后才返回结果。如果设置了超时时间，未超时完成则正常返回结果，如果超时未完成则报异常。
  - ``invokeAny()`` 将第一个得到的结果作为返回值，然后立刻终止所有的线程。如果设置了超时时间，未超时完成则正常返回结果，如果超时未完成则报超时异常。 **注意** ： ``Null`` 也会作为结果返回，所以可以考虑 *阻塞* 、 *中断* 它。
  - ``shutdown()`` 关闭执行器。是 **非阻塞的** 。将线程池状态置为 ``SHUTDOWN`` ,并不会立即停止：停止接收外部submit的任务；内部正在跑的任务和队列里等待的任务，会执行完；等到第二步完成后，才真正关闭执行器。
  - ``shutdownNow()`` 关闭执行器。是 **非阻塞的** 。将线程池状态置为STOP。企图立即停止，事实上不一定：停止接收外部submit的任务；忽略队列里等待的任务；尝试将正在跑的任务interrupt中断；关闭执行器并返回未执行的任务列表；
  - ``awaitTermination(long timeOut, TimeUnit unit)`` 返回结果是执行器是否已停止工作（或者说线程池是否已停止）。它是 **阻塞的** 。当前线程阻塞，直到等所有已提交的任务（包括正在跑的和队列中等待的）执行完；或超时时间到了；或线程被中断，抛出InterruptedException。

    **注意** ：需要在 ``shutdown()`` 之后使用。

  - ``getActiveCount()``
  - ``getMaximumPoolSize()``
  - ``getCorePoolSize()``
  - ``getPoolSize()``
  - ``getLargestPoolSize()``
  - ``getCompletedTaskCount()``
  - ``getTaskCount()`` 计较过的所有任务数，已完成的 + 未完成的 + 正在进行的。
  - ``getQueue()``

- ``ScheduledThreadPoolExeuctor`` ：它支持预定任务的执行。这里我们可以进行如下操作:

  - 在某段延迟之后执行某项任务。
  - 周期性地执行某项任务，包括以固定速率执行任务或者以固定延迟执行任务。

  它的 ``submit()`` 方法的返回类型是 ``ScheduledFuture`` ，但一般自定义该执行器时，需要定义的 `Future` 是集成 ``RunnableScheduledFuture`` 它是 ``ScheduledFuture`` 的子接口。

  一些方法：

  - ``execute()`` 与 ``ThreadPoolExecutor`` 里的方法一样，它是 **零延迟** 执行任务的。
  - ``submit()`` 与 ``ThreadPoolExecutor`` 里的方法一样，它是 **零延迟** 执行任务的。
  - ``schedule(...)`` 在给定延迟之后执行某个任务，且该任务仅执行一次。
  - ``scheduleAtFixedRate(...)`` 给定周期执行一个周期性任务。
  - ``scheduleWithFixedDelay(...)`` 从当前任务结束的时刻开始结算间隔时间。举例说明：如0秒开始执行第一次任务，任务耗时5秒，任务间隔时间3秒，那么第二次任务执行的时间是在第8秒开始。

- ``ScheduledFuture`` 的对象有一个独特的方法：

  - ``getDelay()``

- ``RunnableScheduledFuture`` 的对象有一个独特的方法：

  - ``getDelay()``
  - ``isPeriodic()``

- ``Executors``

  - ``newFixedThreadPool()`` 创建一个定长线程池，可控制线程最大并发数，超出的线程会在队列中等待。
  - ``newCachedThreadPool()`` 创建一个可缓存线程池，如果线程池长度超过处理需要，可灵活回收空闲线程，若无可回收，则新建线程。
  - ``newScheduledThreadPool()`` 创建一个定长线程池，支持定时及周期性任务执行。
  - ``newSingleThreadExecutor()`` 只有一个 *工作线程* 。 创建一个单线程化的线程池，它只会用唯一的工作线程来执行任务，保证所有任务按照指定顺序(FIFO, LIFO, 优先级)执行。

- ``ThreadPoolExecutor`` 对象常用的创建方法是： ::

    ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(poolSize);

- ``CountDownLatch`` 作为计数器，可用于记录 **未完成的进程数量** 。常用于：等待N个任务全部执行完毕，再进行后续操作。常用方法是：

  - ``countDown()`` ， *减一* 操作。
  - ``await()`` ，等待计数器为0。
  - ``getCount()``

- ``CyclicBarrier`` 回环栏栅，可以实现让一组线程等待某一状态之后，再全部同时执行。

  叫做回环是因为当所有等待线程都被释放以后，CyclicBarrier可以被 **重用** 。（而 ``CountDownLatch`` 不能被直接重用）

  构造方法中需要定义 *一组线程的个数* ，同时还可以定义 *全部到达状态后需要执行的动作* （为 ``Runnable`` 类型）。

  - ``await(...)`` 。线程代码从此处阻塞，一直等到N个线程都运行到此处，才能解除 *阻塞状态* 继续运行下去。 **注意** 在解除阻塞之前，能要要借助某个线程运行一段 ``Runnable`` 代码。

- ``Semaphore`` 信号量，可以控同时访问的线程个数，通过 ``acquire()`` 获取一个许可，如果没有就等待，而 ``release()`` 释放一个许可。构造方法中，需要定义 *许可总数* ，另外还可以定义 *是否公平* （及等待时间越长，优先级越高）。

  - ``acquire(...)`` 请求获取 `N` 个许可，默认 `1` 个。
  - ``release(...)`` 释放 `N` 个许可，默认 `1` 个。
  - ``tryAcquire(...)`` 尝试 **立刻** 立刻获得 `N` 个许可，如果成功则返回 ``true`` ，如果没有成功（或等待 *规定* 时间后没有成功），则返回 ``false`` 。

- ``Phaser`` 一种 **同步机制** ：用于控制以并发方式划分为 **多个阶段** 的算法的执行。关键方法如下。

  - *构造方法* ，需要提供一个参数，说明该分段器参与者个数。
  - ``register()`` 注册一个参与者。
  - ``bulkRegister(int)`` 注册N的参与者。
  - ``arrive()`` 任务使用该方法向分段器通报，表明它已经完成了当前阶段并且要继续下一阶段。注意： **不会阻塞** 。
  - ``arriveAndAwaitAdvance()`` 任务使用该方法向分段器通报，表明它已经完成了当前阶段并且要继续下一阶段。分段器将 **阻塞** 该任务，直到所有参与的任务已调用其中一个同步方法。
  - ``awaitAdvance(int phase)`` 任务使用该方法向分段器通报，如果该方法参数中的数值和分段器的实际阶段数相等，就要等待当前阶段结束；如果这两个数值不相等，则该方法立即返回
  - ``onAdvance(...)`` 定义每个阶段完成后，需要做些什么；并定义分段器何时进入 **终止状态** （及返回 ``True`` ）。

    默认情况下，当所有参与者都注销后， ``onAdvance()`` 方法将返回 ``true`` 值。

  - ``getRegisteredParties()`` 该方法返回分段器中参与者的数目。
  - ``getPhase()`` 该方法返回当前阶段的编号。
  - ``getArrivedParties()`` 该方法返回已经完成当前阶段的参与者的数目。
  - ``getUnarrivedParties()`` 该方法返回尚未完成当前阶段的参与者的数目。
  - ``isTerminated()`` 如果分段器处于终止状态，则该方法返回 true 值，否则返回 false 值。

  **注意** 如果使用 ``Phaser`` 对象的线程数超过 **注册** 数，超出者也不会受到控制； ``onAdvance(...)`` 方法中，只要又一次返回 ``True`` ，那么后续的进程运行就不再受控制了。

- *执行器* (如 ``ThreadPoolExecutor`` 、 ``ScheduledThreadPoolExecutor`` )的一些常见重载方法：

  - ``beforeExecute()`` 该方法在执行器中的某一并发任务执行之前被调用。它接收将要执行的 `Runnable` 对象和将要执行这些对象的 `Thread` 对象。

    **注意** ：该方法接收的 `Runnable` 对象是 ``FutureTask`` 类的一个实例，而不是使用 `submit()` 方法发送给执行器的 `Runnable` 对象 。

  - ``afterExecute()`` 该方法在执行器中的某一并发任务执行之后被调用。它接收的是已执行的 `Runnable` 对象和一个 `Throwable` 对象，该 `Throwable` 对象存储了任务中可能抛出的异常。与 `beforeExecute()` 方法相同， `Runnable` 对象是 `FutureTask` 类的一个实例。
  - ``newTaskFor()`` 该方法创建的任务将执行 `submit()` 方法发送的 `Runnable` 对象。追踪源码发现，它是用 ``FutureTask`` 将传进来的 ``Runnable`` 包裹起来，这个新对象才是执行器执行的直接对象。

    该方法必须返回 `RunnableFuture` 接口的一个实现。默认情况下， Open JDK 9 和 Oracle JDK 9 返回 `FutureTask` 类的一个实例，但是这在今后的实现中可能会发生变化。
  - ``decorateTask()`` 该方法只有 ``ScheduledThreadPoolExecutor`` 才有，它与面向预定任务的 `newTaskFor()` 方法类似并且允许重载执行器所执行的任务。

  此外，执行器的一些重要元素也可以进行修改：

  - ``BlockingQueue<Runnable>`` ：每个执行器均使用一个内部的 `BlockingQueue` 存储等待执行的任务。可以将该接口的任何实现作为参数传递。例如，更改执行器执行任务的默认顺序。
  - ``ThreadFactory`` ：可以指定 `ThreadFactory` 接口的一个实现，而且执行器将使用该工厂创建执行该任务的线程。例如，你可以使用 ThreadFactory 接口创建 Thread 类的一个扩展类，保存有关任务执行时间的日志信息。
  - ``RejectedExecutionHandler`` ：调用 ``shutdown()`` 方法或者 ``shutdownNow()`` 方法之后，所有发送给执行器的任务都将被拒绝。可以指定 ``RejectedExecutionHandler`` 接口的一个实现管理这种情形。

- ``PriorityBlockingQueue`` 该类根据 compareTo()方法的执行结果对元素进行排序（因此其中存储的元素必须实现 Comparable 接口）。它可以用在执行器中，用于存储那些将在执行器中执行的任务，用它可以允许我们按照优先级执行任务。

编程经验
^^^^^^^^^^^^^^^^^^^
- ``ThreadPoolExecutor`` 与 ``Phaser`` 结合使用，如果 ``ThreadPoolExecutor`` 的线程数 **小于** ``Phaser`` 成员数，很可能会发生死锁，因为一部分成员（ ``Runnable`` ）永远无法加载到线程中。

概念解析
^^^^^^^^^^^^^^
- **并发** 与 *并行*
- **同步** ：是一种协调两个或更多任务以获得预期结果的机制。分为两种方式：控制同步和数据访问同步。
- 并发系统中有不同的同步机制： *信号量* 、 *监视器*
- **临界段** ：是一段代码，由于它可以访问共享资源，因此在任何给定时间内，只能够被一个任务执行。 *互斥* ：是用来保证这一要求的机制，而且可以采用不同的方式来实现。
- **互斥** 是一种特殊类型的信号量，它只能取两个值（即资源空闲和资源忙），而且只有将互斥设置为忙的那个进程才可以释放它。互斥可以通过保护临界段来帮助你避免出现竞争条件
- **线程安全** ：如果共享数据的所有用户都受到同步机制的保护，那么代码（或方法、对象）就是线程安全的。
- **CAS** ： `Compare And Swap` 。CAS算法的过程是这样：它包含三个参数 CAS（V,E,N）。V表示要更新的变量，E表示预期的值，N表示新值。仅当V值等于E值时，才会将V的值设置成N，否则什么都不做。最后CAS返回当前V的值。CAS算法需要你额外给出一个期望值，也就是你认为现在变量应该是什么样子，如果变量不是你想象的那样，那说明已经被别人修改过。你就重新读取，再次尝试修改即可。

  CAS算法是非阻塞的，它对死锁问题天生免疫，而且它比基于锁的方式拥有更优越的性能。

- **不可变对象** 是一种非常特殊的对象。在其初始化后，不能修改其可视状态（其属性值）。如果想修改一个不可变对象，那么你就必须创建一个新的对象。它的主要优点在于它是线程安全的。你可以在并发应用程序中使用它而不会出现任何问题。

  ``String`` 就是一个 *不可变对象* 。

- **原子操作** 是一种发生在瞬间的操作。在并发应用程序中，可以通过一个临界段来实现原子操作，以便对整个操作采用同步机制。

  原子变量是一种通过原子操作来设置和获取其值的变量。可以使用某种同步机制来实现一个原子变量，或者也可以使用CAS以无锁方式来实现一个原子变量，而这种方式并不需要任何同步机制。

- **通信** ：不同任务可能需要交换信息，这个过程我们称之为通信。这里有两种方法：

  - **共享内存** ：一台电脑
  - **消息传递** ：多台电脑

- **数据竞争** ，提款机例子。
- **死锁** ：描述、定义就 `略` 了，我们说一下避免死锁的机制（虽然我认为下面的四个都不是 `好东西` ）： *忽略他们* 、 *检测* 、 *预防* 、 *规避* 。
- **活锁** 
- **资源不足**
- **优先权反转** ：解决方法 *优先级继承* 和 *优先级天花板* 。
- **护航现象** ：是在多线程并发环境下由于锁的使用而引起的 *性能退化* 问题。
- **Coffman 条件** ： *互斥* 、 *占有并等待条件* 、 *不可剥夺* 、 *循环等待* 。如果他们同时同地出现，恭喜你，你遇见 *死锁* 了。
- 前面说的 *数据竞争* 、 *死锁* 、 *活锁* 、 *资源不足* 、 *优先权反转* 、 *护航问题* 都是并发编程中可能出现的问题。
- **加速比** （speedup）： *串行算法执行的时间* / *并行算法执行的时间*
- **Amdahl 定律**
- **Gustafson-Barsis 定律**
- **同步机制**
- **执行器** ，它有如下特征：

  - 不需要创建任何 Thread 对象。
  - 执行器通过重新使用线程来缩减线程创建带来的开销。
  - 使用执行器控制资源很容易。
  - 你必须以显式方式结束执行器的执行。

- **Fork/Join 框架** ：它必须用于解决基于分治方法的问题。必须将原始问题划分为较小的问题，直到问题很小，可以直接解决。
- **并行流**
- **并发数据结构** 又分为 **阻塞型数据结构** 和 **非阻塞型数据结构** 。
- **并发设计模式** ： **信号模式** 、 **会合模式** 、 **互斥模式** 、 **多元复用模式** 、 **栅栏模式** 、 **双重检查锁定模式** 、 **读写锁模式** 、 **线程池模式** 、 **线程局部存储模式** 。
- **守护线程** 是指在程序运行的时候在后台提供一种通用服务的线程，比如垃圾回收线程就是一个很称职的守护者，并且这种线程并不属于程序中不可或缺的部分。

  守护线程和用户线程的没啥本质的区别：唯一的不同之处就在于虚拟机的离开：如果用户线程已经全部退出运行了，只剩下守护线程存在了，虚拟机也就退出了。

- 进程的状态 ``Thread.States`` ，可以通过 ``Thread.setStatus()`` 修改，通过 ``Thread.getStatus()`` 获得。这些状态不能映射到操作系统的线程状态，它们是 JVM 使用的状态。状态类型如下：

  - NEW： Thread 对象已经创建，但是还没有开始执行。
  - RUNNABLE： Thread 对象正在 Java 虚拟机中运行。
  - BLOCKED： Thread 对象正在等待锁定。
  - WAITING： Thread 对象正在等待另一个线程的动作。
  - TIME_WAITING： Thread 对象正在等待另一个线程的操作，但是有时间限制。
  - THREAD： Thread 对象已经完成了执行。
