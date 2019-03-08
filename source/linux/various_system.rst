各种操作系统
===================================

Ubuntu
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. toctree::
   :maxdepth: 2
   :caption: Contents:

安装的软件
:::::::::::::::::::::::::::::
- ``sudo apt install taskwarrior taskd timewarrior``
- ``sudo apt install ^openjdk-8-*``
- ``sudo apt install pip``
- ``sudo apt install keepass2``
- ``sudo apt install shutter``
- ``sudo pip install ydcv``
- ``sudo pip install chromium-bsu chromium-ublock-origin chromium-lwn4chrome flashplugin-installer``
- ``sudo pip install hamster``

异常解决
:::::::::::::::::::::::
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

- `shutter` 工具的编辑功能被禁用。

  - 下载 `libgoocanvas-common <https://launchpad.net/ubuntu/+archive/primary/+files/libgoocanvas-common_1.0.0-1_all.deb>`_, `libgoocanvas3 <https://launchpad.net/ubuntu/+archive/primary/+files/libgoocanvas3_1.0.0-1_amd64.deb>`_, `libgoo-canvas-perl <https://launchpad.net/ubuntu/+archive/primary/+files/libgoo-canvas-perl_0.06-2ubuntu3_amd64.deb>`_ ， 并安装。

日常管理中，用到的命令
:::::::::::::::::::::::::::::
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

- Ubuntu18中，搜狗输入法的“候选栏”乱码。解决方法：

  搜索进程 ``/usr/bin/fcitx`` 的pid，并将其 ``kill`` 。 ::

    ps -aux | grep fcitx
    kill -9 [pid]

  然后重启这个进程： ::

    fcitx

- 查看所有服务状态： ::

    service --status-all

- 命令行向桌面发送通知： ::

    notify-send 'Hello world!' 'This is an example notification.' --icon=dialog-information

  控制弹窗提醒的持续时间（1秒钟）： ::

    notify-send "Notification Title" "The message body is shown here"  --icon=dialog-information -t 1000

  其他信息：

  - xubuntu中，使用 **Xfce4-notifyd** 作为消息的常驻进程，它来接收消息并构造弹窗。

- 在命令行部署定时任务（使用 **at** 命令）： ::

    at now + 1 minutes -f .local/scripts/at/game-notify

  此命令的含义是一分钟后执行文件 ``.local/scripts/at/game-notify`` 中的命令。

  - 分钟： `minutes` ; 小时: `hour` ; 天: `days` ; 周: `weeks` 。
  - 设定在固定的时间： ``at 10:30pm`` ； ``at 23:59 12/31/2018`` 。

ArchLinux
^^^^^^^^^^^^^^

.. toctree::
   :maxdepth: 1

安装的软件
:::::::::::::
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
- 安装博客工具：
  - Sphinx ::

    sudo pip install sphinx

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
:::::::::::::
-   kibana, elasticsearch

开启的服务或命令备忘
::::::::::::::::::::::::::
-   开启了“嵌套虚拟化”（home archlinux linux）
    -   相关细节：https://wiki.archlinux.org/index.php/KVM_(%E7%AE%80%E4%BD%93%E4%B8%AD%E6%96%87)

命令备忘
:::::::::::::
#.  taskwarrior的经典指令：::

        systemctl enable [service name]

    显示所有已启动的服务： ::

        systemctl list-units --type=service

    显示所有服务： ::

        systemctl -a --type=service

#.  软件升级::

        pacman -Syu

#.  数据库查询各命令的执行时间::

        show processlist

#.  判断sql语句执行逻辑::

        explain select 3 from creative_all where channel = 3 and update_status = 3

#.  xfce4的锁屏命令::

        xflock4

#.  启动VNC服务的命令：::

        vncserver

#.  更新mirrorlist的命令::

        sudo reflector --latest 100 --protocol http --protocol https --sort rate --save /etc/pacman.d/mirrorlist

#.  启动Vnc: ``systemctl start x11vnc``

#.  为pacman增加代理。只需要编辑 `/etc/pacman.conf` ::

        XferCommand = /usr/bin/curl --socks5 127.0.0.1:1080 -C - -f %u > %o

名词摘录：
:::::::::::::
-   disable the hot corners (activities button and message tray)
-   软件工程的流程图，有个名词叫：UML
-   图形桌面：KDE、Gnome、Xfce、LXDE

软件备忘
:::::::::::::
1.  Shell OSD
    Place shell notifications in the upper right hand corner.  Version 3 fixes the unclickable screen bug, and doesn't animate on mouse over.
    https://extensions.gnome.org/extension/243/shell-osd/

2.  Hot-Corn-Dog
    This is a GNOME Shell extension where you can pick your own "hot corners" for toggling the overview, or for running custom applications. You can also change the "hot corner" for the message tray. When you install this extension the "hot corner" that toggles the overview moves to the bottom-left corner. Instructions at http://ricsam.github.io/Hot-Corn-Dog/
    https://extensions.gnome.org/extension/309/hot-corn-dog/

3.  Postman：模拟HTTP请求的工具。

4.  UML的推荐软件：umlet, umbrello

备忘记事
:::::::::::::
1.  喜爱的屏保：Galaxy, Flurry, Flow, Flame, Fireworkx, FadPlot, Euler2D, Drift,  Discoball, Crackberg, CloudLife, Boxed, BinaryRing, Atunnel, Atlantis, 

任务备忘
:::::::::::::
1.  听说archlinux的包管理，没有缓存清理的功能。

archlinux知识点
::::::::::::::::::::::::::
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

操作备忘
:::::::::::::
-   ``gradle init --type pom`` ：将 ``pom.xml`` 转化为 ``build.gradle`` 。

Centos使用备忘
^^^^^^^^^^^^^^^

.. toctree::
   :maxdepth: 1

   centos/gcc_update

