Spring Cloud 总结
==================================
- 设计中心化结构时，需要终于“中心服务”的负载能力。
- Eureka-Client中，配置 ``eureka.client.healthcheck.enabled`` 可以让客户端向服务器主动传播（同步）自己的健康状态。
  **注意** ，此属性只能在 ``application.properties`` 中设置，不能在 ``bootstrap.yml`` 中设置。
- Eureka也考虑到了 **区域** 问题。

  如果您已将Eureka客户端部署到多个区域，您可能希望这些客户端在使用另一个区域中的服务之前，利用同一区域内的服务。为此我们需要这样配置： ::

    eureka.client.preferSameZoneEureka = true
    eureka.instance.metadataMap.zone = zone1

  他们分别代表：开启“优先调用同区域的服务”功能和定义本机的区域名词。

- 关闭自动注册功能： ``spring.cloud.service-registry.auto-registration.enabled=false`` 。
- ``bootstrap.properties`` 里的 ``spring.profiles.active=dev,mysql`` ，可以取代 ``spring.cloud.config.profile`` 的作用。

``application.properties`` 与 ``bootstrap.properties`` 区别
  Spring Cloud 构建于 Spring Boot 之上，在 Spring Boot 中有两种上下文，一种是 bootstrap, 另外一种是 application, bootstrap 是应用程序的父上下文，也就是说 bootstrap 加载优先于 applicaton。bootstrap 主要用于从额外的资源来加载配置信息，还可以在本地外部配置文件中解密属性。这两个上下文共用一个环境，它是任何Spring应用程序的外部属性的来源。bootstrap 里面的属性会优先加载，它们默认也不能被本地相同配置覆盖。

  简而言之：boostrap 由父 ApplicationContext 加载，比 applicaton 优先加载；boostrap 里面的属性不能被覆盖。

  application 配置文件这个容易理解，主要用于 Spring Boot 项目的自动化配置。

  bootstrap 配置文件有以下几个应用场景：

  - 使用 Spring Cloud Config 配置中心时，这时需要在 bootstrap 配置文件中添加连接到配置中心的配置属性来加载外部配置中心的配置信息；
  - 一些固定的不能被覆盖的属性
  - 一些加密/解密的场景；

关于负载均衡的简单对比：
  - ``RestTemplate`` ：需要使用IP，以URI的方式调用其他服务接口。
  - ``Ribbon`` ：使用“服务名”，以URI的方式调用其他服务接口。
  - ``Feign`` ：使用“声明式调用”，以方法的方式调用其他服务接口。

代码
^^^^^^^^^^^^^^^^^
获得客户端信息：

  .. code-block:: java

    public class MyController {
        // 方法一：使用DiscoveryClient
        @Autowired
        private DiscoveryClient discoveryClient;
        // 方法二：使用EurekaClient
        @Autowired
        private EurekaClient eurekaClient;

        public List<ServiceInstance> showInfo() {
            String applicationName = "microservice-provider-user"
            // 方法一：
            return this.discoveryClient.getInstances(applicationName);
            // 方法二：
            return this.eurekaClient.getApplications();
        }
    }

TODO
^^^^^^^^^^^^^^^^
- ``Spring Cloud Eureka`` 中如果我们需要更多的控制健康检查，您可以考虑实施自己的com.netflix.appinfo.HealthCheckHandler。
