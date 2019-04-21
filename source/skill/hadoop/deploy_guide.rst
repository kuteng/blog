部署指南
======================

入门
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
参考网址: `部署在单节点集群上 <http://hadoop.apache.org/docs/stable/hadoop-project-dist/hadoop-common/SingleCluster.html>`_

前提条件
:::::::::::::::::::::::::::::::::::::::::::::
安装有：
- java
- ssh
- rsync
- 下载并解压好hadoop压缩包（本实例使用 `2.9.1` 版本）
- 在 ``etc/hadoop/hadoop-env.sh`` 中设置好 **JAVA_HOME** 。

本地运行
:::::::::::::::::::::::::::::::::::::::::::::
本地运行是指将hadoop作为一个简单的app进行启动，如： ::

    bin/hadoop jar share/hadoop/mapreduce/hadoop-mapreduce-examples-2.9.1.jar grep input output 'dfs[a-z.]+'

本实例中，hadoop作为一个java进程进行启动，它同时加载了另外一个jar文件（jar文件作为参数传进hadoop中了）。

打包Jar
:::::::::::::::::::::::::::::::::::::::::::::
- 创建一个Maven项目
- 在 ``pom.xml`` 中添加如下依赖（注意hadoop的版本号） ::

    <dependency>
      <groupId>org.apache.hadoop</groupId>
      <artifactId>hadoop-mapreduce-client-common</artifactId>
      <version>2.9.1</version>
    </dependency>
    <dependency>
      <groupId>org.apache.hadoop</groupId>
      <artifactId>hadoop-common</artifactId>
      <version>2.9.1</version>
    </dependency>

- 编辑代码
- 运行 ``mvn package`` 进行打包。

伪分布式模式启动
:::::::::::::::::::::::::::::::::::::::::::::


常见问题
:::::::::::::::::::::::::::::::::::::::::::::

问题一：如果在关机之前没有关闭hadoop，再次启动后，发现NameNode服务没有起来。
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
现象： ``http://localhost:50070`` 这个网址启动不起来；同时使用 ``jps`` 命令，发现没有 ``NameNode`` 。
原因：Hadoop的namenode是保存在临时目录 ``/tmp`` 下的，而每次重启计算机，这个目录都会被清空。
解决方法：在 ``etc/hadoop/core-site.xml`` 中，将临时文件的存放路径（ ``hadoop.tmp.dir`` ）修改一下。
