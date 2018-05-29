Ubuntu
===================================
异常解决
^^^^^^^^^^^^^^^^^^^^^^^
- ``error: cannot install "git-todos": snap "git-todos" has "install-snap" change in progress`` 使用命令 ``sudo snap install git-todos`` 解决方案。

  - 使用命令 ``snap changes`` 中到上面报错的安装任务，已知的情况是 `它的状态是Doing` ，如下 ::

      ID   Status  Spawn                 Ready                 Summary
      7    Done    2018-05-21T16:27:31Z  2018-05-21T16:27:50Z  Install "shadowsocks" snap
      8    Done    2018-05-21T16:37:11Z  2018-05-21T16:37:29Z  Install "ssocks" snap
      9    Done    2018-05-21T17:09:06Z  2018-05-21T17:12:11Z  Install "intellij-idea-community" snap
      10   Done    2018-05-21T17:09:18Z  2018-05-21T17:10:41Z  Install "tusk" snap
      11   Done    2018-05-22T02:37:15Z  2018-05-22T02:43:03Z  Auto-refresh snap "core"
      12   Error   2018-05-22T02:40:20Z  2018-05-22T03:05:50Z  Install "okular" snap
      13   Error   2018-05-22T03:00:38Z  2018-05-22T03:01:53Z  Install "git-ubuntu" snap
      14   Doing   2018-05-22T03:01:47Z  -                     Install "git-todos" snap

  - 终止掉这个任务，命令如下 ::

      sudo snap abort 14

日常管理中，用到的命令
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
- 自动切换python2和python3 ::

    sudo update-alternatives --install /usr/bin/python python /usr/bin/python2 100
    sudo update-alternatives --install /usr/bin/python python /usr/bin/python3 150

  开始切换 ::

    sudo update-alternatives --config python

- 增加swap分区

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

- 限制chromium的CPU和内存。

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

