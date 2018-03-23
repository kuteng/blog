Git用法
=====================

.. toctree::
   :maxdepth: 1

Git代理的设置方法
^^^^^^^^^^^^^^^^^^^^^
设置： ::

    git config --local https.proxy http://127.0.0.1:1080
    git config --local http.proxy 'socks5://127.0.0.1:1080'

解除： ::

    git config --global --unset https.proxy

集成vimdiff
^^^^^^^^^^^^^^^^^^^^^
::

    git config --global diff.tool vimdiff
    git config --global difftool.prompt false
    git config --global alias.d difftool

中文问题
^^^^^^^^^^^^^^^^^^^^^
解决 ``git status`` 不能显示中文的问题。 ::

    git config --global core.quotepath false

生成公钥
^^^^^^^^^^^^^^^^^^^^^
::

    git config --global user.name "username"
    git config --global user.email "user@email"
    ssh-keygen -t rsa -C “user@email”

更新远程分支列表
^^^^^^^^^^^^^^^^^^^^^
::

    git remote update origin --prune

查看log
^^^^^^^^^^^^^^^^^^^^^
::

    git log --after=2017-03-10 --author=[name]  //查看2017-03-10以后，用户名的提交
    git log --author=[name] --after=2017-03-10  //查看2017-03-10以后，用户名的提交
