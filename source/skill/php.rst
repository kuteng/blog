PHP
=====================

.. toctree::
   :maxdepth: 2
   :caption: Contents:

语法
^^^^^^^^^^^^^^^
数组去重
#######################
::

    array_values(array_unique(arr));

数组转化为字符串
#######################
::

    implode($arr,"-")

字符串转化为数组
#######################
::

    explode(separator,$string);

查看数据库对象的属性有那些修改过
##############################################
::

    foreach($ad->getDirtyAttributes() as $key => $value) {
        Yii::trace(">>> $key: " . $ad->getOldAttribute($key)
            . ": " . $ad->getAttribute($key), __METHOD__);
    }

关于进程/系统信息的函数。
##############################################
::
    getmypid(): 获取当前进程的PID（int），如果返回false表示失败。
    getmygid()：
    getmyuid()
    get_current_user()
    getmyinode()
    getlastmod()

进入交互模式
#######################
::

    php -a

如果显示的内容是`Interactive mode enabled`，表示其禁用交互模式。这是，你输入的内容需要以“<?php”开头，结束之后需要按组合键“Ctrl+D”。

判断数据库表中是否存在某个字段
##############################################
::

    var_dump(empty("fix_geo_status", $tableModel->attributes()));
    var_dump($tableModel->tableColumn);

数组操作
#######################
- php中有两个函数用来判断数组中是否包含指定的键，分别是 ``array_key_exists`` 和 ``isset`` ::

    array_key_exists($key, $array)
    isset($array[$key])

Yii2框架
#######################
yii\base\Controller的action中，获取从命令行中传入的参数数组 ::

    Yii::$app->controller->module->requestedParams
