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

- 在文件中插入内容

  1. 在文件的首行插入指定内容： ::

      sed -i "1i#! /bin/sh -" a 

     执行后，在a文件的第一行插入#! /bin/sh -

  2. 在文件的指定行（n）插入指定内容： ::

      sed -i "niecho "haha"" a 

    执行后，在a文件的第n行插入echo "haha"

  3. 在文件的末尾行插入指定内容： ::

      echo “haha” >> a

    执行后，在a文件的末尾行插入haha

常用的命令
^^^^^^^^^^^^^^^^^^^^
- 解压tar.gz文件 ::

    tar xvf <filename>.tar.gz

  对于tar.bz2结尾的文件 ::

    tar -xjf all.tar.bz2

- 想要personal项目下的 `install.sh` 生效，需要切换到personal目录下，使用下面的命令运行 `install.sh` ::

    source install.sh

