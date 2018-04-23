ArchLinux
==============

.. toctree::
   :maxdepth: 1

安装的软件
^^^^^^^^^^^^^
- 有道词典。命令：ydcv
- 任务管理工具。命令：task，官网：https://taskwarrior.org，软件名：taskwarrior
  -   查看task的帮助信息的方式是：man task
- 笔记工具pynote。运行指令是note。安装的方式：pip install pynote。—— https://pypi.python.org/pypi/pynote
- 向内网发送邮件，需要安装mailutils
- 安装yaourt，以便安装AUR上的软件。
- 使用makepkg命令的方式，安装AUR上的软件。
- ntfs-3g，安装ntfs的格式化工具
- 番茄时间：gnome-pomodoro: http://gnomepomodoro.org/
- linux软件市场：https://extensions.gnome.org/
- 绘图软件：YED
- php的包管理：composer。官方描述：Dependency Manager for PHP
- 抓包工具：wireshark
- 截图工具：deepin-screenshot
- dialog命令，可以在控制台上绘制“界面”。
- 锁屏软件：light-locker, xscreensaver
- 安装了docker
- 网络工具：pacman -S net-tools dnsutils inetutils iproute2
- 安装了pynote，运行指令是note。—— https://pypi.python.org/pypi/pynote
- 安装了sdk：http://sdkman.io/install.html
- 桌面提示的工具：

  - kdialog ::

      kdialog --dontagain myscript:nofilemsg --msgbox "File: '~/.backup/config' not found."

  - zenity
  - Dialog: 控制台显示UI。 ::

      #!/bin/bash
      dialog --title "Delete file" \
      --backtitle "Linux Shell Script Tutorial Example" \
      --yesno "Are you sure you want to permanently delete \"/tmp/foo.txt\"?" 7 60

      # Get exit status
      # 0 means user hit [yes] button.
      # 1 means user hit [no] button.
      # 255 means user hit [Esc] key.
      response=$?
      case $response in
         0) echo "File deleted.";;
         1) echo "File not deleted.";;
         255) echo "[ESC] key pressed.";;
      esac

  - gmessage - 基于 GTK xmessage 的克隆
  - xmessage - 在窗口中显示或询问消息（基于 X 的 /bin/echo）
  - whiptail - 显示来自 shell 脚本的对话框
  - python-dialog - 用于制作简单文本或控制台模式用户界面的 Python 模块

- logger 命令将信息写到系统日志文件

安装的服务
^^^^^^^^^^^^^
-   kibana, elasticsearch

开启的服务或命令备忘
^^^^^^^^^^^^^^^^^^^^^^^^^^
-   开启了“嵌套虚拟化”（home archlinux linux）
    -   相关细节：https://wiki.archlinux.org/index.php/KVM_(%E7%AE%80%E4%BD%93%E4%B8%AD%E6%96%87)
-   启动代理服务的命令： ``ss-local -c /etc/shadowsocks/config.json`` 。

命令备忘
^^^^^^^^^^^^^
#.  taskwarrior的经典指令：::

        systemctl enable [service name]

    显示所有已启动的服务： ::

        systemctl list-units --type=service

    显示所有服务： ::

        systemctl -a --type=service

#.  taskwarrior的经典指令：::

        task add "ES-深入搜索-近似匹配" due:tomorrow proj:work pri:H +es depends:3
        task [ID] modify due:3day
        task [ID] modify desc:《教程》-深入搜索-近似匹配

        task [ID] start 激活任务
        task active 查看激活的任务。
        task [ID] done 将任务设置为完成。
        task completed 查看已经完成的任务。

#.  向其他用户发送消息::

        write [用户名] [ttyname(例如pts/1)]
        write all
        wall

#.  判断用户是否存在::

        id peter >& /dev/null
        if [ $? -ne 0 ];then
            echo "don't have the user"
        fi

#.  创建目录及子目录。::

        mkdir -p folder1/folder2/folder3

#.  软件升级::

        pacman -Syu

#.  进程搜索::

        ps -d -opid,etime,args | grep "$1" | grep -v grep

#.  数据库查询各命令的执行时间::

        show processlist

#.  判断sql语句执行逻辑::

        explain select 3 from creative_all where channel = 3 and update_status = 3

#.  显示文件peter.note的第5行到第9行的内容。::

        sed -n "5,9p" peter.note

#. 对数据结果进行统计<br/>
    wc命令：统计指定文件中的字节数、字数、行数，并将统计结果显示输出。
    例如：统计ps结果的行数::

        ps -aux "sync/" | wc -l

#. 使用更简洁的搜索文件命令：::

        mlocate

#. xfce4的锁屏命令::

        xflock4

#. 启动VNC服务的命令：::

        vncserver

#. 更新mirrorlist的命令::

        sudo reflector --latest 100 --protocol http --protocol https --sort rate --save /etc/pacman.d/mirrorlist

#. 启动Vnc: ``systemctl start x11vnc``

#. linux系统监控的命令

    -   top
    -   vmstat：虚拟内存统计
    -   w：找到登录的用户
    -   uptime：linux系统运行了多久
    -   ps：显示系统的进程
    -   free：内存的使用情况
    -   iostat：CPU平均负载和磁盘活动
    -   sar：监控、收集和汇报系统活动
    -   mpstat：监控多处理器的使用情况。
    -   pmap：监控进程的内存使用情况
    -   netstat：linux网络统计监控系统
    -   ss：网络统计
    -   iptraf：获取实时网络统计信息

名词摘录：
^^^^^^^^^^^^^
-   disable the hot corners (activities button and message tray)
-   软件工程的流程图，有个名词叫：UML
-   图形桌面：KDE、Gnome、Xfce、LXDE

软件备忘
^^^^^^^^^^^^^
1.  Shell OSD
    Place shell notifications in the upper right hand corner.  Version 3 fixes the unclickable screen bug, and doesn't animate on mouse over.
    https://extensions.gnome.org/extension/243/shell-osd/

2.  Hot-Corn-Dog
    This is a GNOME Shell extension where you can pick your own "hot corners" for toggling the overview, or for running custom applications. You can also change the "hot corner" for the message tray. When you install this extension the "hot corner" that toggles the overview moves to the bottom-left corner. Instructions at http://ricsam.github.io/Hot-Corn-Dog/
    https://extensions.gnome.org/extension/309/hot-corn-dog/

3.  Postman：模拟HTTP请求的工具。

4.  UML的推荐软件：umlet, umbrello

5.  docker的命令：

    -   如果你想用你的使用者帳戶(非root帳戶)來使用Docker，把你的帳戶加到Docker的群組中： ``gpasswd -a user docker``
    -   記得重新登入來套用新權限，或者你可以用這個指令讓現在的使用者階段套用新群組： ``newgrp docker``
    -   ``docker ps`` 命令都出错，需要运行一下“docker ps”。
    -   运行一个镜像： ``docker run -it base/archlinux:v1.0 /bin/bash``
    -   启动镜像： ``docker run -t -i base/archlinux:v1.0 /bin/bash``
    -   更新镜像： ``docker commit -m="has update" -a="runoob" 0abdd647ca96 base/archlinux:v1.0``
    -   保存一个镜像： ``docker save -o temps/archlinux.image2.tar archlinux:v1``
    -   加载一个镜像： ``docker load -i temps/archlinux.image2.tar``

6.  Sphinx用法

    -   ``sphinx-quickstart`` : 建立源目录及默认配置文件 conf.py。
    -   ``make html`` ：生成html文档。

常用命令
^^^^^^^^^^^^^
1.  wget使用代理下咋文件： ``wget -c -r -np -k -L -p -e "http_proxy=http://127.0.0.1:8087" [URL]``
2.  注销当前用户： ``pkill X``
3.  查看端口占用情况： ``netstat –apn | grep 1000``

备忘记事
^^^^^^^^^^^^^
1.  喜爱的屏保：Galaxy, Flurry, Flow, Flame, Fireworkx, FadPlot, Euler2D, Drift,  Discoball, Crackberg, CloudLife, Boxed, BinaryRing, Atunnel, Atlantis, 

任务备忘
^^^^^^^^^^^^^
1.  听说archlinux的包管理，没有缓存清理的功能。

archlinux知识点
^^^^^^^^^^^^^^^^^^^^^^^^^^
1.  修改archlinux的源：/etc/pacman.d/mirrorlist, /etc/pacman.conf
2.  pacman 常用命令

    ==========================  ====================================================
    命令                        备注
    ==========================  ====================================================
    pacman -Sy abc                    和源同步后安装名为abc的包
    pacman -S abc                     从本地数据库中得到abc的信息，下载安装abc包
    pacman -Sf abc                    强制安装包abc
    pacman -Ss abc                   搜索有关abc信息的包
    pacman -Si abc                    从数据库中搜索包abc的信息
    pacman -Syu                        同步源，并更新系统
    pacman -Sy                          仅同步源
    pacman -R abc                     删除abc包
    pacman -Rc abc                   删除abc包和依赖abc的包
    pacman -Rsn abc                 移除包所有不需要的依赖包并删除其配置文件
    pacman -Sc                          清理/var/cache/pacman/pkg目录下的旧包
    pacman -Scc                        清除所有下载的包和数据库
    pacman -Sd abc                   忽略依赖性问题，安装包abc
    pacman -Su --ignore foo       升级时不升级包foo
    pacman -Sg abc                   查询abc这个包组包含的软件包
    pacman -Q                           列出系统中所有的包
    pacman -Q package             在本地包数据库搜索(查询)指定软件包
    pacman -Qi package            在本地包数据库搜索(查询)指定软件包并列出相关信息
    pacman -Q | wc -l                  统计当前系统中的包数量
    pacman -Qdt                         找出孤立包
    pacman -Rs $(pacman -Qtdq)   删除孤立软件包（递归的,小心用)
    pacman -U   abc.pkg.tar.gz      安装下载的abs包，或新编译的本地abc包
    pacman-optimize && sync        提高数据库访问速度
    pacman -Syu yaourt           更新源
    ==========================  ====================================================

3.  声音控制，需要安装：alsa和alsa-utils。alsa-utils中包含工具alsamixer。

    ==========================  ====================================================
    指令                        备注
    ==========================  ====================================================
    amixer set Master 5%+       增加音量:
    amixer set Master 5%-       减小音量:
    amixer set Master toggle    静音/解静音:
    ==========================  ====================================================

    音量可视化插件：xfce4-pulseaudio-plugin

4.  命令行进行wifi连接

    ==========================================  ============================================================================================
    备注                                        指令
    ==========================================  ============================================================================================
    查看可用wifi                                nmcli device wifi
    连接 WiFi 网络                              nmcli dev wifi connect <name> password <password>
    通过指定接口（如wlan1接口)连接 WiFi 网络    nmcli dev wifi connect <name> password <password> iface wlan1 [profile name]
    断开一个接口                                nmcli dev disconnect iface eth0
    重新连接一个标记为已断开的接口              nmcli con up uuid <uuid>
    获得 UUID 列表                              nmcli con show
    查看网络设备及其状态列表                    nmcli dev
    关闭 WiFi                                   nmcli r wifi off
    ==========================================  ============================================================================================

    此外，nmtui可以启动图形化的控制台界面，进行网络连接。

5.  调节屏幕亮度，可以直接编辑文件：/sys/class/backlight/intel_backlight/brightness，最大值为1000
    除此之外，可以安装“xorg-xbacklight”，运行如下命令进行设置：

    ===========================  ============================================================================================
    备注                                        指令
    ===========================  ============================================================================================
    设定指定亮度（最大值为100）   xbacklight -set 60
    增加10%                       xbacklight -inc 10
    减少10%                       xbacklight -dec 10
    ===========================  ============================================================================================

运维备忘
^^^^^^^^^^^^^
- 在磁盘上建立文件的时候需要两个条件：1.磁盘空间，2.需要有inode  任何一个满了都回提示设备没有空间。

  - ``df -h`` 可以查看磁盘剩余空间
  - ``df -ia`` 可以查看磁盘详情，包括剩余的inode数量(还可以创建的文件/文件夹数量）。

操作备忘
^^^^^^^^^^^^^
-   ``gradle init --type pom`` ：将 ``pom.xml`` 转化为 ``build.gradle`` 。

安装mutt
^^^^^^^^^^^^^
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

