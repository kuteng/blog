Apache2 http server
=====================

.. toctree::
   :maxdepth: 3
   :caption: Contents:

本文的总结基本来自于Ubuntu上的apache使用经验，这一点请特别注意一下。

入门
^^^^^^^^^^^^^
在其他端口，引入其他目录的项目
#################################
-   在 `/etc/apache2/ports.conf` 文件中， ``Listen 80`` 下面增加一行 ::

        Listen 8080

-   在文件 `/etc/apache2/apache2.conf` 中，对应位置增加如下代码 ::

        <Directory "/home/username/folder1/folder2/target_folder">
            Options Indexes FollowSymLinks
            AllowOverride None
            Require all granted
        </Directory>

    具体位置，可以考虑在下面这段代码附近添加 ::

        <Directory />
            Options FollowSymLinks
            AllowOverride None
            Require all denied
        </Directory>

-   在文件 `/etc/apache2/sites-available/000-default.conf` 中，合适位置增加如下代码 ::

        <VirtualHost *:8080>
            DocumentRoot /home/username/folder1/folder2/target_folder
        </VirtualHost>

    具体位置，可以参考下面的这段代码 ::

        <VirtualHost *:80>
            ServerAdmin webmaster@localhost
            DocumentRoot /var/www/html
            ErrorLog ${APACHE_LOG_DIR}/error.log
            CustomLog ${APACHE_LOG_DIR}/access.log combined
        </VirtualHost>
