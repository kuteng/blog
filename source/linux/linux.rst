Linux
===================================

.. toctree::
   :maxdepth: 2
   :caption: Contents:

常用命令
^^^^^^^^^^^^^^^^^^^^^
- 查看内存信息

  查看内存的插槽数,已经使用多少插槽.每条内存多大，已使用内存多大 ::

    sudo dmidecode | grep -P -A5 "Memory\s+Device" | grep Size | grep -v Range

  结果例如 ::

    Size:2048MB  
    Size:2048MB  
    Size:NoModuleInstalled  
    Size:NoModuleInstalled  
    Size:NoModuleInstalled  
    Size:NoModuleInstalled  
    Size:NoModuleInstalled  
    Size:NoModuleInstalled  

  查看内存支持的最大内存容量 ::

    sudo dmidecode | grep -P 'Maximum\s+Capacity'

  结果 ::

    MaximumCapacity:64GB  

  查看内存的频率 ::

    sudo dmidecode | grep -P -A16 "Memory\s+Device" | grep 'Speed'

  结果 ::

    Speed:667MHz(1.5ns)  
    Speed:667MHz(1.5ns)  
    Speed:667MHz(1.5ns)  
    Speed:667MHz(1.5ns)  
    Speed:667MHz(1.5ns)  
    Speed:667MHz(1.5ns)  
    Speed:667MHz(1.5ns)  
    Speed:667MHz(1.5ns)  

- 注册新用户

  - 创建用户 ::

      useradd testuser

    注意：新创建用户会在 `/home` 下创建一个用户目录 `testuser`
  - 修改密码 ::

      paswd testuser

  - 修改用户这个名利的相关参数 ::

      usermod --help

  - 删除用户 ::

      userdel testuser

  - 删除用户的主目录 ::

      rm -rf /home/testuser

  - 新建用户tab无法补全命令 

    - 通过 ``cat /ect/passwd`` ，可知这个用户的shell是 ``/bin/sh``
    - 修改为 ``/bin/bash`` 即可


  - 赋予 `sudo` 权限：

    编辑文件 ``/etc/sudoers`` ，添加一行 ::

      test   ALL=(ALL:ALL) ALL

    或者修改为这样应该也可以 ::

      test   ALL=(root:root) ALL

- 修改用户密码

  修改用户自己的密码： ::

    passwd

  修改其他用户的密码： ::

    passwd othername

- 向其他用户发送消息::

      write [用户名] [ttyname(例如pts/1)]
      write all
      wall

- 判断用户是否存在::

      id peter >& /dev/null
      if [ $? -ne 0 ];then
          echo "don't have the user"
      fi

- 创建目录及子目录。::

        mkdir -p folder1/folder2/folder3

- 进程搜索::

      ps -d -opid,etime,args | grep "$1" | grep -v grep

- 解压tar.gz文件 ::

    tar xvf <filename>.tar.gz

  对于tar.bz2结尾的文件 ::

    tar -xjf all.tar.bz2

- 想要personal项目下的 `install.sh` 生效，需要切换到personal目录下，使用下面的命令运行 `install.sh` ::

    source install.sh

- `grep` 与 `find` 组合： ::

    find -name "*.py" | xargs grep -rn "media_analysis"

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

- 显示文件peter.note的第5行到第9行的内容。::

      sed -n "5,9p" peter.note

- 对数据结果进行统计<br/>
  wc命令：统计指定文件中的字节数、字数、行数，并将统计结果显示输出。
  例如：统计ps结果的行数::

      ps -aux "sync/" | wc -l

- 使用更简洁的搜索文件命令：::

      mlocate

- wget使用代理下咋文件： ::

    wget -c -r -np -k -L -p -e "http_proxy=http://127.0.0.1:8087" [URL]

- 注销当前用户： ::

    pkill X

- 查看端口占用情况： ::

    netstat –apn | grep 1000

- 自动切换python2和python3 ::

    sudo update-alternatives --install /usr/bin/python python /usr/bin/python2 100
    sudo update-alternatives --install /usr/bin/python python /usr/bin/python3 150

  开始切换 ::

    sudo update-alternatives --config python

- linux系统监控的命令

  - top
  - vmstat：虚拟内存统计
  - w：找到登录的用户
  - uptime：linux系统运行了多久
  - ps：显示系统的进程
  - free：内存的使用情况
  - iostat：CPU平均负载和磁盘活动
  - sar：监控、收集和汇报系统活动
  - mpstat：监控多处理器的使用情况。
  - pmap：监控进程的内存使用情况
  - netstat：linux网络统计监控系统
  - ss：网络统计
  - iptraf：获取实时网络统计信息

- 在磁盘上建立文件的时候需要两个条件：1.磁盘空间，2.需要有inode  任何一个满了都回提示设备没有空间。

  - ``df -h`` 可以查看磁盘剩余空间
  - ``df -ia`` 可以查看磁盘详情，包括剩余的inode数量(还可以创建的文件/文件夹数量）。

- 开启了“嵌套虚拟化”（home archlinux linux）
  - 相关细节：https://wiki.archlinux.org/index.php/KVM_(%E7%AE%80%E4%BD%93%E4%B8%AD%E6%96%87)

技巧
^^^^^^^^^^^^^^^^^^^^^^^
判断进程是否存在，从而可以做预警处理.. 
  脚本内容::

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

增加swap分区
  查看系统是否有交换分区 ::

    sudo swapon --show
    free -h

  使用 ``fallocate`` 命令创建一个交换文件 ::

    sudo fallocate -l 1G /swapfile

  锁定文件的root权限，防止普通用户能够访问这个文件，造成重大安全隐患 ::

    sudo chmod 600 /swapfile

  通过以下命令将文件标记为交换空间 ::

    sudo mkswap /swapfile

  启用该交换文件，让我们的系统开始使用它 ::

    sudo swapon /swapfile

  验证交换空间是否可用 ::

    sudo swapon --show
    free -h

  永久保留交换文件 ::

    # 备份/etc/fstab文件以防出错
    sudo cp /etc/fstab /etc/fstab.bak
    # 将swap文件信息添加到/etc/fstab文件的末尾：
    echo '/swapfile none swap sw 0 0' | sudo tee -a /etc/fstab

  查看当前的swappiness值（默认是60） ::

    cat /proc/sys/vm/swappiness

  设置swappiness的值 ::

    sudo sysctl vm.swappiness=10

  保证这个设置再下次启动中依然生效，我们可以在 ``/etc/sysctl.conf`` 文件中添加一行实现 ::

    vm.swappiness=10

限制chromium的CPU和内存。
  安装 `cgroup-bin` 。

  在文件 `/etc/cgconfig.conf` 加入如下代码： ::

    group browsers {
        cpu {
    #       Set the relative share of CPU resources equal to 25%
            cpu.shares = "256";
        }
        memory {
    #       Allocate at most 1 GB of memory to tasks
            memory.limit_in_bytes = "1G";
    #       Apply a soft limit of 512 MB to tasks
            memory.soft_limit_in_bytes = "768M";
        }
    }

  再在文件 `/etc/cgrules.conf` 中添加如下代码，注意 ``jojeda`` 是用户名。 ::

    # user:process                                          subsystems      group
    jojeda:/usr/lib/chromium-browser/chromium-browser       cpu,memory      browsers

  重启 `cgconfig` 服务 ::

    sudo service cgconfig restart

  如果报 ::

    Failed to start cgconfig.service: Unit cgconfig.service not found.

  你或许还需要修改文件 `/etc/default/grub` 中的 `GRUB_CMDLINE_LINUX_DEFAULT` ::

    GRUB_CMDLINE_LINUX_DEFAULT="cgroup_enable=memory swapaccount=1"

  在手动更新它 ::

    sudo update-grub

  或者这样做：

  运行命令： ::

    # Loads /etc/cgconfig.conf
    cgconfigparser -l /etc/cgconfig.conf
    # Loads /etc/cgrules.conf
    cgrulesengd -vvv --logfile=/var/log/cgrulesengd.log

  然后，给自己写了一个init脚本，在系统启动时加载上面的两个文件。下面的内容存放在文件 `/etc/init.d/cgconf` 中 ::

    #!/bin/sh
    ### BEGIN INIT INFO
    # Provides:          cgconf
    # Required-Start:    $remote_fs $syslog
    # Required-Stop:     $remote_fs $syslog
    # Should-Start:
    # Should-Stop:
    # Default-Start:     2 3 4 5
    # Default-Stop:      0 1 6
    # Short-Description: Configures CGroups
    ### END INIT INFO

    start_service() {
      if is_running; then
        echo "cgrulesengd is running already!"
        return 1
      else
        echo "Processing /etc/cgconfig.conf..."
        cgconfigparser -l /etc/cgconfig.conf
        echo "Processing /etc/cgrules.conf..."
        cgrulesengd -vvv --logfile=/var/log/cgrulesengd.log
        return 0
      fi
    }

    stop_service() {
      if is_running; then
        echo "Stopping cgrulesengd..."
        pkill cgrulesengd
      else
        echo "cgrulesengd is not running!"
        return 1
      fi
    }

    status() {
      if pgrep cgrulesengd > /dev/null; then
        echo "cgrulesengd is running"
        return 0
      else
        echo "cgrulesengd is not running!"
        return 3
      fi
    }

    is_running() {
      status >/dev/null 2>&1
    }

    case "${1:-}" in
      start)
        start_service
        ;;
      stop)
        stop_service
        ;;
      status)
        status
        ;;
      *)
        echo "Usage: /etc/init.d/cgconf {start|stop|restart|status}"
        exit 2
        ;;
    esac

    exit $?

  运行如下命令 ::

    # make the script executable
    chmod 755 /etc/init.d/cgconf

    # register the service
    update-rc.d cgconf defaults

    # start the service
    service cgconf start

    # check the status
    service cgconf status


软件
^^^^^^^^^^^^^^^^^^^^^^^
mutt
:::::::::::::::::::::::
-   安装mutt, msmtp, getmail
-   msmtp的配置文件：/etc/msmtprc::

        account default
        host smtp.163.com
        from name@163.com
        auth login
        tls off
        user name@163.com
        password passwd
        logfile  /var/log/mmlog

-   mutt的配置文件：/etc/Muttrc::

        set realname="yandong"
        set use_from=yes
        set sendmail="/usr/bin/msmtp"   #你的msmtp命令路径
        set editor=vim

-   getmail的配置：

-   发送邮件的命令： ``echo "test" | mutt -s "测试" yandong@zingfront.com``


Sphinx
:::::::::::::::::::::::
- ``sphinx-quickstart`` : 建立源目录及默认配置文件 conf.py。
- ``make html`` ：生成html文档。

docker的命令：
:::::::::::::::::::::::
- 如果你想用你的使用者帳戶(非root帳戶)來使用Docker，把你的帳戶加到Docker的群組中： ``gpasswd -a user docker``
- 記得重新登入來套用新權限，或者你可以用這個指令讓現在的使用者階段套用新群組： ``newgrp docker``
- ``docker ps`` 命令都出错，需要运行一下“docker ps”。
- 运行一个镜像： ``docker run -it base/archlinux:v1.0 /bin/bash``
- 启动镜像： ``docker run -t -i base/archlinux:v1.0 /bin/bash``
- 更新镜像： ``docker commit -m="has update" -a="runoob" 0abdd647ca96 base/archlinux:v1.0``
- 保存一个镜像： ``docker save -o temps/archlinux.image2.tar archlinux:v1``
- 加载一个镜像： ``docker load -i temps/archlinux.image2.tar``

ShadowSocks
:::::::::::::::::::::::
- 启动代理服务的命令： ``ss-local -c /etc/shadowsocks/config.json`` 。
