Linux
===================================

.. toctree::
   :maxdepth: 1
   :caption: Contents:

常用命令
^^^^^^^^^^^^^^^^^^^^^
- 查看内存信息

  查看内存的插槽数,已经使用多少插槽.每条内存多大，已使用内存多大 ::

    sudo dmidecode | grep -P -A5 "Memory\s+Device" | grep Size | grep -v Range

  结果例如 ::

    Size:2048MB  
    Size:2048MB  
    Size:NoModuleInstalled  
    Size:NoModuleInstalled  
    Size:NoModuleInstalled  
    Size:NoModuleInstalled  
    Size:NoModuleInstalled  
    Size:NoModuleInstalled  

  查看内存支持的最大内存容量 ::

    sudo dmidecode | grep -P 'Maximum\s+Capacity'

  结果 ::

    MaximumCapacity:64GB  

  查看内存的频率 ::

    sudo dmidecode | grep -P -A16 "Memory\s+Device" | grep 'Speed'

  结果 ::

    Speed:667MHz(1.5ns)  
    Speed:667MHz(1.5ns)  
    Speed:667MHz(1.5ns)  
    Speed:667MHz(1.5ns)  
    Speed:667MHz(1.5ns)  
    Speed:667MHz(1.5ns)  
    Speed:667MHz(1.5ns)  
    Speed:667MHz(1.5ns)  

- 注册新用户

  - 创建用户 ::

      useradd testuser

    注意：新创建用户会在 `/home` 下创建一个用户目录 `testuser`
  - 修改密码 ::

      paswd testuser

  - 修改用户这个名利的相关参数 ::

      usermod --help

  - 删除用户 ::

      userdel testuser

  - 删除用户的主目录 ::

      rm -rf /home/testuser

  - 新建用户tab无法补全命令 

    - 通过 ``cat /ect/passwd`` ，可知这个用户的shell是 ``/bin/sh``
    - 修改为 ``/bin/bash`` 即可


  - 赋予 `sudo` 权限：

    编辑文件 ``/etc/sudoers`` ，添加一行 ::

      test   ALL=(ALL:ALL) ALL

    或者修改为这样应该也可以 ::

      test   ALL=(root:root) ALL

- 修改用户密码

  修改用户自己的密码： ::

    passwd

  修改其他用户的密码： ::

    passwd othername

