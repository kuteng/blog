Java的解惑备忘
========================

读嵌套字的内容
^^^^^^^^^^^^^^^^^^^^^^^
.. code-block :: java

  ServerSocket serverSocket = new ServerSocket(Constants.CONCURRENT_PORT);
  Socket clientSocket = serverSocket.accept();
  BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
  String line = in.readLine();
  ....

泛型的优点
^^^^^^^^^^^^^^^^^^^^^^^
- 可以将类型参数化。
- 类型安全
- 消除强制类型转化，增强可读性与安全性，并提高了代码重用率。
- 潜在的性能收益。

另外泛型在使用中还有一些规则和限制：

1. 泛型的类型参数只能是类类型（包括自定义类），不能是简单类型。
2. 同一种泛型可以对应多个版本（因为参数类型是不确定的），不同版本的泛型类实例是不兼容的。
3. 泛型的类型参数可以有多个。
4. 泛型的参数类型可以使用extends语句，例如<T extends superclass>。习惯上成为“有界类型”。
5. 泛型的参数类型还可以是通配符类型。例如Class<?> classType = Class.forName(Java.lang.String);

一些注意点：

泛型只存在编译期
  泛型值存在于java的编译期（进行检验），编译后生成字节码文件泛型是被擦除的，即运行时没有泛型的概念

  证明的代码如下：

  .. code-block:: java

    ArrayList<Integer> list = new ArrayList<Integer>();
    list.add(111);
    list.add(222);
    // 直接插入字符串会报错
    // list.add("Hello");

    // 但是通过反射的方式执行add方法，插入字符串不会报错。
    Class clazz3 = Class.forName("java.util.ArrayList");//获取ArrayList的字节码文件
    Method m = clazz3.getMethod("add", Object.class);//获取add() 方法，Object.class 代表任意对象类型的数据
    m.invoke(list,"Hello");//通过反射添加字符串类型元素数据
    System.out.println(list);//运行结果：[111, 222, Hello]

List<?> 与 List<Object>
  - 我们无法通过 ``new ArrayList<?>()`` 方式创建一个对象。
  - 如果向 ``List<?>`` 对象中添加了 String 类型数据，再添加 Integer 类型数据会报错。但在 ``List<Object>`` 中这样是可以的。

泛型的上界和下界
  -  ``List<? extends Number>`` 定义了泛型的上界是 Number ，即 List 中包含的元素类型是 Number 及其子类。 **该对象只能get()，不能add()** 。它唯一能够确保的即使我们get到的类型是 ``Number`` 。
  -  ``List<? super Number>`` 定义了泛型的下界, 即 List 中包含的是 Number 及其父类。 **该对象可以add() Number的子类对象，也能get()但返回类型是Object** ， **下界不影响往里存，但往外取只能放在Object 对象里** 。

注意：下面语法中，最后是错误的，。

.. code-block:: java

  public class Box<T> {
      // T stands for "Type"
      private T t;

      public Box(T t) {
          this.t = t;
      }
      public void set(T t) {
          this.t = t;
      }
      public T get() {
          return t;
      }
  }

  class Food { }
  class Rice extends Food { }
  class Fruit extends Food { }
  class Apple extends Fruit {}
  class Orange extends Fruit {}
  // 下面这句话错了。因为编译器认为的容器之间没有继承关系，所以我们不能这样做。
  // Box<Fruit> box = Box<Orange>(new Orange)
  Box<? extends Fruit> box = new Box<Orange>(new Orange);
  // 上界只能外围取，不能往里放。原因是 Java 编译器只知道容器内是 Fruit 或者它的派生类， 但是不知道是什么类型。可能是 Fruit、 可能是 Orange、可能是Apple？当编译器在看到 box 用 Box 赋值后， 它就把容器里标上占位符 “AAA” 而不是 “水果”等，当在插入时编译器不能匹配到这个占位符，所有就会出错。
  // 所以下面的set语句也不能用了。
  // box.set(new Fruit);
  // box.set(new Orange);
  // 取出来的东西只能存放在Fruit或它的基类里
  Fruit fruit = box.get();

PECS 原则
  - 如果要从集合中读取类型T的数据， 并且不能写入，可以使用 上界通配符（<？extends>）
  - 如果要从集合中写入类型T 的数据， 并且不需要读取，可以使用下界通配符（<? super>）。
  - 如果既要存又要取， 那么就要使用任何通配符。

Java中的移位运算符
^^^^^^^^^^^^^^^^^^^^^^
- <<  : 左移运算符，num << 1,相当于num乘以2
- >>  : 右移运算符，num >> 1,相当于num除以2
- >>> : 无符号右移，忽略符号位，空位都以0补齐

关于HashMap
^^^^^^^^^^^^^^^^^^^^^^^
- ``hash`` : 翻译为“散列”，就是把任意长度的输入，通过散列算法，变成固定长度的输出，该输出就是散列值。
- ``hash冲突`` ：就是根据key即经过一个函数f(key)得到的结果的作为地址去存放当前的key value键值对(这个是hashmap的存值方式)，但是却发现算出来的地址上已经有人先来了。就是说这个地方被抢了啦。这就是所谓的hash冲突啦。
- HashMap使用链表法解决 ``Hash冲突`` 的，但是 **JDK1.8** 中根据key的hash与table长度确定table位置，同一个位置的key以链表形式存储，超过一定限制链表转为树。总结为 **数组+链表+红黑树的方式思想** 。
- 解决 **Hash冲突** 的其他方法：开放定址法、链地址法、再哈希。
- HashMap中则通过 ``h&(length-1)`` 的方法来代替取模，同样实现了均匀的散列，但比使用 ``h%(length-1)`` 效率要高很多
- 默认构造器 ``HashMap()`` ：构建一个初始容量为 ``16`` ，负载因子为 0.75 的 HashMap。

参考：

- `HashMap之java8新特性 <https://www.cnblogs.com/shengkejava/p/6771469.html>`_ 。

面向接口编程
^^^^^^^^^^^^^^^^^^^^^^^^
面向接口编程就是先把客户的业务逻辑线提取出来，作为接口，业务具体实现通过该接口的实现类来完成。当客户需求变化时，只需编写该业务逻辑的新的实现类，通过更改配置文件(例如Spring框架)中该接口的实现类就可以完成需求，不需要改写现有代码，减少对系统的影响。

优点
  - 降低程序的耦合性。
  - 易于程序的扩展。
  - 有利于程序的维护。

开闭原则
  其遵循的思想是：对扩展开放，对修改关闭。其恰恰就是遵循的是使用接口来实现。在使用面向接口的编程过程中，将具体逻辑与实现分开，减少了各个类之间的相互依赖，当各个类变化时，不需要对已经编写的系统进行改动，添加新的实现类就可以了，不在担心新改动的类对系统的其他模块造成影响。

反射机制
^^^^^^^^^^^^^^^^^^^^^^^^
指在运行状态中,对于任意一个类,都能够知道这个类的所有属性和方法,对于任意一个对象,都能调用它的任意一个方法.这种动态获取信息,以及动态调用对象方法的功能叫java语言的反射机制.

应用场景
  - 生成动态代理
  - 面向切片编程(在调用方法的前后各加栈帧).

原理
  - 首先明确的概念: 一切皆对象----类也是对象.
  - 然后知道类中的内容 : ``modifier``  ``constructor``  ``field``  ``method`` 。
  - 其次明白加载: 当Animal.class在硬盘中时,是一个文件,当载入到内存中,可以认为是一个对象,是java.lang.class的对象.
  - 借用 ``getConstructor`` 、 ``getMethods`` 、 ``getFields`` 、 ``getModifiers`` 等方法。

反射得到一个对象与实例化得到一个对象的区别
  - 反射对象是直到程序运行期间才知道类的名字的实例，这时才获取对象的成员，并设置属性。此时要用到类的全路径 ，用类的全路径来加载类，并返回该类的对象。以后就可以遍历类中的各个方法，各个属性。
  - new是给类直接在内存中创建一个实例，并且可以直接初始化等。不需要类的全路径。

代码示例
  实例化一个对象：

  .. code-block:: java

    //1.获取Class对象
    Class clazz = Class.forName("com.atguigu.java.fanshe.Person");  
    //2. 利用Class对象的newInstance方法创建一个类的实例
    Object obj =  clazz.newInstance();
    // 获得类的方法对象，"run" 是方法名，Integer.class 是参数列表中的参数类型。
    Method method = clazz.getMethod("run", Integer.class);
    // 通过反射调用方法。
    method.invoke(object, 1);

Java内存溢出(OOM)异常排查指南
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
堆空间不足
:::::::::::::::::
异常：
  java.lang.OutOfMemoryError:Java heap space

背景：
  Java应用程序在启动时会指定所需要的内存大小，它被分割成两个不同的区域：Heap space（堆空间）和Permgen（永久代）

  这两个区域的大小可以在JVM（Java虚拟机）启动时通过参数 ``-Xmx`` 和 ``-XX:MaxPermSize`` 设置，如果你没有显式设置，则将使用特定平台的默认值。

  注意的是：即使有足够的物理内存可用，只要达到堆空间设置的大小限制，此异常仍然会被触发。

原因：
  - 启动是提供的堆空间太小。
  - 流量/数据量峰值。某一时刻，当用户数量或数据量突然达到一个峰值，并且这个峰值已经超过了设计之初预期的阈值，那么以前正常的功能将会停止，并触发这个异常。
  - 内存泄漏：特定的编程错误会导致你的应用程序不停的消耗更多的内存，每次使用有内存泄漏风险的功能就会留下一些不能被回收的对象到堆空间中，随着时间的推移，泄漏的对象会消耗所有的堆空间，最终触发java.lang.OutOfMemoryError: Java heap space错误。

解决方法：
  使用参数 ``-Xmx`` ，提供更大的堆空间即可。

垃圾回收低效
:::::::::::::::::::::
异常：
  java.lang.OutOfMemoryError:GC overhead limit exceeded

背景：
  默认情况下，当应用程序花费超过 ``98%`` 的时间用来做GC并且回收了不到 ``2%`` 的堆内存时，会抛出 ``java.lang.OutOfMemoryError:GC overhead limit exceeded`` 错误。具体的表现就是你的应用几乎耗尽所有可用内存，并且GC多次均未能清理干净。

解决方法：
  增加堆内存

持久代所在区域的内存
:::::::::::::::::::::::::
异常：
  java.lang.OutOfMemoryError: PermGen space

背景：
  Java内存分为：堆内存、方法区、本地方法区、计数器及java栈。其中方法区又称为 “持久代”。

  持久代主要存储的是每个类的信息，比如： **类加载器引用** 、 **运行时常量池** （所有常量、字段引用、方法引用、属性）、 **字段** (Field)数据、 **方法** (Method)数据、 **方法代码** 、 **方法字节码** 等等。我们可以推断出， **PermGen的大小取决于被加载类的数量以及类的大小** 。

原因：
  - 在程序启动时，太多的类或者太大的类被加载到permanent generation（持久代）。
  - 在重新部署时，如果应用中有类的实例对当前的classloader的引用，那么Permgen区的class将无法被卸载，导致Permgen区的内存一直增加直到出现Permgen space错误。许多第三方库以及糟糕的资源处理方式（比如：线程、JDBC驱动程序、文件系统句柄）使得卸载以前使用的类加载器变成了一件不可能的事。反过来就意味着在每次重新部署过程中，应用程序所有的类的先前版本将仍然驻留在Permgen区中，你的每次部署都将生成几十甚至几百M的垃圾。

    就以线程和JDBC驱动来说说。很多人都会使用线程来处理一下周期性或者耗时较长的任务，这个时候一定要注意线程的生命周期问题，你需要确保线程不能比你的应用程序活得还长。否则，如果应用程序已经被卸载，线程还在继续运行，这个线程通常会维持对应用程序的classloader的引用，造成的结果就不再多说。多说一句，开发者有责任处理好这个问题，特别是如果你是第三方库的提供者的话，一定要提供线程关闭接口来处理清理工作。

  Java中堆空间是JVM管理的最大一块内存空间，可以在JVM启动时指定堆空间的大小，其中堆被划分成两个不同的区域：新生代（Young）和老年代（Tenured），新生代又被划分为3个区域： ``Eden`` 、 ``From Survivor`` 、 ``To Survivor`` 。

解决方法：
  - 对于 *初始化时的OutOfMemoryError* 通过 ``-XX：MaxPermSize`` 参数，给予持久代更大的内存。如： ``java -XX:MaxPermSize=512m ...`` 。
  - 对于 *Redeploy时的OutOfMemoryError* 分析dump文件：首先，找出引用在哪里被持有；其次，给你的web应用程序添加一个关闭的hook，或者在应用程序卸载后移除引用。 **可以使用如下命令导出dump文件** ： ``jmap -dump:format=b,file=dump.hprof <process-id>`` 。

  - 对于 **运行时OutOfMemoryError** ：

    - 首先你需要检查是否允许GC从PermGen卸载类，JVM的标准配置相当保守，只要类一创建，即使已经没有实例引用它们，其仍将保留在内存中，特别是当应用程序需要动态创建大量的类但其生命周期并不长时，允许JVM卸载类对应用大有助益，你可以通过在启动脚本中添加以下配置参数来实现： ``-XX:+CMSClassUnloadingEnabled`` 。

      默认情况下，这个配置是未启用的，如果你启用它，GC将扫描PermGen区并清理已经不再使用的类。但请注意，这个配置只在UseConcMarkSweepGC的情况下生效，如果你使用其他GC算法，比如：ParallelGC或者Serial GC时，这个配置无效。所以使用以上配置时，需要配合： ``-XX:+UseConcMarkSweepGC``

    - 如果已经确保JVM可以卸载类，但是仍然出现内存溢出问题，那么你应该继续分析dump文件，使用以下命令生成dump文件： ``jmap -dump:file=dump.hprof,format=b <process-id>`` 。
    - 当生成的堆转储文件，并利用像Eclipse Memory Analyzer Toolkit这样的工具来寻找应该卸载却没被卸载的类加载器，然后对该类加载器加载的类进行排查，找到可疑对象，分析使用或者生成这些类的代码，查找产生问题的根源并解决它。

元空间不足
:::::::::::::::::::
异常：
  java.lang.OutOfMemoryError:Metaspace

背景：
  java8开始，删除了持久代，引入了 **Metaspace** （请看： :ref:`metaspace_and_permgen` ）

原因：
  - 太多的类或太大的类加载到元空间。

解决方法：
  - 通过参数 ``-XX：MaxMetaspaceSize`` ，增加 ``Metaspace`` 的大小。如： ``java -XX：MaxMetaspaceSize = 512m`` 。

Spring的事务
^^^^^^^^^^^^^^^^^^^^
重要参数：
  - rollbackFor：回滚条件
  - isolation：隔离强度。分别有： ``READ_UNCOMMITTED`` （读操作未提交）、 ``READ_COMMITTED`` （读操作已提交）、 ``REPEATABLE_READ`` （可重读）、 ``SERIALIZABLE`` （可串行化）。
  - propagation: 传播行为。有7种传播行为,如：有事务则加入无事务则创建；有事务加入无事务抛错；如果有事务加入否则以无事务执行；有事务挂起新建事务；以非事务执行，如果已存在事务则挂起它；已非事务执行，如果以有事务则抛异常；如果当前存在事务则在嵌套事务内执行，如果当前没有事务则执行与 PROPAGATION_REQUIRED 类似的操作。
  - timeout: 超时。
  - readOnly：是否为只读。依靠数据库实现，告诉数据库此事务中没有修改数据库的操作。

知识点
^^^^^^^^^^^^^^^^^^^

.. _metaspace_and_permgen:

元空间与持久代
::::::::::::::::::::
java8开始，java的内存模型发生了重大变化，删除了 **持久代** ( ``PermGen`` )，引入了 **元空间** ( ``Metaspace`` )。请注意：不是简单的将PermGen区所存储的内容直接移到Metaspace区，PermGen区中的某些部分，已经移动到了普通堆里面。

原因是：

- 应用程序所需要的PermGen区大小很难预测，设置太小会触发 ``PermGen OutOfMemoryError`` 错误，过度设置导致资源浪费。
- 提升GC性能，在HotSpot中的每个垃圾收集器需要专门的代码来处理存储在PermGen中的类的元数据信息。从PermGen分离类的元数据信息到Metaspace，由于Metaspace的分配具有和Java Heap相同的地址空间，因此Metaspace和Java Heap可以无缝的管理，而且简化了FullGC的过程，以至将来可以并行的对元数据信息进行垃圾收集，而没有GC暂停。
- 支持进一步优化，比如：G1并发类的卸载，也算为将来做了准备。

详细内容可以查看 `Java PermGen 去哪里了? <http://ifeve.com/java-permgen-removed/>`_

Java应用程序已达到其可以启动线程数量的极限
:::::::::::::::::::::::::::::::::::::::::::::
异常
  java.lang.OutOfMemoryError:Unable to create new native thread

背景：
  当JVM向OS请求创建一个新线程时，而OS却无法创建新的native线程时就会抛出Unable to create new native thread错误。一台服务器可以创建的线程数依赖于物理配置和平台，建议运行下文中的示例代码来测试找出这些限制。总体上来说，抛出此错误会经过以下几个阶段：

  - 运行在JVM内的应用程序请求创建一个新的线程
  - JVM向OS请求创建一个新的native线程
  - OS尝试创建一个新的native线程，这时需要分配内存给新的线程
  - OS拒绝分配内存给线程，因为32位Java进程已经耗尽内存地址空间（2-4GB内存地址已被命中）或者OS的虚拟内存已经完全耗尽
  - ``Unable to create new native thread`` 错误将被抛出

原因：
  当JVM向OS请求创建一个新线程时，而OS却无法创建新的native线程时就会抛出该异常。

解决方案：
  有时，你可以通过在OS级别增加线程数限制来绕过这个错误。如果你限制了JVM可在用户空间创建的线程数，那么你可以检查并增加这个限制。下面的命令可以查看系统的线程限制： ``ulimit -u`` 。

交换空间耗尽
:::::::::::::::::::::
异常：
  java.lang.OutOfMemoryError:Out of swap space

背景：
  Java应用程序在启动时会指定所需要的内存大小，可以通过-Xmx和其他类似的启动参数来指定。在JVM请求的总内存大于可用物理内存的情况下，操作系统会将内存中的数据交换到磁盘上去。

  ``Out of swap space?`` 表示交换空间也将耗尽，并且由于缺少物理内存和交换空间，再次尝试分配内存也将失败。

  当应用程序向 ``JVM native heap`` 请求分配内存失败并且native heap也即将耗尽时，JVM会抛出Out of swap space错误。该错误消息中包含分配失败的大小（以字节为单位）和请求失败的原因。

原因：
  - 该异常往往是由操作系统级别的问题引起的，如：操作系统配置的交换空间不足；系统上的另一个进程消耗所有内存资源。
  - 还有可能是本地内存泄漏导致应用程序失败，比如：应用程序调用了native code连续分配内存，但却没有被释放。

解决方案：
  - 增加交换空间
  - 升级机器以包含更多内存
  - 优化应用程序以减少其内存占用
  - 如果你的应用程序部署在JVM需要同其他进程激烈竞争获取资源的物理机上，建议将服务隔离到单独的虚拟机中

  **注意** ：Java GC会扫描内存中的数据，如果是对交换空间运行垃圾回收算法会使GC暂停的时间增加几个数量级，因此你应该 **慎重考虑使用增加交换空间的解决方法** 。

应用程序试图分配大于Java虚拟机可以支持的数组
::::::::::::::::::::::::::::::::::::::::::::::::
异常：
  java.lang.OutOfMemoryError:Requested array size exceeds VM limit

描述：
  当你遇到Requested array size exceeds VM limit错误时，意味着你的应用程序试图分配大于Java虚拟机可以支持的数组。

背景：
  Java对应用程序可以分配的最大数组大小有限制。不同平台限制有所不同，但通常在1到21亿个元素之间。

原因：
  该错误由JVM中的native code抛出。 JVM在为数组分配内存之前，会执行特定于平台的检查：分配的数据结构是否在此平台中是可寻址的。

  - 数组增长太大，最终大小在平台限制和Integer.MAX_INT之间
  - 你有意分配大于2 ^ 31-1个元素的数组

解决方法：
  - 在第一种情况下，检查你的代码库，看看你是否真的需要这么大的数组。也许你可以减少数组的大小，或者将数组分成更小的数据块，然后分批处理数据。
  - 在第二种情况下，记住Java数组是由int索引的。因此，当在平台中使用标准数据结构时，数组不能超过2 ^ 31-1个元素。事实上，在编译时就会出错：error：integer number too large。

线程被系统杀死
::::::::::::::::
异常：
  Out of memory:Kill process or sacrifice child

背景：
  操作系统是建立在进程的概念之上，这些进程在内核中作业，其中有一个非常特殊的进程，名叫“内存杀手（Out of memory killer）”。当内核检测到系统内存不足时，OOM killer被激活，然后选择一个进程杀掉。哪一个进程这么倒霉呢？选择的算法和想法都很朴实：谁占用内存最多，谁就被干掉。细节可以阅读 `理解和配置 Linux 下的 OOM Killer <https://www.vpsee.com/2013/10/how-to-configure-the-linux-oom-killer/>`_ 。

  当可用虚拟虚拟内存(包括交换空间)消耗到让整个操作系统面临风险时，就会产生Out of memory:Kill process or sacrifice child错误。在这种情况下，OOM Killer会选择“流氓进程”并杀死它。

原因：
  默认情况下，Linux内核允许进程请求比系统中可用内存更多的内存，但大多数进程实际上并没有使用完他们所分配的内存。这就跟现实生活中的宽带运营商类似，他们向所有消费者出售一个100M的带宽，远远超过用户实际使用的带宽，一个10G的链路可以非常轻松的服务100个(10G/100M)用户，但实际上宽带运行商往往会把10G链路用于服务150人或者更多，以便让链路的利用率更高，毕竟空闲在那儿也没什么意义。

  Linux内核采用的机制跟宽带运营商差不多，一般情况下都没有问题，但当大多数应用程序都消耗完自己的内存时，麻烦就来了，因为这些应用程序的内存需求加起来超出了物理内存（包括 swap）的容量，内核（OOM killer）必须杀掉一些进程才能腾出空间保障系统正常运行。

解决方案：
  - 最有效也是最直接的方法就是升级内存
  - 调整OOM Killer配置、水平扩展应用，将内存的负载分摊到若干小实例上
  - 我们不建议的做法是增加交换空间。因为会增大GC的负担。

特殊名词
^^^^^^^^^^^^^^^^^^^
fail-fast
  是一种错误检测机制。它只能被用来检测错误，因为JDK并不保证fail-fast机制一定会发生。

  遍历集合时，遇到它被修改（可能是其他线程）的情况时。

  原理：以 ``ArrayList`` 为例。无论是add()、remove()，还是clear()，只要涉及到修改集合中的元素个数时，都会改变modCount的值；而在遍历过程中，当它执行到next()函数时，调用checkForComodification()比较“expectedModCount”和“modCount”的大小；

Native Heap Memory
  它是JVM内部使用的Memory，这部分的Memory可以通过JDK提供的JNI的方式去访问，这部分Memory效率很高，但是管理需要自己去做，如果没有把握最好不要使用，以防出现内存泄露问题。JVM 使用 ``Native Heap Memory`` 用来优化代码载入（JTI代码生成），临时对象空间申请，以及JVM内部的一些操作。

特殊方法
^^^^^^^^^^^^^^^^^^^^^^^^
- ``Integer.numberOfLeadingZeros(int i)`` ：返回无符号整型i的最高非零位前面的0的个数，包括符号位在内。
- ``Objects.requireNonNull(obj)`` 检查对象是否是Null，如果是，则抛出异常 ``NullPointerException`` 。
