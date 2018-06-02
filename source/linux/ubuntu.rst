Ubuntu
===================================

.. toctree::
   :maxdepth: 2
   :caption: Contents:

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

- ubuntu版的sublime-text3输入中文问题

  借助工具：sublime-text-imfix

  更新包 ::

    sudo apt-get update && sudo apt-get upgrade

  ``Clone`` 工具的git库 ::

    git clone https://github.com/lyfeyaj/sublime-text-imfix.git

  安装 ``sublime-text-imfix`` ::

    cd sublime-text-imfix
    ./sublime-imfix

- Ubuntu /home下中文目录如何修改成英文？

  命令 ::

    export LANG=en_US
    xdg-user-dirs-gtk-update

  跳出对话框询问是否将目录转化为英文路径,同意并关闭. ::

     export LANG=zh_CN

  关闭终端,并重起.下次进入系统,系统会提示是否把转化好的目录改回中文.选择不再提示,并取消修改.主目录的中文转英文就完成了~
