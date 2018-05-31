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

- 修改用户密码

  修改用户自己的密码： ::

    passwd

  修改其他用户的密码： ::

    passwd othername

