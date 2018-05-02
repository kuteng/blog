PHP
=====================

.. toctree::
   :maxdepth: 1
   :caption: Contents:

语法
^^^^^^^^^^^^^^^
获取毫秒时间戳。
#######################
::
    microtime(true);

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

Yii2
^^^^^^^^^^^^^^^^^^^^^^^
Yii2框架
#######################
- yii\base\Controller的action中，获取从命令行中传入的参数数组 ::

    Yii::$app->controller->module->requestedParams

- 获取 `public action` 的用法： ::

    ./yii help/usage <controller>[/<action>]

  例如： ::

    ./yii help/usage creative/index-entry

  返回的结果： ::

    yii creative/index-entry <configKey> [procType] [isTest]

数据库操作
#######################
直接使用原生SQL语句
:::::::::::::::::::::::
::

    $connection = \Yii::$app->sp_etl;
    $sql = "select p.category
        from sp_profile.advertiser_profile p, sp_etl.app_goods_relation r, sp_etl.ecommerce_goods_detail d
        where r.creative_all_id = {$creative_id} and r.goods_id = d.id and p.store_id = d.merchant_url";
    $datas = $connection->createCommand($sql)->queryAll();

从ActiveRecord获取原生sql
::::::::::::::::::::::::::::::
::

    $query = Salesorder::find()
        ->where(['order_id'=>[1,2,3,4]])
        ->select(['order_id']);
    // get the AR raw sql in YII2
    $commandQuery = clone $query;
    echo $commandQuery->createCommand()->getRawSql();
    exit;

