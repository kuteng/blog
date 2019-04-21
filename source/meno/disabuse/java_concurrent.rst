并发相关的解惑
===========================

- 并发的数据结构： ``AtomicLong`` 、 ``AtomicInteger`` 、 ``ConcurrentHashMap`` 、 ``LinkedBlockingQueue`` 。

-------

``StringBuffer`` 、 ``StringBuilder`` 和 ``StringWriter`` 
  ``StringBuffer`` 相对于 ``StringBuilder`` 是线程安全的。
  关于 ``StringWriter`` 类，通过查看源码发现它内部是调用了 ``StringBuffer`` ，所以我可以认为它是线程安全的。同时在关闭这个“流”之后，调用该实例的方法不会报错。不过在许多开源代码中，我发现出现了使用了该实例却没有 `close()` 的情况，原因是源码中 ``StringWriter`` 的 ``close()`` 方法就是个空实现，调用与否无所谓。

-------

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
