安装创建快捷方式
======================================

.. toctree::
   :maxdepth: 4

以为 `eclipse` 创建快捷方式为例。

- 进入 ``/usr/share/applications`` 文件夹.该文件夹就相当于Windows上的快捷方式。可以用 ls 查看一下都是以 ``.desktop`` 结尾的文件。
- 在此文件夹创建一个eclipse的快捷方式。 ``sudo vim eclipse.desktop``
- 文件的内容是： ::

    [Desktop Entry]
    Encoding=UTF-8
    Name=Eclipse
    Comment=Eclipse IDE
    Exec=/usr/local/eclipse/eclipse(eclipse存放路径)
    Icon=/usr/local/eclipse/icon.xpm(eclipse存放路径)
    Terminal=false
    Type=Application
    Categories=GNOME;Application;Development;
    StartupNotify=true

- 我们可以看到在 ``/usr/share/applacations/`` 文件夹下有了eclipse的快捷方式
- 最后把eclipse快捷方式复制到桌面就可以启动了！
