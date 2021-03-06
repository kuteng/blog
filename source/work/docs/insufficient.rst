工作中的不足
======================================

.. toctree::
   :maxdepth: 1
   :caption: Contents:


常见残余
^^^^^^^^^^^^^^^^^^^^
- 数据中存在多种格式，这样的原因可能是旧的格式无法适应新状况需要新格式（如 ``1`` 、 ``2`` ），但是旧数据量又较大，统一格式需要费一番功夫；也可能是在数据录入过程中程序出现错误（如 ``2`` 、 ``3`` ），但是读写数据对此类情况都默认兼容，这时候就是人懒了。

  +----------------------------------------------------------------------------------------------+---------------------+
  |   id   | target                                                                              | updated_at          |
  +----------------------------------------------------------------------------------------------+---------------------+
  |   1    | ["MYS","BHR","QAT","SAU"]                                                           | 2017-11-23 19:02:54 |
  +----------------------------------------------------------------------------------------------+---------------------+
  |   2    | {"country":["MYS","BHR","QAT","SAU"]}                                               | 2017-11-23 19:02:54 |
  +----------------------------------------------------------------------------------------------+---------------------+
  |   3    | {"country":{"0":"MYS","2":"BHR","3":"QAT","4":"SAU"}}                               | 2017-11-23 19:02:54 |
  +----------------------------------------------------------------------------------------------+---------------------+

- 相似的服务没有整合在一起。如ES数据推送服务，还有一些任务没有整合到此服务中（如： ``images`` 、 ``aggs_day_ads`` 、 ``app`` 等）这样会导致ES服务器过载是，排查问题的不方便。
- 代码变动导致无用代码残余，这样对新人熟悉项目、数据结构调整（删除某个字段）带来困难。残余原因可能是懒，也可能是其他模块正在使用这段代码，他们目前没有经历去修改。解决办法，引入注释 ``@Deprecated`` 然后定期对代码进行清理。
