并发相关的解惑
===========================
备忘点
^^^^^^^^^^^^^^^^^^^^^^^
- 并发的数据结构： ``AtomicLong`` 、 ``AtomicInteger`` 、 ``ConcurrentHashMap`` 、 ``LinkedBlockingQueue`` 。
- 对 ``BlockingQueue`` 添加元素时，最好使用 ``offer(e, timeout, unit)`` ，这样在长时间没有空间时可以抛出异常。
- BlockingQueue是在生产/消费者模式下经常会用到的数据结构，通常常用的主要会是ArrayBlockingQueue、LinkedBlockingQueue和SynchronousQueue。ArrayBlockingQeue/LinkedBlockingQueue两者的最大不同主要在于存放Queue中对象方式，一个是数组，一个是链表。SynchronousQueue是一个非常特殊的BlockingQueue，它的模式是在offer的时候，如果没有另外一个线程正在take或poll的话，那么offer就会失败；在take的时候，如果没有另外的线程正好并发在offer，也会失败，这种特殊的模式非常适合用来做要求高响应并且线程出不固定的线程池的Queue。
- ``CopyOnWriteArrayList`` 与 ``ArrayList`` 、 ``CopyOnWriteArraySet`` 与 ``HashSet``
- ``ConcurrentHashMap`` ，JDK6中采用一种更加细粒度的加锁机制Segment“分段锁”，JDK8中采用CAS无锁算法，详细分析推荐阅读。参考阅读： `ConcurrentHashMap源码分析（JDK8版本） <https://blog.csdn.net/u010723709/article/details/48007881>`_


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

待续

参考阅读
^^^^^^^^^^^^^^^^^^^^^^^^^
- `非阻塞同步算法与CAS(Compare and Swap)无锁算法 <http://www.cnblogs.com/Mainz/p/3546347.html?utm_source=tuicool&utm_medium=referral>`_
