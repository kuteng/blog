Ubuntu
===================================
异常解决
^^^^^^^^^^^^^^^^^^^^^^^
- ``error: cannot install "git-todos": snap "git-todos" has "install-snap" change in progress`` 使用命令 ``sudo snap install git-todos`` 解决方案。

  - 使用命令 ``snap changes`` 中到上面报错的安装任务，已知的情况是 `它的状态是Doing` ，如下 ::

      ID   Status  Spawn                 Ready                 Summary
      7    Done    2018-05-21T16:27:31Z  2018-05-21T16:27:50Z  Install "shadowsocks" snap
      8    Done    2018-05-21T16:37:11Z  2018-05-21T16:37:29Z  Install "ssocks" snap
      9    Done    2018-05-21T17:09:06Z  2018-05-21T17:12:11Z  Install "intellij-idea-community" snap
      10   Done    2018-05-21T17:09:18Z  2018-05-21T17:10:41Z  Install "tusk" snap
      11   Done    2018-05-22T02:37:15Z  2018-05-22T02:43:03Z  Auto-refresh snap "core"
      12   Error   2018-05-22T02:40:20Z  2018-05-22T03:05:50Z  Install "okular" snap
      13   Error   2018-05-22T03:00:38Z  2018-05-22T03:01:53Z  Install "git-ubuntu" snap
      14   Doing   2018-05-22T03:01:47Z  -                     Install "git-todos" snap

  - 终止掉这个任务，命令如下 ::

      sudo snap abort 14

日常管理中，用到的命令
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
- 自动切换python2和python3 ::

    sudo update-alternatives --install /usr/bin/python python /usr/bin/python2 100
    sudo update-alternatives --install /usr/bin/python python /usr/bin/python3 150

  开始切换 ::

    sudo update-alternatives --config python

