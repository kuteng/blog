借助网络资源搭建共享taskwarrior server
==============================================

第一步：保证本机的task的版本为2.3.0以上。 ::

    task --version

第二步下载下面的文件 ::

   https://freecinc.com/download/freecinc_33b1ad4b.key.pem
   https://freecinc.com/download/freecinc_33b1ad4b.cert.pem
   https://freecinc.com/download/freecinc_33b1ad4b.ca.pem

将他们放到目录 `~/.task` 下。

注意此文件的文件名是随机生成的（每次访问 `FreeCinc的公用Taskserver资源`_ ），但如果要保证不同机器上使用共同账户，就需要保证文件名一样了。

第三步配置，执行下面几行命令： ::

    task config taskd.server freecinc.com:53589
    task config taskd.key ~/.task/freecinc_33b1ad4b.key.pem
    task config taskd.certificate ~/.task/freecinc_33b1ad4b.cert.pem
    task config taskd.ca ~/.task/freecinc_33b1ad4b.ca.pem
    task config taskd.credentials -- 'FreeCinc/freecinc_33b1ad4b/a5dfd125-c2a4-4649-9d6a-6e3fed36d8c3'

同样，每个配置的参数是与第二步中随机生成的文件名匹配的。

第四步校验。运行下面的命令： ::

    task diagnostics

这样可以并确认：输出CA，证书和密钥都是“可读”的；还确保您拥有服务器的价值和信誉的价值。

第五步同步。执行下面的命令： ::

    task sync init

第六步分部署。在其他机器上再次重复第一到第五步，注意第二步、第三步的内容需要相同。

注意第二步中的三个文件已经保存在此教程中了。（此内容只是我本人的东西）。

参考网页: `FreeCinc的公用Taskserver资源`_

.. _FreeCinc的公用Taskserver资源: https://www.cnblogs.com/shoshana-kong/p/9066888.html
