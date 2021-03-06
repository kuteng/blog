《HTTPS权威指南》
===================================================================
.. toctree::
   :maxdepth: 1

HTTPS权威指南：在服务器和Web应用上部署SSL/TLS和PKI

新名词
^^^^^^^^^^^^^^^^^^^^
- 应用层、表示层、会话层
- TCP、SSL/TLS、SMTP、IMAP
- 序列密码、RC4加密、分组密码(block cipher)、分组密码模式、高级加密标准、填充(padding)、散列函数、散列、
- 消息验证代码（message authentication code，MAC）、使用密钥的散列（keyed-hash）、基于散列的消息验证代码（hash-based message authentication code，HMAC）
- 电码本（electronic codebook，ECB）模式、加密块链接（cipher block chaining，CBC）模式、GCM
- 非对称加密（asymmetric encryption）、数字签名。
- 真随机数生成器
- 机密性、完整性和真实性

备忘
^^^^^^^^^^^^^^^^^^^
- 散列函数可以用于验证数据完整性
- 散列函数经常被称为指纹、消息摘要，或者简单称为摘要。
- 消息签名的过程，还是不太明白。

临时问题
^^^^^^^^^^^^^^^^^^^
- 加密块链接模式如何将IV传送给接收端？
- 为什么会这样： `对称加密不能用于访问安全数据的无人系统。因为使用相同的密钥可以反转整个过程，这样的系统出现任何问题都会影响到存储在系统中的所有数据` ？
- 非对称加密是如何实现的？
- 能否通过公钥推测出私钥？

摘录
^^^^^^^^^^^^^^^^^^^^^
之所以说随机数不易生成，是因为计算机是十分善于预测的，它们会严格按照指令执行。如果告诉它生成一个随机数，它很可能做不好这项工作 。真正的随机数只能通过观测特定的物理处理器才能得到。没有的话，计算机将关注于收集少量的熵

yandong
