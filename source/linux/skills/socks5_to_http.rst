linux下使用privoxy将socks转为http代理
======================================

.. toctree::
   :maxdepth: 4

环境：ubuntu 15.04

工具：shadowsocks（socks5），privoxy（http）

privoxy有将socks代理转为http代理的功能。

1. 开启shadowsocks，socks代理地址为127.0.0.1:1080。
2. 安装privoxy。 ::

   $ sudo apt-get install privoxy

3. 更改provoxy配置，位置在“/etc/privoxy/config”。 ::

    $ sudo vim /etc/privoxy/config

  在里面添加一条： ::

    # 在 froward-socks4下面添加一条socks5的，因为shadowsocks为socks5，
    # 地址是127.0.0.1:1080。注意他们最后有一个“.”
    #        forward-socks4   /               socks-gw.example.com:1080  .
    forward-socks5   /               127.0.0.1:1080 .

    # 下面还存在以下一条配置，表示privoxy监听本机8118端口，
    # 把它作为http代理，代理地址为 http://localhost.8118/ 。
    # 可以把地址改为 0.0.0.0:8118，表示外网也可以通过本机IP作http代理。
    # 这样，你的外网IP为1.2.3.4，别人就可以设置 http://1.2.3.4:8118/ 为http代理。
    listen-address localhost:8118

4. 然后重启privoxy。 ::

    $ sudo systemctl restart privoxy.serivce

5. 现在咱们就可以使用http代理了，如果要给系统设置http代理，就在~/.bashrc里添加一条http_proxy配置。 ::

    $ vim ~/.bashrc

  添加： ::

    export http_proxy=http://127.0.0.1:8118/

  然后使用source是它立刻生效。 ::

    $ source ~/.bashrc

