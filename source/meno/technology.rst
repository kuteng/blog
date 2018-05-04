技术
===========================================
听说的技术名词
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
- UUID
- serverless
- D3 (Data-Driven Documents)
- TiDB
- MaxCompute
- Spring Cloud Stream
- Apache SkyWalking
- Kubernetes
- Vigil: 一个云服务的监控工具
- lstio: 一个连接、管理和保护为服务的开放平台。
- Kayenta、金丝雀

加密算法的方法名的区分
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
- ``digest()`` —— 对内容签名，不可逆
- ``encode()/decode()`` —— 编解码，需要明确编码类型，一般有默认；可逆
- ``serialize()/deserialize()`` —— 序列化反序列化，一种特殊的编解码形式，需要给定结构；可逆
- ``encrypt()/decrypt()`` —— 加解密，需要给秘钥（公钥、私钥）；可逆

加密相关算法的介绍
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
-   MD5和Base64不是加密算法。MD5算是一种指纹算法，对内容做摘要签名，不可逆。Base64是一种编解码算法，binary和plain text之间的编解码。还有类似的例如Thrift的Protocol层以及Protobuf，也是一种编解码，是有私有协议参照的序列化/反序列化算法，也不是加密算法。
-   典型常用加密算法是AES、RSA这种。ssh-key-gen默认用到的就是RSA了，典型非对称加密。
