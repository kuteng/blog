FTP限制用户的访问权限
======================================
目的
^^^^^^^^^^^^^^^^^^^
  1. 普通用户只能访问主目录下的东西。
  2. 特定（指定）用户可以访问根目录。
  3. 这些特定用户所能访问的根目录也各有不同。

相关参数
^^^^^^^^^^^^^^^^^^^
chroot_local_user
  是否将所有用户限制在主目录,YES为启用 NO禁用.(该项默认值是NO,即在安装vsftpd后不做配置的话，ftp用户是可以向上切换到要目录之外的)；

chroot_list_enable
  是否启动限制用户的名单 YES为启用  NO禁用(包括注释掉也为禁用)；

chroot_list_file
  是否限制在主目录下的用户名单，至于是限制名单还是排除名单，这取决于chroot_local_user的值。取值：路径字符串。

local_root
  用户登录的根目录路径。取值：路径字符串

chroot_local_user
  锁定用户到各自目录为其根目录。取值：YES/NO

user_config_dir
  关于用户根目录配置文件的路径。

列表：

+------------------------+---------------------------------+-----------------------------------+
|                        | chroot_local_user=YES           |      chroot_local_user=NO         |
+========================+=================================+===================================+
| chroot_list_enable=YES | 1、所有用户都被限制在其主目录下 | 1、所有用户都不被限制其主目录下   |
|                        | 2、使用chroot_list_file指定的用 | 2、使用chroot_list_file指定的用户 |
|                        | 户列表，这些用户作为"例外"不受  | 列表，这些用户作为"例外"，受到限  |
|                        | 限制                            | 制；                              |
+------------------------+---------------------------------+-----------------------------------+
| chroot_list_enable=NO  | 1、所有用户都被限制在其主目录下 | 1、所有用户都不被限制其主目录下   |
|                        | 2、不使用chroot_list_file指定的 | 2、使用chroot_list_file指定的用户 |
|                        | 用户列表，没有任何"例外"用户    | 列表，没有任何"例外"用户          |
+------------------------+---------------------------------+-----------------------------------+

实际操作
^^^^^^^^^^^^^^^^^^^^^^
1. 编辑文件 `/etc/vsftpd/vsftpd.conf` ： ::

    chroot_local_user=YES
    chroot_list_enable=YES
    chroot_list_file=/etc/vsftpd/chroot_list
    local_root=/data/
    chroot_local_user=YES
    user_config_dir=/etc/vsftpd/userconfig

2. 编辑文件 `vim /etc/vsftpd/chroot_list` ::

    ftpadmin

3. 创建存放“用户更目录”配置文件的文件夹： ::

    mkdir userconfig
    cd userconfig

4. 配置各自用户访问根目录：特定用户都在目录 `userconfig` 下创建同名文件，并做如下编辑。如用户 `ftpadmin` ： ::

    local_root=/data/ftpadmin

5. 结束

其他
^^^^^^^^^^^^^^^^^
- 注意，如果需要对ftp服务器进行写操作的话，需要在文件 ``/etc/vsftpd/vsftpd.conf`` 中设置： ::

    write_enable=YES



