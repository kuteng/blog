Apache Thrift介绍
===========================

背景
^^^^^^^^^^^^^^^
**Thrift** 是由Facebook创建，后托管给Apache基金会的项目。它是知名的 **RPC** 框架，同时也可用于信息序列化。

*Thrift* 框架的主要概念是 *服务* ，服务和面向对象编程语言中的类很相似。每个服务中都包含方法，也是OOP中的类似概念。同事 *Thrift* 还实现了需要数据类型。

流程概述
^^^^^^^^^^^^^^^

安装
^^^^^^^^^^^^^^^
系统命令
  ``sudo apt install thrift-compiler``

通过源代码
  通过 *Git* 下载之后，执行如下命令： ``./configure && make``

  可以参考官方教程： `Apache Thrift Tutorial <http://thrift.apache.org/tutorial/>`_

入门示例
^^^^^^^^^^^^^^
编辑简单的 ``thrift`` 文件
  ::

    namespace py tutorial

    service MultiplicationService
    {
        i32 multiply(1:i32 n1, 2:i32 n2),
    }




其他序列化或RPC的比较
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
Avro
  介绍待补充

Protobuf
  介绍待补充

Kryo
  介绍待补充

Dubbo
  介绍待补充

Spring Cloud
  介绍待补充

