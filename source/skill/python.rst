Python
=====================

.. toctree::
   :maxdepth: 2
   :caption: Contents:

小技巧
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
- `pid` 操作 ::

    import os;
    # 获取进程pid
    pid=os.getpid()
    # 杀掉进程
    os.kill(pid, signal.SIGQUIT)

- 修改进程名 ::

    from setproctitle import setproctitle
    setproctitle("the new proc's name")

- 内部队列 ::

    from Queue import Queue;

    dict_queue = Queue();
    newdict = {"key": "keyname", "value": "value body"};
    dict_queue.put(newdict);
    queue_size = dict_queue.qsize();
    dict_item = dict_queue.get();

- 根据文件包名，加载python文件。 ::

    source_module = __import__("plog.source.%s" % source_module_name,fromlist=["plog.source"]);
    # 使用加载后的结果。source是其内部的class
    source_iter = source_module.source(source_dict=conf_dict["source"]).yield_line()

- 读取文件的多行 ::

    file = open("filepath");

    while 1:
        line_list = file.readlines();

        if not line_list:
            break;

  其中 ``readlines`` 并非一次性将文件的所有内容都读出来。而是更具系统缓存的大小一次次往外读。

常用方法
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
- 分割文件名与扩展名： ``os.path.splitext()``
- 去掉文件名，返回目录所在的路径： ``os.path.dirname()``
- 去掉目录的路径，只返回文件名： ``os.path.basename()``

常用的第三方模块
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
- 信号模块： ``signal``
- 命令行解析模块： ``optparse``

一个持久话的module是shelve。
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
::

        import shelve
        s = shelve.open('test_shelf.db')
        try:
            s['key1'] = { 'int': 10, 'float':9.5, 'string':'Sample data' }
        finally:
            s.close();

据库连接池
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
python2
###############
代码： ::

    from twisted.enterprise import adbapi;
    dbparams=dict(
        host=settings['MYSQL_HOST'],#读取settings中的配置
        db=settings['MYSQL_DBNAME'],
        user=settings['MYSQL_USER'],
        passwd=settings['MYSQL_PASSWD'],
        charset='utf8',#编码要加上，否则可能出现中文乱码问题
        cursorclass=MySQLdb.cursors.DictCursor,
        use_unicode=False,
    )
    #**表示将字典扩展为关键字参数,相当于host=xxx,db=yyy....
    dbpool=adbapi.ConnectionPool('MySQLdb',**dbparams)
    d = self.dbpool.runInteraction(self.update_feed_seen_ids)
    d.addErrback(self._database_error)

python3
###############
- 安装： ``sudo pip install PyMySQL``
- 代码： ::

    import pymysql
    # 打开数据库连接
    db = pymysql.connect("localhost","testuser","test123","TESTDB" )

    # 使用 cursor() 方法创建一个游标对象 cursor
    cursor = db.cursor()

    # 使用 execute()  方法执行 SQL 查询
    cursor.execute("SELECT VERSION()")

    # 使用 fetchone() 方法获取单条数据.
    data = cursor.fetchone()

    print ("Database version : %s " % data)

    # 关闭数据库连接
    db.close()

ORM
^^^^^^^^^^^^^^^^^^^^^^^^^^^^
- peewee
  - 安装： ``pip install peewee``
  - 资料：http://docs.peewee-orm.com/en/latest
- SQLAlchemy
- Django's ORM
- Storm
- SQLObject
- 参考 URL: http://blog.csdn.net/permike/article/details/52173757

json操作：
^^^^^^^^^^^^^^^^^^^^^^^^^^^^
::

    import codecs;
    import json
    line = json.dumps(dict(item)) + "\n"#转为json的
    self.file = codecs.open('info.json', 'w', encoding='utf-8')#保存为json文件
    self.file.write(line)#写入文件中
    self.file.close()

::

    import json
    # fp:文件句柄,
    # ensure_ascii: 设置为False的话才可以把中文以中文的形式存到文件里，否则会是’\xXX\xXX’这种,
    # indent: 缩进量，为0时不格式化
    json.dumps(obj, fp, ensure_ascii, indent=4);
    json.loads(str);

安装MySQLdb
^^^^^^^^^^^^^^^^^^^^^^^^^^^^
::

    # python2
    sudo pip install mysql-python
    # python3
    sudo pip install mysqlclient

安装ConfigParser
^^^^^^^^^^^^^^^^^^^^^^^^^^^^
::

    sudo pip install configparser

获取环境变量
^^^^^^^^^^^^^^^^^^^^^^^^^^^^
::

    import os;
    print(os.environ["TEMP"]);
    os.environ["MYDIR"] = mydir;

md5
^^^^^^^^^^^^^^^^^^^^^^^^^^^^
python2
##########
::

    import md5
    sign = "the string";
    m1 = md5.new()
    m1.update(sign)
    print(m1.hexdigest());

python3
#############
::

    import hashlib;
    pwd = 'zhonghui123'
    m2 = hashlib.md5()
    #参数必须是byte类型，否则报Unicode-objects must be encoded before hashing错误
    m2.update(pwd.encode("utf-8")
    print(m2.hexdigest())

    # 中文字符
    data='我是'
    m = hashlib.md5(data.encode(encoding='gb2312'))
    print(m.hexdigest())

urllib常用方法小结
^^^^^^^^^^^^^^^^^^^^^^^^^^^^
python2
################
::

    import urllib$
    urlQuery = urllib.quote(q)

python3
################
::

    from urllib import parse;
    url = r'https://docs.python.org/3.5/search.html?q=parse&check_keywords=yes&area=default';
    parseResult = parse.urlparse(url);
    param_dict = parse.parse_qs(parseResult.query)

    query = {"name": "walker", "age": 99};
    parse.urlencode(query)

    parse.quote('a&b/c') #未编码斜线
    parse.quote_plus('a&b/c')  #编码了斜线
    parse.unquote('1+2')  #不解码加号
    parse.unquote('1+2')  #把加号解码为空格

导入第三方模块：
^^^^^^^^^^^^^^^^^^^^^^^^^^^^
维护环境变量PYTHONPATH的值。

报错的解决方案
^^^^^^^^^^^^^^^^^^^^^^^^^^^^
-   Command "python setup.py egg_info"  ::

        sudo python -m pip install --upgrade --force pip
        sudo pip install setuptools==33.1.1

-   其他
