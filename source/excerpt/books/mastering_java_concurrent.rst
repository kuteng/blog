《精通Java多线程编程》
======================================

.. toctree::
   :maxdepth: 1

知识点
^^^^^^^^^^^^^^^^^
volatile关键字的注意事项。
  内存可见性：通俗来说就是，线程A对一个volatile变量的修改，对于其它线程来说是可见的，即线程每次获取volatile变量的值都是最新的。

  通过关键字sychronize可以防止多个线程进入同一段代码，在某些特定场景中，volatile相当于一个轻量级的sychronize，因为不会引起线程的上下文切换，但是使用volatile必须满足两个条件：

  1、对变量的写操作不依赖当前值，如多线程下执行a++，是无法通过volatile保证结果准确性的;

  2、该变量没有包含在具有其它变量的不变式中

  摘自： `Java volatile关键字`_

.. _Java volatile关键字: https://www.cnblogs.com/shoshana-kong/p/9066888.html

try-with-resources语句
  之前没有注意这种语句。

摘要
^^^^^^^^^^^^^^^^^
- Java 中创建执行线程的最基本元素： Runnable 接口和 Thread 类，都需要重载或重写 run 方法。而实现 Runnable 接口 比 Thread 类更受欢迎，因为它的灵活性更大。
- Thread 类中有许多不同的方法。用这些方法可以获取线程信息，更改线程的优先级，或者等待线程结束。详见 ``22页`` ``23页``
- Executors 类提供了其他一些创建 ThreadPoolExecutor 对象的方法。详见 ``61页``
- Java中有两种并发的数据结构：阻塞型数据结构、非阻塞型数据结构。具体的方法有：

  - ``put()``, ``putFirst()``, ``putLast()`` ; ``task()``, ``taskFirst()``, ``taskLast()``
  - ``add()``, ``addFirst()``, ``addLast()``; ``remove()``, ``removeFirst()``, ``removeLast()``; ``element()``, ``getFirst()``, ``getLast()``; ``offer()``, ``offerFirst()``, ``offerLast()``; ``poll()``, ``pollFirst()``, ``pollLast()``; ``peek()``, ``neekFirst()``, ``peekLast()``

  更加详细地讲述并发数据结构可以在11章查看。

备忘
^^^^^^^^^^^^^^^^^^^
- ``java.util.concurrent.Future`` 的 ``cancel(boolean mayInterruptIfRunning)`` 方法：其中的参数如果为 `True` ，则表示即便线程正在执行目标Task，也需要立刻中断执行；如果为 `False` 则表示只有在进行没有执行目标Task时，中断操作才会生效。（ `Future` 类是 ``ThreadPoolExecutor.submit(Runnable task)`` 的返回类型，它可以控制已提交到线程池中的Task的执行。）
- Java提供了两个常用的线程执行器： ``ThreadPoolExecutor`` 、 ``ScheduledThreadPoolExecutor`` 。开发人员可以对这些执行器扩展，推荐的可重载的方法有：

  - ``beforeExecute(Thread t, Runnable r)``
  - ``afterExecute(Runnable r, Throwable t)``
  - ``newTaskFor(Runnable runnable, T value)``
  - ``newTaskFor(Callable<T> callable)``
  - ``decorateTask(Runnable runnable, RunnableScheduledFuture<V> task)``

  其中的 `newTaskFor()` 方法是创建 `Future` 对象的。（就是 ``ThreadPoolExecutor.submit()`` 方法的返回值）。

- 初始化 `ThreadPoolExecutor` 、 `ScheduledThreadPoolExecutor` 执行器的时候，可以传入下面的三个参数： ``BlockingQueue<Runnable>`` 、 ``ThreadFactory`` 、 ``RejectedExecutionHandler`` 。
- 有时间了解一下接口 ``java.util.function.Function<T, R>`` 、 数据结构 ``java.util.concurrent.ConcurrentHashMap<K,V>`` 。
- `Java 9` 中新增加的功能 `JShell` ，可以去了解一下。同时 `Java 9` 中增加的其他特性有必要整体了解一下，如：模块系统、改进的javadoc、Stream API、多分辨率图像API。参考网页： http://www.runoob.com/java/java9-new-features.html 。
- 源码阅读备忘： ``java.util.concurrent.LinkedBlockingQueue`` 的 ``take()`` 方法。
- 备忘：“一个人”输入 ``Person`` 这个类，但是如何从XML文件中识别（解析）出“人的信息”，不属于 ``Person`` 类。因为被解析的XML的来源、格式可能是不同的，甚至根本不是XML文件。
