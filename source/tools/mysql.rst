MySql
========================
管理命令
^^^^^^^^^^^^^^^
首次安装登录
:::::::::::::::
- 命令： ``sudo cat /etc/mysql/debian.cnf`` ，可以看到用户密码。
- 修改密码，本篇文章将密码修改成 root , 用户可自行定义 ::

    use mysql;
    // 下面这句命令有点长，请注意。
    update mysql.user set authentication_string=password('root') where user='root' and Host ='localhost';
    update user set plugin="mysql_native_password";
    flush privileges;
    quit;

- 重新启动mysql ::

    sudo service mysql restart
    mysql -u root -p // 启动后输入已经修改好的密码：root


常用命令
^^^^^^^^^^^^^^^
修改数据
::::::::::::::::::::::::
::

  UPDATE table_name SET field1=new-value1, field2=new-value2 [WHERE Clause]

重置主键：
:::::::::::::::::::::::::
::

  ALTER TABLE table_name AUTO_INCREMENT= 1;

查询表的数据量
:::::::::::::::::::::::::
表：information_schema.tables，它存储了各个表的统计信息。 ::

  select table_schema, table_name, table_rows, data_length, index_length from information_schema.tables order by table_rows desc;
  select table_schema, table_name, table_rows, data_length, index_length from information_schema.tables where table_schema = 'sp_etl' order by table_rows desc;

查询表的结构
:::::::::::::::::::::::::
::

  desc sp_etl.creative_audience_network;

包括字段注释 ::

  show create table tablename;

查询表的索引
:::::::::::::::::::::::::
::

  show index from sp_etl.creative_audience_network;

查询各语句的执行时间：
:::::::::::::::::::::::::
::

  show processlist

查询各IP的连接数：
:::::::::::::::::::::::::
::

  select count(*) as num
  from (
      select SUBSTR(host, 1, INSTR(host, ':') - 1) as ip, db , command, info
      from  information_schema.processlist
  ) as a;

  select ip, count(*) as num
  from (
      select SUBSTR(host, 1, INSTR(host, ':') - 1) as ip, db , command, info
      from  information_schema.processlist
  ) as a
  group by ip order by num desc;

创建数据库
:::::::::::::::::::::::::
::

  create database if not exists dbname character SET latin1 collate latin1_bin;
  create database if not exists dbname DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;

更改数据库字符编码集
:::::::::::::::::::::::::
::

  ALTER DATABASE db_name DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci

创建用户
:::::::::::::::::::::::::
::

  CREATE USER 'username'@'localhost' IDENTIFIED BY 'passwd';
  CREATE USER 'username'@'%' IDENTIFIED BY 'passwd';

赋予用户权限
:::::::::::::::::::::::::
::

  GRANT ALL ON dbname.* TO 'username'@'localhost';

查看所有用户
:::::::::::::::::::::::::
查看MYSQL数据库中所有用户 ::

  SELECT DISTINCT CONCAT('User: ''',user,'''@''',host,''';') AS query FROM mysql.user;

查看用户权限
:::::::::::::::::::::::::
查看数据库中具体某个用户的权限 ::

  show grants for 'cactiuser'@'%';

删除用户
:::::::::::::::::::::::::
::

  DROP USER 'username'@'localhost';

添加联合唯一约束
:::::::::::::::::::::::::
::

  --修改表时
  ALTER TABLE tabelName ADD UNIQUE KEY(col1, col2);
  --创建表时
  UNIQUE KEY `keyname` (`col1`,`col2`)

修改用户密码
:::::::::::::::::::::::::
::

  use mysql;
  update user set password=password('新密码') where user='root';
  flush privileges;

忘记管理员密码
:::::::::::::::::::::::::
- 在my.ini的[mysqld]字段下面加入： ``skip-grant-tables``
- 重启mysql服务，这时的mysql不需要密码即可登录数据库
- 然后进入mysql: ::

    use mysql;
    update user set password=password('新密码') where user='root';
    flush privileges

初次设置Root密码
:::::::::::::::::::::::::
- ``su`` 命令，将系统切换到root用户。
- ``mysqladmin -uroot -p password 'newpassword'`` 执行此命令，修改密码。
- 在系统反馈： ``Enter password:`` 时， **直接敲回车** 。
- 将系统退出root用户。
- ``systemctl restart mysql`` 重启Mysql服务。


性能优化语句：
:::::::::::::::::::::::::
::

  select table_name, data_length/1000000, index_length/1000000 from information_schema.tables where table_schema = 'sp_etl' and table_name like 'creative%' order by data_length desc, index_length desc;
  select * from information_schema.processlist  where INFO like '%creative_audience_network%' order by TIME;

保留字
^^^^^^^^^
- 字段名不可用词：

  - key
  - desc

- 字段名可用词:

  - id
  - path

