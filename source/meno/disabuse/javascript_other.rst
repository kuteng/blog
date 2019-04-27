JavaScript备忘
========================
Vue的双向绑定
^^^^^^^^^^^^^^^^^^^^^^^^
vue数据双向绑定是通过数据劫持结合发布者-订阅者模式的方式来实现的。

vue是通过Object.defineProperty()来实现数据劫持的。而 ``Object.defineProperty()`` 可以来控制一个对象属性的一些特有操作，比如读写权、是否可以枚举

实现过程
  1. 实现一个监听器Observer，用来劫持并监听所有属性，如果有变动的，就通知订阅者。
  2. 实现一个订阅者Watcher，可以收到属性的变化通知并执行相应的函数，从而更新视图。
  3. 实现一个解析器Compile，可以扫描和解析每个节点的相关指令，并根据初始化模板数据以及初始化相应的订阅器。

  如图：

  |the_grap_of_two_way_data_binding_in_vue|

.. |the_grap_of_two_way_data_binding_in_vue| image:: /images/meno/disabuse004_the_grap_of_two_way_data_binding_in_vue.png
