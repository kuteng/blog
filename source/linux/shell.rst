Shell
======================================

.. toctree::
   :maxdepth: 1

常用脚本
^^^^^^^^^^^^^^^^^^
- 判断进程是否存在，从而可以做预警处理..  ::

    count=`ps -ef | grep Seeyon | grep -v "grep" | wc -l`
    echo $count

    if [ $count -gt 0 ];
    then
        echo "Good." >> /home/admin/test123.txt
    else
        echo "Down!" >> /home/admin/test123.txt
    fi

  ::

    #!/bin/sh
    ps -fe|grep elasticsearch |grep -v grep

    if [ $? -ne 0 ]
    then
        echo "start process....."
    else
        echo "runing....."
    fi

常用的命令
^^^^^^^^^^^^^^^^^^^^
- 解压tar.gz文件 ::

    tar xvf <filename>.tar.gz

  对于tar.bz2结尾的文件 ::

    tar -xjf all.tar.bz2
