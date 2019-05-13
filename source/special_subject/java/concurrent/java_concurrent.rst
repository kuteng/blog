并发相关的解惑
===========================
备忘点
^^^^^^^^^^^^^^^^^^^^^^^
- 并发的数据结构： ``AtomicLong`` 、 ``AtomicInteger`` 、 ``ConcurrentHashMap`` 、 ``LinkedBlockingQueue`` 。
- 对 ``BlockingQueue`` 添加元素时，最好使用 ``offer(e, timeout, unit)`` ，这样在长时间没有空间时可以抛出异常。
- BlockingQueue是在生产/消费者模式下经常会用到的数据结构，通常常用的主要会是ArrayBlockingQueue、LinkedBlockingQueue和SynchronousQueue。ArrayBlockingQeue/LinkedBlockingQueue两者的最大不同主要在于存放Queue中对象方式，一个是数组，一个是链表。SynchronousQueue是一个非常特殊的BlockingQueue，它的模式是在offer的时候，如果没有另外一个线程正在take或poll的话，那么offer就会失败；在take的时候，如果没有另外的线程正好并发在offer，也会失败，这种特殊的模式非常适合用来做要求高响应并且线程出不固定的线程池的Queue。
- ``CopyOnWriteArrayList`` 与 ``ArrayList`` 、 ``CopyOnWriteArraySet`` 与 ``HashSet``
- ``ConcurrentHashMap`` ，JDK6中采用一种更加细粒度的加锁机制Segment“分段锁”，JDK8中采用CAS无锁算法，详细分析推荐阅读。参考阅读： `ConcurrentHashMap源码分析（JDK8版本） <https://blog.csdn.net/u010723709/article/details/48007881>`_
- 线程池在五个重要参数：核心线程数（corePoolSize）、任务队列容量（queueCapacity）、ThreadFactory、BlockingQueue<Runnable>、RejectedExecutionHandler、线程空闲时间(keepAliveTime)。
- **自旋锁** 可以使线程在没有取得锁的时候，不被挂起，而转去执行一个空循环，（即所谓的自旋，就是自己执行空循环），若在若干个空循环后，线程如果可以获得锁，则继续执行。若线程依然不能获得锁，才会被挂起。
- 阻塞锁与自旋锁比较：阻塞锁会有上下文切换，如果并发量比较高且临界区的操作耗时比较短，那么造成的性能开销就比较大了。但是如果临界区操作耗时比较长，一直保持自旋，也会对CPU造成更大的负荷。

锁的类型：
  - 自旋锁：采用让当前线程不停的在循环体内执行实现，当循环的条件被其它线程改变时才能进入临界区。

    由于自旋锁只是将当前线程不停地执行循环体，不进行线程状态的改变，所以响应速度更快。但当线程数不停增加时，性能下降明显，因为每个线程都需要执行，占用CPU时间。如果线程竞争不激烈，并且保持锁的时间段。适合使用自旋锁。

  - 阻塞锁：阻塞锁改变了线程的运行状态，让线程进入阻塞状态进行等待，当获得相应的信号（唤醒或者时间）时，才可以进入线程的准备就绪状态，转为就绪状态的所有线程，通过竞争，进入运行状态。

    阻塞锁的优势在于，阻塞的线程不会占用cpu时间，不会导致 CPu占用率过高，但进入时间以及恢复时间都要比自旋锁略慢。在竞争激烈的情况下 阻塞锁的性能要明显高于自旋锁。

  - 非阻塞锁：多个线程同时调用一个方法的时候，当某一个线程最先获取到锁，这时其他线程判断没拿到锁，这时就直接返回，只有当最先获取到锁的线程释放，其他线程才能进来，在它释放之前其它线程都会获取失败。

  - 重入锁

    可重入锁的最大优点就是可以避免死锁。缺点是必须手动开启和释放锁。

ReentrantLock与synchronized比较：
  - 前者使用灵活，但是必须手动开启和释放锁
  - 前者扩展性好，有时间锁等候（tryLock( )），可中断锁等候（lockInterruptibly( )），锁投票等，适合用于高度竞争锁和多个条件变量的地方
  - 前者提供了可轮询的锁请求，可以尝试去获取锁（tryLock( )），如果失败，则会释放已经获得的锁。有完善的错误恢复机制，可以避免死锁的发生。

``StringBuffer`` 、 ``StringBuilder`` 和 ``StringWriter`` 
  ``StringBuffer`` 相对于 ``StringBuilder`` 是线程安全的。
  关于 ``StringWriter`` 类，通过查看源码发现它内部是调用了 ``StringBuffer`` ，所以我可以认为它是线程安全的。同时在关闭这个“流”之后，调用该实例的方法不会报错。不过在许多开源代码中，我发现出现了使用了该实例却没有 `close()` 的情况，原因是源码中 ``StringWriter`` 的 ``close()`` 方法就是个空实现，调用与否无所谓。

与同步容器对应的并发容器：
  - ``ConcurrentHashMap`` 对应 ``HashMap`` ，JDK6中采用一种更加细粒度的加锁机制Segment“分段锁”，JDK8中采用CAS无锁算法，详细分析推荐阅读。
  - ``CopyOnWriteArrayList`` 对应 ``ArrayList`` ，利用高并发往往是读多写少的特性，对读操作不加锁，对写操作，先复制一份新的集合，在新的集合上面修改，然后将新集合赋值给旧的引用，并通过volatile 保证其可见性，当然写操作的锁是必不可少的了。
  - ``CopyOnWriteArraySet`` 对应 ``HashSet`` ，基于CopyOnWriteArrayList实现，其唯一的不同是在add时调用的是CopyOnWriteArrayList的addIfAbsent方法，其遍历当前Object数组，如Object数组中已有了当前元素，则直接返回，如果没有则放入Object数组的尾部，并返回。
  - ``ConcurrentSkipListMap`` 对应 ``TreeMap`` ，Skip list（跳表）是一种可以代替平衡树的数据结构，默认是按照Key值升序的。Skip list让已排序的数据分布在多层链表中，以0-1随机数决定一个数据的向上攀升与否，通过”空间来换取时间”的一个算法。ConcurrentSkipListMap提供了一种线程安全的并发访问的排序映射表。内部是SkipList（跳表）结构实现，在理论上能够在O（log（n））时间内完成查找、插入、删除操作。
  - ``ConcurrentSkipListSet`` 对应 ``TreeSet`` ，内部基于ConcurrentSkipListMap实现。
  - ``ConcurrentLinkedQueue`` 对应 ``Queue`` ，基于链表实现的FIFO队列（LinkedList的并发版本）。
  - ``LinkedBlockingQueue`` 对应 ``BlockingQueue`` ，基于链表实现的可阻塞的FIFO队列
  - ``ArrayBlockingQueue`` 对应 ``BlockingQueue`` ，基于数组实现的可阻塞的FIFO队列
  - ``PriorityBlockingQueue`` 对应 ``BlockingQueue`` ，按优先级排序的队列

AtomicBoolean
  ``compareAndSet`` ：如果当前值 == 预期值，则以原子方式将该值设置为给定的更新值。这里需要注意的是这个方法的返回值实际上是是否成功修改，而与之前的值无关。

  ``getAndSet`` ：以原子方式设置为给定值，并返回以前的值。

LongAdder、DoubleAdder
  存储由不同线程频繁更新的长整型和双精度值。在这方面他们提供了比 ``AtomicLong`` 、 ``AtomicDouble`` 更好的性能。

  ``LongAccumulator`` 、 ``DoubleAccumulator`` 与前者两者类似不过它们需要在构造函数中指定两个参数：

  - 计数器的初始值
  - 能够表示为lambda表达式的LongBinaryOperator或DoubleBinaryOperator。此表达式接收变量的旧值和要应用的增量，并返回变量的新值。

数据结构
^^^^^^^^^^^^^^^^^^^^^^^
ReentrantLock
:::::::::::::::::::::::
主要利用CAS+CLH队列来实现。它支持公平锁和非公平锁，两者的实现类似。

CAS：Compare and Swap，比较并交换。CAS有3个操作数：内存值V、预期值A、要修改的新值B。当且仅当预期值A和内存值V相同时，将内存值V修改为B，否则什么都不做。该操作是一个原子操作，被广泛的应用在Java的底层实现中。在Java中，CAS主要是由sun.misc.Unsafe这个类通过JNI调用CPU底层指令实现。

CLH队列：带头结点的双向非循环链表(如下图所示)：

|the_diagram_of_clh_in_java_concurrent|

ReentrantLock的基本实现可以概括为：先通过CAS尝试获取锁。如果此时已经有线程占据了锁，那就加入CLH队列并且被挂起。当锁被释放之后，排在CLH队列队首的线程会被唤醒，然后CAS再次尝试获取锁。在这个时候，如果：

- 非公平锁：如果同时还有另一个线程进来尝试获取，那么有可能会让这个线程抢先获取；
- 公平锁：如果同时还有另一个线程进来尝试获取，当它发现自己不是在队首的话，就会排到队尾，由队首的线程获取到锁。

通过构造方法 ``public ReentrantLock(boolean fair)`` 决定是公平锁还是非公平锁。

ReentrantLock中有趣的方法：

- ``lock.tryLock()`` 会直接调用 ``sync.nonfairTryAcquire(1)`` ，即 **直接使用非公平锁** 。
- ``lock.tryLock(long timeout, TimeUnit unit)`` 则会调用 ``sync.tryAcquireNanos(1, unit.toNanos(timeout))`` ，即使用公平锁。
- ``lock.lockInterruptibly()`` 这是一个可中断的获取锁方法，即：请求锁，除非当前线程被中断。

  - 如果没有其他线程持有锁，则当前线程获取到锁，并为锁计数加1，并且立即返回。
  - 如果当前线程已经持有锁，则为锁计数加1，并立即返回。
  - 如果其他线程持有锁，则当前线程将处于不可用状态以达到于线程调度目的，并且休眠直到下面两个事件中的一个发生：

    - 当前线程获取到锁
    - 其他线程中断当前线程

  - 如果当前线程获取到锁，则将锁计数设置为1。
  - 如果当前线程在方法条目上设置了中断状态或者在请求锁的时候被中断，将抛出中断异常。

用法示例
^^^^^^^^^^^^^^^^^^^^^^^

``java.util.concurrent.ConcurrentHashMap<K,V>`` 的用法
  示例 ::

    class TestItem { private String id; private String source; ... }
    ConcurrentHashMap<String, String> storedItems = new new ConcurrentHashMap<String, String>();
    storedItems.compute(item.getId(), (id, oldSource) -> {
        if(oldSource == null) {
            return item.getSource();
        }
        else {
            System.out.println("对于这个Id: " + item.getId() + ", Map中已经存储了");
            return oldSource;
        }
    });

  简单总结： ``ConcurrentHashMap<K, V>`` 的 ``compute(K key, BiFunction fun)`` 方法。这个方法接收的第一个参数与Map的Key类型相同，第二个参数是 `lambda表达式` ，其表达是的第一个参数类型同样是 ``K`` ，第二个参数的类型是 ``V`` 值为Map中对应Key原来的Value。

  注意lambda表达式中，可以引用外部变量，如这里的 ``item`` 。

-------

``java.util.concurrent.LinkedBlockingQueue`` 用法
  示例： ::

    LinkedBlockingQueue<Item> buffer = new LinkedBlockingQueue<>();
    buffer.add(new Item());
    buffer.take();

  ``LinkedBlockingQueue`` 是一个带有阻塞操作的并发数据结构。 ``take()`` 方法是“取数据”的，如果从列表中获取某个项但是列表为空，那么调用方法的线程就会被阻塞，直到列表中有元素为止。

-------

``Thread.yield()``
  将当前线程的执行时间片段让出去，以便由线程调度机制重新决定哪个线程可以执行。

  与 ``Thread.sleep(100)`` 、 ``object.wait()`` 类似却又不同。

待续

参考阅读
^^^^^^^^^^^^^^^^^^^^^^^^^
- `非阻塞同步算法与CAS(Compare and Swap)无锁算法 <http://www.cnblogs.com/Mainz/p/3546347.html?utm_source=tuicool&utm_medium=referral>`_

.. |the_diagram_of_clh_in_java_concurrent| image:: /images/special_subject/java/002_the_diagram_of_clh_in_java_concurrent.webp
