Spring Security Reference
==================================
网址： https://docs.spring.io/spring-security/site/docs/5.1.4.RELEASE/reference/htmlsingle/
源代码： https://github.com/spring-projects/spring-security/

``Spring Security 5.1`` 的特点
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
服务程序侧
  - 借助 ``UserDetailsPasswordService`` 实现密码存储的自动升级。
  - ``OAuth 2.0`` 客户端
  - ``OAuth 2.0`` 资源服务器 - 支持JWT编码的承载令牌
  - 添加了 ``OAuth2``  WEB客户端集成
  - HTTP防火墙可防止HTTP动词篡改和跨站点跟踪
  - 借助 ``ExceptionTranslationFilter`` 可以通过 ``RequestMatcher`` 和 ``AccessDeniedHandler`` 实现访问过滤（访问拒绝）的目标。
  - ``CSRF`` 可以实现对某些请求的排除（不需要权限验证？）。
  - 添加了对功能策略（ ``Feature Policy`` ）的支持。
  - 增加注解 ``@Transient`` ，它是关于“授权令牌”的。
  - 默认的登录界面，它很现代化。

``WebFlux`` 侧
  我不关注，柿子还是先挑软的捏。

关于集成方面
  - 通过 ``BadCredentialsException`` 实现对 ``Jackson`` 的支持。
  - 注解 ``@WithMockUser`` 支持在测试中设置 ``SecurityContext`` 时进行自定义
  - ``LDAP Authentication`` 可以使用自定义环境变量进行配置。
  - `X.509 Authentication` supports deriving the principal as a strategy

Spring Security 的安装或引入
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
只了解使用Maven的引入（因为我熟悉Maven）。

在 ``Spring Boot`` 中引入 ``Spring Security``
  只需要在 `pom.xml` 中加入如下内容： ::

    <dependencies>
        <!-- ... other dependency elements ... -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>
    </dependencies>

  `Spring Boot` 会自动管理 `Spring Security` 的版本，不需要我们来特殊规定。但是如果我们非要如此的话： ::

    <properties>
        <!-- ... -->
        <spring-security.version>5.1.4.RELEASE</spring-security.version>
    </dependencies>

  由于 `Spring Security` 仅对主要版本进行了重大更改，因此使用 `Spring Boot` 的较新版本的 `Spring Security` 是安全的。但是，有时可能还需要更新 `Spring Framework` 的版本。这可以通过添加Maven属性轻松完成： ::

    <properties>
        <!-- ... -->
        <spring.version>5.1.5.RELEASE</spring.version>
    </dependencies>

在非 `Spring Boot` 的项目中引入 `Spring Security`
  首选方法是利用 `Spring Security` 的 ``BOM`` 来确保在整个项目中使用一致的 `Spring Security` 版本。 ::

    <dependencyManagement>
        <dependencies>
            <!-- ... other dependency elements ... -->
            <dependency>
                <groupId>org.springframework.security</groupId>
                <artifactId>spring-security-bom</artifactId>
                <version>5.1.4.RELEASE</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>

    <!-- ... other content ... ->

    <dependencies>
        <!-- ... other dependency elements ... -->
        <!-- 最小的Spring Security Maven依赖项通常如下所示 -->
        <dependency>
            <groupId>org.springframework.security</groupId>
            <artifactId>spring-security-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.security</groupId>
            <artifactId>spring-security-config</artifactId>
        </dependency>
    </dependencies>

  这一块我不常用，了解一下就好。

模块简介
^^^^^^^^^^^^^^^^^^^^
`Spring Security` 将代码封装到不同的jar包中，以方便区分与调用。

核心代码
  spring-security-core.jar

  包含认证的核心代码、访问控制的类与接口、远程支持和基本配置的API。它不可获取，可用于：独立程序、远程客户端、服务的安全调用、 ``JDBC user provisioning`` 。包含如下 `top-level package` ：

  - ``org.springframework.security.core``
  - ``org.springframework.security.access``
  - ``org.springframework.security.authentication``
  - ``org.springframework.security.provisioning``

远程相关
  spring-security-remoting.jar

  它提供对 ``Spring Remoting`` 的集成。除非正在使用 ``Spring Remoting`` 编写远程客户端，否则我们不需要它。

  - ``org.springframework.security.remoting``

WEB方面
  spring-security-web.jar

  包含过滤器和月Web安全相关的基础结构代码。servlet API所有依赖都在这里。如果您需要 `Spring Security` 做Web身份验证服务和基于URL的访问控制，则需要它。

  - ``org.springframework.security.web``

配置相关
  spring-security-config.jar

  这里包含“安全命名空间”的解析代码和相关的Java配置代码。如果你需要 `Spring Security XML namespace` 去进行配置或者 `Spring Security` 的配置支持，你需要它。不过这里面的类不会直接作用与应用程序。

  - ``org.springframework.security.config``

LDAP相关
  spring-security-ldap.jar

  LDAP身份验证和配置代码。如果需要使用LDAP身份验证或管理LDAP用户条目，那么我们需要它。

  - ``org.springframework.security.ldap``

OAuth 2.0 Core
  spring-security-oauth2-core.jar

  包含为 ``OAuth 2.0`` 授权框架和 ``OpenID Connect Core 1.0`` 提供支持的核心类和接口。使用 `OAuth 2.0` 或 `OpenID Connect Core 1.0` 的应用程序（例如客户端，资源服务器和授权服务器）需要它。

  - ``org.springframework.security.oauth2.core``

OAuth 2.0 Client
  spring-security-oauth2-client.jar

  它是支持OAuth 2.0授权框架和OpenID Connect Core 1.0的客户端。使用 `OAuth 2.0 Login` 的应用和 `OAuth` 客户端都需要这个包。

  - org.springframework.security.oauth2.client

OAuth 2.0 JOSE
  spring-security-oauth2-jose.jar

  它是对 `JOSE` 框架的支持。它由一系列规范组成：

  - JSON Web Token (JWT)
  - JSON Web Signature (JWS)
  - JSON Web Encryption (JWE)
  - JSON Web Key (JWK)

  它的 `top-level` 包有：

  - ``org.springframework.security.oauth2.jwt``
  - ``org.springframework.security.oauth2.jose``

ACL
  spring-security-acl.jar

  支持对域的 `ACL` 实现。用于确保应用中域对象的安全性（安全访问）。

  - ``org.springframework.security.acls``

CAS
  spring-security-cas.jar

  它是 `Spring Security` 中 `CAS` （中央认证服务）客户端的集成。如果我们使用 `CAS` 单点登录服务与 `Spring Security` 的WEB认证结合的话，需要这个包。

  - ``org.springframework.security.cas``

OpenID
  spring-security-openid.jar

  OpenID Web身份验证支持。用于针对外部OpenID服务器对用户进行身份验证。

  它需要 ``OpenID4Java`` 。

  - ``org.springframework.security.openid``

Test
  spring-security-test.jar

  支持对 `Spring Security` 的测试。

Spring Security的样本
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
`Spring Security` 为我们提供了两个实现样本，分别是 `Tutorial Sample` 与 `Contacts Sample` 。其中 `Contacts Sample` 中具备更多的东西如 `ACLs` ，值的一体的是它提供了一个页面，我们可以编辑“用户资料”。获取方式有两种：到中心库中直接下载包 ``spring-security-samples-tutorial-3.1.x.war`` 、 ``spring-security-samples-contacts-3.1.x.war`` ；或者用Maven ::

  <dependency>
      <groupId>org.springframework.security</groupId>
      <artifactId>spring-security-samples-tutorial</artifactId>
      <version>3.1.x</version>
  </dependency>

::

  <dependency>
      <groupId>org.springframework.security</groupId>
      <artifactId>spring-security-samples-contacts</artifactId>
      <version>3.1.x</version>
  </dependency>

初次之外，官方还提供了 `LDAP Sample` 、 `OpenID Sample` 、 `CAS Sample` 、 `JAAS Sample` 、 `Pre-Authentication Sample` 。不过这些样本就没有现成 `war` 文件了，需要自己编译打包。下载方式参考上面的 `Maven` 方式。

Spring Security的配置
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
自 `Spring Security 3.2` 以来， `Spring Security Java Configuration` 支持使用户无需使用任何XML即可轻松配置 `Spring Security` 。

带来的新疑惑或求知欲
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
- ``SpringMVC`` 与 ``SpringWebFlux`` 的区别？（知道了新名词 ``Web Flux`` 、 ``Reactive Programming`` 、 ``非阻塞异步框架`` ）

  参考资料： http://www.cnblogs.com/niechen/p/9303451.html

- ``Spring BOM`` 可以自动管理第三发控件的版本号，从而避免由于版本的问题导致的各种各样的问题。 BOM是 `bill of materials` 的简写，这个东西可以去了解一下。
- XML命名空间（ ``XML namespaces`` ），它的实际意义是什么？
- ``Spring Security XML namespace`` 是什么鬼？关于 ``spring-security-config.jar`` 的用途有些疑惑。
- ``LDAP`` 是轻量目录访问协议。
- ``OAuth 2.0`` 与 ``OpenID Connect Core 1.0``
- ``JOSE`` 全名 `Javascript Object Signing and Encryption` 。 `JOSE` 框架旨在提供一种在各方之间安全地转移信息的方法（这一点我可能理解有误）。
- ``ACL`` 是访问控制列表的简称。
- ``CAS`` 中央认证服务的简称。
- ``CSRF`` 全称“跨站请求伪造”（Cross-site request forgery），是一种对网站的恶意利用的攻击方式。
- ``HSTS`` 全称 ``HTTP Strict Transport Security`` ，是一个安全功能,它告诉浏览器只能通过HTTPS访问当前资源,而不是HTTP。
- ``X-Content-Type-Options`` 响应首部相当于一个提示标志，被服务器用来提示客户端一定要遵循在 `Content-Type` 首部中对 `MIME` 类型的设定，而不能对其进行修改。这就禁用了客户端的 `MIME` 类型嗅探行为，换句话说，也就是意味着网站管理员确定自己的设置没有问题。
- ``会话固定攻击`` 又称为 ``Session Fixation Attack`` ，是利用应用系统在服务器的会话ID固定不变机制，借助他人用相同的会话ID获取认证和授权，然后利用该会话ID劫持他人的会话以成功冒充他人，造成会话固定攻击。
- ``X-XSS-Protection`` ，它是一个响应头，也是Internet Explorer，Chrome和Safari的一个功能，当检测到跨站脚本攻击 (XSS)时，浏览器将停止加载页面。虽然这些保护在现代浏览器中基本上是不必要的，当网站实施一个强大的 ``Content-Security-Policy`` 来禁用内联的JavaScript ('unsafe-inline')时, 他们仍然可以为尚不支持 CSP 的旧版浏览器的用户提供保护。
- ``Clickjacking`` 又称为 `点击劫持` 。是一种在网页中将恶意代码等隐藏在看似无害的内容（如按钮）之下，并诱使用户点击的手段。
- ``X-Frame-Options`` 是一个HTTP响应头，用来给浏览器指示允许一个页面可否在 <frame>, <iframe> 或者 <object> 中展现的标记。网站可以使用此功能，来确保自己网站的内容没有被嵌到别人的网站中去，也从而避免了点击劫持 (clickjacking) 的攻击。
