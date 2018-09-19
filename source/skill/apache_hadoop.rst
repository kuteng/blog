Apache Hadoop
==============================================

.. toctree::
   :maxdepth: 1
   :caption: 视频教程:

入门
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
参考网址: `部署在单节点集群上 <http://hadoop.apache.org/docs/stable/hadoop-project-dist/hadoop-common/SingleCluster.html>`_

前提条件
#############################################
安装有：
- java
- ssh
- rsync
- 下载并解压好hadoop压缩包（本实例使用 `2.9.1` 版本）
- 在 ``etc/hadoop/hadoop-env.sh`` 中设置好 **JAVA_HOME** 。

本地运行
#############################################
本地运行是指将hadoop作为一个简单的app进行启动，如： ::

    bin/hadoop jar share/hadoop/mapreduce/hadoop-mapreduce-examples-2.9.1.jar grep input output 'dfs[a-z.]+'

本实例中，hadoop作为一个java进程进行启动，它同时加载了另外一个jar文件（jar文件作为参数传进hadoop中了）。

伪分布式模式启动
#############################################

