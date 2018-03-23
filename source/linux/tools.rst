常用工具
======================================

.. toctree::
   :maxdepth: 1

Apache
^^^^^^^^^^^^^^^^^^^
不同端口不同项目的配置
#########################
文件： ``/etc/httpd/conf/httpd.conf`` ::

    Listen 9988
    NameVirtualHost *:9988
    <VirtualHost *:9988>
        ServerName 192.168.199.168:9988
        # ServerName 127.0.0.1:9988
        DocumentRoot [web path]
        <Directory "[web path]">
            Options Indexes FollowSymLinks
            AllowOverride All
            Order Deny,Allow
            Allow from all
        </Directory>
    </VirtualHost>

其他

Yii2
^^^^^^^^^^^^^^^^^^^^
查看组建的版本
####################
通过文件 ``[yii2-home]/composer.lock`` 查看

Eclipse
^^^^^^^^^^^^^^^^^^^^^
- properties文件不能显示中文：
    `菜单` -> `Preferences` -> `General` -> `ContentTypes` -> `Text` -> `Java Properties File` ，设置 `Default encoding` ，把下面的 `ISO-8859-1` 改为 `UTF-8` 或者 `GBK` （推荐 `UTF-8` ），然后Update

