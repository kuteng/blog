Apache Tomcat
=====================

.. toctree::
   :maxdepth: 3
   :caption: Contents:

背景
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
- 系统：Ubuntu
- 版本：8

入门
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
Help
######################
- 打开网址 `http://127.0.0.1:8080/` 可以看到入门帮助。这个入门版主的页面源码路径是 `/var/lib/tomcat8/webapps/ROOT/index.html` ，如果后期去掉了 `ROOT` 项目，可以直接找到这个页面。

备忘
######################
- 安装war项目，可以直接将war文件拷贝到 `/var/lib/tomcat8/webapps` 。
- 重启tomcat服务： ``systemctl restart tomcat8``
- 启动之前，需要创建目录 `/var/lib/tomcat8/jspwiki-files` 。否则 `wiki` 会启动失败。

问题解决
######################
- Tomcat启动很慢，现象。在log ::

    28-Jun-2018 17:40:35.175 INFO [localhost-startStop-1] org.apache.jasper.servlet.TldScanner.scanJars At least one JAR was scanned for TLDs yet contained no TLDs. Enable debug logging for this logger for a complete list of JARs that were scanned but no TLDs were found in them. Skipping unneeded JARs during scanning can improve startup time and JSP compilation time.

  出来的几分钟之后再会出下面的log ::

    28-Jun-2018 17:44:01.071 WARNING [localhost-startStop-1] org.apache.catalina.util.SessionIdGeneratorBase.createSecureRandom Creation of SecureRandom instance for session ID generation using [SHA1PRNG] took [205,825] milliseconds.

  然后Tomcat才启动起来。

  解决方法：

  打开$JAVA_PATH/jre/lib/security/java.security这个文件，找到下面的内容： ::

    securerandom.source=file:/dev/random
  
  替换成 ::
  
    securerandom.source=file:/dev/urandom

- 访问不了页面 ``[IP]:8080/manager/html`` 

  修改文件 `conf/tomcat-users.xml` 后，如果是远程访问这个页面还需要将文件 `[tomcat]/webapps/manager/META-INF/context.xml` 下的这一行注释掉： ::

      <Valve className="org.apache.catalina.valves.RemoteAddrValve"
         allow="127\.\d+\.\d+\.\d+|::1|0:0:0:0:0:0:0:1" />

