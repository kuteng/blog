Spring Cloud入门指南
=======================
服务治理
^^^^^^^^^^^^^^^^^^^^^^^
这里以 **Eureka** 为例（还有其他选择，比如： *Consul* 。

参考：
  `Spring Cloud构建微服务架构：服务注册与发现（Eureka、Consul）【Dalston版】 <https://segmentfault.com/a/1190000010097698>`_

  `SpringCloud-微服务的注册与发现Eureka（二） <https://cloud.tencent.com/developer/article/1152507>`_

作用
:::::::::::::::::::::
在 *客户端* 可以通过 ``discoveryClient.getApplications()`` 这类语句，获得集群中的所有服务信息，以便做后续交互。


代码示例
:::::::::::::::::::::
查看项目 `spring_cloud <https://gitee.com/Kuteng/spring_cloud>`_ 里的 ``eureka-client`` 与 ``eureka-server`` 两个项目，它们通过 *Eureka* 简单实现的 *服务治理* 的服务与客户端。 注意：java切换为 **jdk8** 版本；在tag ``eureka.canrun`` 中，可以保证成功启动。

  该实例中， *Eureka Server* 是单节点的，但是在生产环境下应该是多节点的。需要在 *Eureka Server* 的 ``application.properties`` 添加类似这样的配置： ``eureka.client.serviceUrl.defaultZone=http://peer1:1111/eureka/,http://peer2:1111/eureka/`` ，同时在 *Eureka Client* 的 ``application.properties`` 中配置好 ``eureka.client.serviceUrl.defaultZone`` 。

原理
:::::::::::::::::::::
服务治理一般都会有两个功能：服务注册、服务发现。通常会有一个注册中心，每个服务单元向注册中心登记自己信息，比如提供的服务，ip, 端口以及一些附近加信息等。注册中心会将新的服务实例发送给其它依赖此服务的实例。 

|the_diagram_of_eureka_server|

服务注册
  服务提供者在启动时会将自己的信息注册到Eureka Server， Eureka Server收到信息后， 会将数据信息存储在一个双层结构的Map中，其中第一层的key是服务名，第二层的key是具体服务的实例名。

服务同步
  如果有多个Eureka Server，一个服务提供者向其中一个Eureka Server注册了，这个Eureka Server会向集群内的其它Eureka Server转发这个服务提供者的注册信息，从而实现实现Eureka Server之间的服务同步。

服务续约
  在注册完成成后，服务提供者会维护一个心中持续发送信息给Eureka Server(注册中心)表示正常运行，以防止Eureka Server将该服务实例从服务列表中剔除。

服务下线
  当服务实例正常关闭时，它会发送一个服务下线的消息给注册中心，注册中心收到信息后，会将该服务实例状态置为下线，并把该信息传播出去。

  服务下线的集中方式：

  - ``curl -X DELETE http://eureka-server-ip/eureka/apps/{service-name}[/<service-ip>:<service-name>:<service-port>`` 如： ``curl -X DELETE http://localhost:8090/eureka/apps/test-service/localhost:test-service:8081``

    值得注意的是，Eureka客户端每隔一段时间（默认30秒）会发送一次心跳到注册中心续约。如果通过这种方式下线了一个服务，而没有及时停掉的话，该服务很快又会回到服务列表中。所以， **可以先停掉服务，再发送请求将其从列表中移除** 。
  - 客户端主动通知注册中心下线。如果你的eureka客户端是是一个spring boot应用，可以通过调用以下代码通知注册中心下线。

    ``DiscoveryManager.getInstance().shutdownComponent();`` ，请将它放到某个Controller中。

获取服务
  当一个服务实例依赖另一个服务时，这时这个服务实例又充当了服务消费者，它会发送一个信息给注册中心, 请求获取注册的服务清单，注册中心会维护一份只读的服务清单来返回给服务消费者。

失效剔除
  有时候，服务实例可能无法正常提供服务，而注册中心没有收到服务下线的信息。注册中心会创建一个定时任务，将超过一定时间没有服务续约消息的服务实例从服务清单中剔除。

自我保护
  上面讲到失效剔除时，会将超过一定时间没有收到服务续约消息的实例从服务清单中剔除掉，在这中间还有一个逻辑。如果在运行期间，统计心跳成功的比例低于85%（心跳阈值），注册中心会将当前服务清单中的实例注册信息保护起来，让这些实例不会过期。但是在这种情况下，若服务实例出现问题，那么服务消费者可能会拿到实际已经不能正常运行的服务实例，就会出现调用失败的情况，所以客户端需要有容错机制，比如请求重试，或断路器等。

  但是有一个定时任务默认每15分钟执行一次，会根据运行状况重新计算心跳阈值；但也可能不重新计算，这时，Eureka Server的自我保护状态会一直存在。

  如果要关闭自我保护机制，可以将eureka.server.enable-self-preservation设置为false，以确保注册中心将不可用的服务实例及时剔除。

服务消费
^^^^^^^^^^^^^^^^^^^^^^^^^

简单实现服务消费
::::::::::::::::::::::
参考：
  `Spring Cloud构建微服务架构：服务消费（基础）【Dalston版】 <https://segmentfault.com/a/1190000010097825>`_


代码示例：
  项目： `spring_cloud/eureka-consumer <https://gitee.com/Kuteng/spring_cloud/tree/master/eureka-consumer>`_

  解读：

  - pom.xml中需要加入依赖 ``spring-boot-starter-actuator`` 。
  - 使用 ``@EnableDiscoveryClient`` 注解（类 ``Application`` ）用来将当前应用加入到服务治理体系中。
  - 在类 ``Application`` 中， *初始化* ``RestTemplate`` ，以便以后该应用发起REST请求。
  - 在类 ``DcController`` 中，我们分别注入了 ``LoadBalancerClient`` 和 ``RestTemplate`` 。前者是引入的jar包自动 *初始化* 的，后者是我们在 ``Application`` 类 *初始化* 的。
  - 可以通过 ``LoadBalancerClient`` 接口的 ``choose`` 函数，用 *服务名* 来获取该服务的实例，这个服务实例的基本信息存储在ServiceInstance中。
  - 获得 *服务实例* 后，我们可以获得实例信息或者发起服务接口消费请求。在这里，我们通过这些对象中的信息拼接出访问/dc接口的详细地址，最后再利用 ``RestTemplate`` 对象实现对服务提供者接口的调用。
  - 但是这样的做法需要我们手工的去编写服务选取、链接拼接等繁琐的工作，对于开发人员来说非常的不友好。

Spring Cloud Ribbon
:::::::::::::::::::::::::::
**Spring Cloud Ribbon** 是基于Netflix Ribbon实现的一套客户端负载均衡的工具。它是一个基于HTTP和TCP的客户端负载均衡器。它可以通过在客户端中配置ribbonServerList来设置服务端列表去轮询访问以达到均衡负载的作用。

当Ribbon与Eureka联合使用时， ``ribbonServerList`` 会被 ``DiscoveryEnabledNIWSServerList`` 重写，扩展成从Eureka注册中心中获取服务实例列表。同时它也会用NIWSDiscoveryPing来取代IPing，它将职责委托给Eureka来确定服务端是否已经启动。而当Ribbon与Consul联合使用时，ribbonServerList会被ConsulServerList来扩展成从Consul获取服务实例列表。同时由ConsulPing来作为IPing接口的实现。

我们在使用Spring Cloud Ribbon的时候，不论是与Eureka还是Consul结合，都会在引入Spring Cloud Eureka或Spring Cloud Consul依赖的时候通过自动化配置来加载上述所说的配置内容，所以我们可以快速在Spring Cloud中实现服务间调用的负载均衡。

参考：
  `Spring Cloud构建微服务架构：服务消费（Ribbon）【Dalston版】 <https://segmentfault.com/a/1190000010163772>`_

代码示例
  项目： `spring_cloud/eureka-consumer-ribbon <https://gitee.com/Kuteng/spring_cloud/tree/master/eureka-consumer-ribbon>`_

  注意事项：

  - pom.xml中需要加入依赖 ``spring-cloud-starter-ribbon`` 。
  - 对于方法 ``public RestTemplate restTemplate() { ... }`` ，除了注解 ``@Bean`` 外，还需要注解 ``@LoadBalanced`` 。
  - 因为java bean: ``restTemplate`` 被 ``@LoadBalanced`` 注解，所以代码 ``restTemplate.getForObject("http://eureka-client/dc", String.class);`` 中，可以只是使用 *Application name* 替换掉 *IP* 与 *端口* 。

    实现这一功能的原理是： ``Spring Cloud Ribbon`` 有一个拦截器，它能够在这里进行实际调用的时候，自动的去选取服务实例，并将实际要请求的IP地址和端口替换这里的服务名，从而完成服务接口的调用。

原理
#######################
在每个服务实例中根据配置的负载均衡规则，实现负载均衡。

修改配置的方式有：

- 通过 *配置类* ：

  .. code-block:: java

    /**
     * 该类为配置类
     * 不应该被ComponentScan扫描
     */
    @Configuration
    public class RibbonConfiguration {
        @Bean
        public IRule ribbonRule(){
            //配置负载均衡的规则，更改为随机
            return new RandomRule();
        }
    }

    /**
     * 在驱动类中，指定配置类。
     */
    @SpringBootApplication
    @EnableDiscoveryClient
    // 使用 @RibbonClient 或 @RibbonClients 注解为服务提供者指定配置类
    @RibbonClient(name = "flim-user",configuration = RibbonConfiguration.class)
    public class FlimConsumerApplication {
        @Bean
        @LoadBalanced
        public RestTemplate restTemplate(){
            return new RestTemplate();
        }
        public static void main(String[] args) {
            SpringApplication.run(FlimConsumerApplication.class, args);
        }
    }

- 配置文件方式配置 ::

    flim-user:
      ribbon:
        NFLoadBalancerRuleClassName: com.netflix.loadbalancer.RandomRule

  相关配置有：

  - ``NFLoadBalancerClassName`` ：配置 ILoadBalancer 的实现类
  - ``NFLoadBalancerRuleClassName`` ：配置 IRule 的实现类
  - ``NFLoadBalancerPingClassName`` ：配置 IPing 实现类
  - ``NIWSServerListClassName`` ：配置 ServerList 的实现类
  - ``NIWSServerListFilterClassName`` ：配置 ServerListFilter 的实现类

  常用的 Ribbon 全局配置 ::

    ribbon:
      ConnectionTimeout: #连接超时时间
      ReadTimeout: #读取超时时间
      OkToRetryOnAllOperatotions: #对所有操作请求都进行重试
      MaxAutoRetriesNextServer: #切换服务器实例的重试次数
      MaxAutoRetries:  #对当前实例的重试次数
      ServerListRefreshInterval:  #刷新服务列表源的间隔时间

Spring Cloud Feign
:::::::::::::::::::::::::::
**Spring Cloud Feign** 是一套基于Netflix Feign实现的声明式服务调用客户端。它使得编写Web服务客户端变得更加简单。我们只需要通过创建接口并用注解来配置它既可完成对Web服务接口的绑定。它具备可插拔的注解支持，包括Feign注解、JAX-RS注解。它也支持可插拔的编码器和解码器。Spring Cloud Feign还扩展了对Spring MVC注解的支持，同时还整合了Ribbon和Eureka来提供均衡负载的HTTP客户端实现。

参考
  `Spring Cloud构建微服务架构：服务消费（Feign）【Dalston版】 <https://segmentfault.com/a/1190000010180228>`_

代码示例
  项目： `spring_cloud/eureka-consumer-feign <https://gitee.com/Kuteng/spring_cloud/tree/master/eureka-consumer-feign>`_

  注意事项：

  - pom.xml中需要加入依赖 ``spring-cloud-starter-feign`` 。
  - 在应用主类（ ``Application`` ）中，使用注解 ``@EnableFeignClients`` 进行修饰，以便开启扫描Spring Cloud Feign客户端的功能。
  - 我们创建了接口 ``DcClient`` ，它是Feign的客户端接口定义。使用 ``@FeignClient`` 注解来指定这个接口所要调用的 *服务名称* ，接口中定义的各个函数使用Spring MVC的注解就可以来绑定服务提供方的REST接口，比如本示例中就是绑定eureka-client服务的/dc接口的例子：
  - 在逻辑类 ``DcController`` 中，我们通过定义的feign客户端（ ``DcClient`` ）来调用服务提供方的接口。

优点
  - 通过 **Spring Cloud Feign** 来实现服务调用的方式更加简单了，通过 ``@FeignClient`` 定义的接口来统一的声明我们需要依赖的微服务接口。而在具体使用的时候就跟调用本地方法一样的进行调用即可。
  - 由于Feign是基于Ribbon实现的，所以它自带了 **客户端负载均衡** 功能，也可以通过Ribbon的IRule进行策略扩展。
  - 另外，Feign还整合的Hystrix来实现服务的容错保护，在Dalston版本中，Feign的Hystrix默认是关闭的。待后文介绍Hystrix带领大家入门之后，我们再结合介绍Feign中的Hystrix以及配置方式。

配置中心
^^^^^^^^^^^^^^^^^^^^^^^^^^^
``Spring Cloud Config`` 是Spring Cloud团队创建的一个全新项目，用来为分布式系统中的基础设施和微服务应用提供集中化的外部配置支持，它分为服务端与客户端两个部分。其中服务端也称为分布式配置中心，它是一个独立的微服务应用，用来连接配置仓库并为客户端提供获取配置信息、加密/解密信息等访问接口；而客户端则是微服务架构中的各个微服务应用或基础设施，它们通过指定的配置中心来管理应用资源与业务相关的配置内容，并在启动的时候从配置中心获取和加载配置信息。Spring Cloud Config实现了对服务端和客户端中环境变量和属性配置的抽象映射，所以它除了适用于Spring构建的应用程序之外，也可以在任何其他语言运行的应用程序中使用。由于Spring Cloud Config实现的配置中心默认采用Git来存储配置信息，所以使用Spring Cloud Config构建的配置服务器，天然就支持对微服务应用配置信息的版本管理，并且可以通过Git客户端工具来方便的管理和访问配置内容。当然它也提供了对其他存储方式的支持，比如：SVN仓库、本地化文件系统。

重要框架
  ``Spring Cloud Config``

参考
  简单实现： `Spring Cloud构建微服务架构：分布式配置中心【Dalston版】 <https://segmentfault.com/a/1190000010180245>`_

  复杂实现： `SpringCloud-Config 配置中心原理 <https://blog.csdn.net/sinat_25518349/article/details/86323476>`_

代码示例
  *Config仓库* 项目： `config-repo-demo <https://gitee.com/Kuteng/config-repo-demo>`_

  *Config服务器* 项目： `spring_cloud/config-server-git <https://gitee.com/Kuteng/spring_cloud/tree/master/config-server-git>`_

  *Config客户端* 项目： `spring_cloud/config-client <https://gitee.com/Kuteng/spring_cloud/tree/master/config-client>`_

代码解读
  配置仓库：

    - 将 **配置仓库** 与 *配置服务器* 分离开，可以实现解耦。比如服务器不停止，同时修改 *Config仓库* 里的配置内容，并能够让这些修改立刻生效。
    - 该仓库中 ``config-client.yml`` 和 ``config-client-dev.yml`` 是名为 ``config-client`` 的应用的配置文件。其中 ``config-client.yml`` 是该应用的默认（ ``default`` ）配置，而 ``config-client-dev.yml`` 是该应用在开发环境（ ``dev`` ）下的配置。

  服务器部分：

  - pom.xml中需要加入依赖 ``spring-cloud-config-server`` 。
  - 在应用主类（ ``Application`` ）上，需要使用注解 ``@EnableConfigServer`` 开启Spring Cloud Config的服务端功能。
  - 在配置文件 ``application.yml`` 增加 **配置仓库** （这里是Git）的信息。
  - 如果我们的Git仓库需要权限访问，那么可以通过配置下面的两个属性来实现；

    spring.cloud.config.server.git.username：访问Git仓库的用户名

    spring.cloud.config.server.git.password：访问Git仓库的用户密码

  - 如果通过URL访问，那么配置信息的URL与配置文件的映射关系如下：

    - ``/{application}/{profile}[/{label}]``
    - ``/{application}-{profile}.yml``
    - ``/{label}/{application}-{profile}.yml``
    - ``/{application}-{profile}.properties``
    - ``/{label}/{application}-{profile}.properties``

    其中 ``{label}`` 对应Git上不同的分支，默认为master。

    如果要访问master分支，config-client应用的dev环境，就可以访问这个url：http://localhost:1201/config-client/dev/master 。它返回的是 *配置仓库* 中 ``config-client-dev.yml`` 的内容。也就是说 ``{profile}`` 在这里对应文件名 ``config-client-dev.yml`` 中的 ``dev`` ；而 ``{application}`` 对应文件名中的 ``config-client`` 。即：应用名是 ``config-client`` ，环境名是 ``dev`` ，分支名是 ``master`` ，以及default环境和dev环境的配置内容。

  客户段部分：

  - pom.xml总需要加入依赖 ``spring-boot-starter-web`` 和 ``spring-cloud-starter-config`` 。
  - 在配置文件 ``bootstrap.yml`` （注意不是 ``application.yml`` ）中需要配置好本应用的名称（ ``spring.application.name`` ）。注意：该应用名以及 ``spring.cloud.config.profile`` 、 ``spring.cloud.config.label`` ，这三者需要与 *配置仓库* 中的配置文件的文件名（或路径）相对应。配置说明如下：

    - ``spring.application.name`` ：对应配置文件规则中的{application}部分
    - ``spring.cloud.config.profile`` ：对应配置文件规则中的{profile}部分
    - ``spring.cloud.config.label`` ：对应配置文件规则中的{label}部分
    - ``spring.cloud.config.uri`` ：配置中心config-server的地址

  - 客户端中的Java代码可以使用注解 ``@Value`` ，通过注入的方式获得配置信息；也可以通过 ``@Autowired`` 得到 ``Environment`` 对象，使用 ``env.getProperty("info.profile")`` 得到配置信息。
  - 注解 ``@RefreshScope`` 的作用是，可以通过URI： ``/fresh`` ，主动让 *Config Server* 从 *配置仓库* 拉取最新的配置文件。

容错保护
^^^^^^^^^^^^^^^^^^^^^^^^^^
在微服务架构中，我们将系统拆分成了一个个的服务单元，各单元应用间通过服务注册与订阅的方式互相依赖。由于每个单元都在不同的进程中运行，依赖通过远程调用的方式执行，这样就有可能因为网络原因或是依赖服务自身问题出现调用故障或延迟，而这些问题会直接导致调用方的对外服务也出现延迟，若此时调用方的请求不断增加，最后就会出现因等待出现故障的依赖方响应而形成任务积压，线程资源无法释放，最终导致自身服务的瘫痪，进一步甚至出现故障的蔓延最终导致整个系统的瘫痪。如果这样的架构存在如此严重的隐患，那么相较传统架构就更加的不稳定。为了解决这样的问题，因此产生了断路器等一系列的服务保护机制。

针对上述问题，在 ``Spring Cloud Hystrix`` 中实现了 *线程隔离* 、 *断路器* 等一系列的服务保护功能。它也是基于Netflix的开源框架 Hystrix实现的，该框架目标在于通过控制那些访问远程系统、服务和第三方库的节点，从而对延迟和故障提供更强大的容错能力。Hystrix具备了 **服务降级** 、 **服务熔断** 、 **线程隔离** 、 **请求缓存** 、 **请求合并** 以及 **服务监控** 等强大功能。

重要框架
  ``Spring Cloud Hystrix`` 

服务降级
  主逻辑失败后，使用备用逻辑。如下面项目中，如果请求超时，就放弃请求改为执行 ``fallback()`` 方法。

服务熔断
  在主逻辑多次失败之后， *隔离* （或者说 *忽略* ）主逻辑，直接使用备用逻辑。同时提过主逻辑的 *恢复* 的机制。

  hystrix的 *服务熔断* 实现了对依赖资源故障的端口、对降级策略的自动切换以及对主逻辑的自动恢复机制。

  详见： :ref:`the-explain-of-circuit-breaker` 。

线程隔离
  简述：每个被 ``@HystrixCommand`` 注解的方法，都会启动一个线程池。每次调用该服务（即执行该方法），都会在一个线程中执行。

  详见： :ref:`the_explain_of_thread_isolation`

请求缓存
  对通过 ``Key`` 信息对请求进行缓存。默认URI里参数都会作为Key，当然这个可以更改。

  相关注解有：

  - ``@CacheResult``
  - ``@CacheResult(cacheKeyMethod = "methodName")`` 。如： ::

      @CacheResult(cacheKeyMethod = "getCacheKey2")
      @HystrixCommand
      public Book test6(Integer id) {
          return restTemplate.getForObject("http://HELLO-SERVICE/getbook5/{1}", Book.class, id);
      }

      // 这里的参数就是 getForObject 方法 URI 里的参数。
      public String getCacheKey2(Integer id) {
          return String.valueOf(id);
      }

  - ``@CacheKey`` 指定缓存的key，如下： ::

      @CacheResult
      @HystrixCommand
      // CacheKey 被用作修改该方法的参数。
      public Book test6(@CacheKey Integer id,String aa) {
          return restTemplate.getForObject("http://HELLO-SERVICE/getbook5/{1}", Book.class, id);
      }

  - ``@CacheRemove(commandKey = "targetMethodName")`` ，参数 ``commandKey`` 不能缺少。它的意思是：一旦调用被此注解修饰的方法， ``commandKey`` 指向 *目标方法* 的注解将清空。如： ::

      @CacheRemove(commandKey = "test6")
      @HystrixCommand
      public Book test7(@CacheKey Integer id) {
          return null;
      }

      @CacheResult(cacheKeyMethod = "getCacheKey2")
      @HystrixCommand
      public Book test6(Integer id) {
          return restTemplate.getForObject("http://HELLO-SERVICE/getbook5/{1}", Book.class, id);
      }

    一旦调用方法 ``test7(id)`` ，则 ``test6(id)`` 对应的缓存就会被清空。

请求合并
  待补充

服务监控
  之前提到过，断路器是根据一段时间窗内的请求情况来判断并操作断路器的打开和关闭状态的。而这些请求情况的指标信息都是HystrixCommand和HystrixObservableCommand实例在执行过程中记录的重要度量信息，它们除了Hystrix断路器实现中使用之外，对于系统运维也有非常大的帮助。这些指标信息会以“滚动时间窗”与“桶”结合的方式进行汇总，并在内存中驻留一段时间，以供内部或外部进行查询使用，Hystrix Dashboard就是这些指标内容的消费者之一。

  监控器的用法可以参考：
  `Spring Cloud构建微服务架构：Hystrix监控面板【Dalston版】 <https://segmentfault.com/a/1190000010180299>`_

  ``Hystrix Dashboard`` 共支持三种不同的监控方式，依次为：

  - 默认的集群监控：通过URLhttp://turbine-hostname:port/turbine.stream开启，实现对默认集群的监控。
  - 指定的集群监控：通过URLhttp://turbine-hostname:port/turbine.stream?cluster=[clusterName]开启，实现对clusterName集群的监控。
  - 单体应用的监控：通过URLhttp://hystrix-app:port/hystrix.stream开启，实现对具体某个服务实例的监控。

  前两者都对集群的监控，需要整合Turbine才能实现。

参考
  `Spring Cloud构建微服务架构：服务容错保护（Hystrix服务降级）【Dalston版】 <https://segmentfault.com/a/1190000010180256>`_

  `Spring Cloud构建微服务架构：服务容错保护（Hystrix依赖隔离）【Dalston版】 <https://segmentfault.com/a/1190000010180268>`_

  `Spring Cloud构建微服务架构：服务容错保护（Hystrix断路器）【Dalston版】 <https://segmentfault.com/a/1190000010180279>`_

  `Spring Cloud构建微服务架构：Hystrix监控面板【Dalston版】 <https://segmentfault.com/a/1190000010180299>`_

  `白话：服务降级与熔断的区别 <https://segmentfault.com/a/1190000012137439>`_

代码示例
  项目： `spring_cloud/eureka-consumer-ribbon-hystrix <https://gitee.com/Kuteng/spring_cloud/tree/master/eureka-consumer-ribbon-hystrix>`_

代码解读
  - ``pom.xml`` 中引入 ``spring-cloud-starter-hystrix`` 、 ``spring-cloud-starter-hystrix-dashboard`` 依赖
  - 在应用主类中使用 ``@EnableCircuitBreaker`` 或 ``@EnableHystrix`` 注解开启 *Hystrix* 的使用，同时使用 ``@EnableHystrixDashboard`` 注解开启 **Hystrix Dashboard** 功能。
  - 定义专门的 *消费类* ``ConsumerService`` ，实现对其他应用的服务的消费。在具体的消费动作（具体执行逻辑的函数）上增加 ``@HystrixCommand`` 注解来指定服务降级方法。

  其他

  - 一个Spring Cloud标准应用应包含服务发现以及断路器，直接使用 ``@SpringCloudApplication`` 代替下面三个注解： ``@SpringBootApplication`` 、 ``@EnableDiscoveryClient`` 、 ``@EnableCircuitBreaker`` 。
  - 可以自定义服务降级的触发条件，比如说 *超时时间* 。

    - 在代码中修改：

      .. code-block:: java

        @HystrixCommand(fallbackMethod = "fallback", commandProperties = {
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds",value = "2000") // 设置超时时间为 2 秒。
        })

    - 通过配置文件修改：application.properties中的 ``hystrix.command.default.execution.isolation.thread.timeoutInMilliseconds=3000`` 。

原理
::::::::::::::::::

Hystrix 通过如下机制来解决雪崩效应问题：

- 资源隔离：包括线程池隔离和信号量隔离，限制调用分布式服务的资源使用，某一个调用的服务出现问题不会影响其他服务调用。
- 降级机制：超时降级、资源不足时(线程或信号量)降级，降级后可以配合降级接口返回托底数据。
- 融断：当失败率达到阀值自动触发降级(如因网络故障/超时造成的失败率高)，熔断器触发的快速失败会进行快速恢复。
- 缓存：提供了请求缓存、请求合并实现。

资源隔离
  - 线程池隔离模式：使用一个线程池来存储当前请求，线程池对请求作处理，设置任务返回处理超时时间，堆积的请求先入线程池队列。这种方式要为每个依赖服务申请线程池，有一定的资源消耗，好处是可以应对突发流量（流量洪峰来临时，处理不完可将数据存储到线程池队里慢慢处理）
  - 信号量隔离模式：使用一个原子计数器（或信号量）记录当前有多少个线程在运行，请求来先判断计数器的数值，若超过设置的最大线程个数则丢弃该类型的新请求，若不超过则执行计数操作请求来计数器+1，请求返回计数器-1。这种方式是严格的控制线程且立即返回模式，无法应对突发流量（流量洪峰来临时，处理的线程超过数量，其他的请求会直接返回，不继续去请求依赖的服务）

服务降级
  服务降级的目的保证上游服务的稳定性，当整体资源快不够了，将某些服务先关掉，待渡过难关，再开启回来。

  一般有两种模式：

  - 如果服务失败，通过 `fallback` 进行降级，返回静态值。
  - *级联模式* ，如果服务失败，则调用 *备用服务* 。

  如果资源充足（线程池或信号量等），Hystrix将会执行操作指令。操作指令的调用最终都会到这两个方法：

  - ``HystrixCommand.run()`` ：返回一个响应或者抛出一个异常
  - ``HystrixObservableCommand.construct()`` ：返回一个可观测的发出响应(s)或发送一个onError通知

  如果执行指令的时间超时，执行线程会抛出 TimeoutException 异常。Hystrix会抛弃结果并直接进入失败处理状态。如果执行指令成功，Hystrix会进行一系列的数据记录，然后返回执行的结果。

源码解读
::::::::::::::::::::::::
从 ``HystrixCommandAspect.java`` ，它使用了 *AOP* 技术，有趣的是它里面的很多切点都是 *注解* 。


服务网关
^^^^^^^^^^^^^^^^^^^^^^^^
**服务网关** 是微服务架构中一个不可或缺的部分。通过服务网关统一向外系统提供REST API的过程中，除了具备服务路由、均衡负载功能之外，它还具备了权限控制等功能。Spring Cloud Netflix中的 **Zuul** 就担任了这样的一个角色，为微服务架构提供了前门保护的作用，同时将权限控制这些较重的非业务逻辑内容迁移到服务路由层面，使得服务集群主体能够具备更高的可复用性和可测试性。

下面我们通过实例例子来使用一下 *Zuul* 来作为服务的路由功能。

一旦 **服务网关** 起作用，外部网络就可以通过 *网关应用* 以 ``/<app name>/<server name>`` 的方式，访问集群内的所有服务（前提是没有权限限制）。而权限限制也可以在 *网关应用* 内 *集中* 管理。

参考
  `Spring Cloud构建微服务架构：服务网关（基础）【Dalston版】 <https://segmentfault.com/a/1190000010874181>`_

代码示例
  项目： `spring_cloud/api-gateway <https://gitee.com/Kuteng/spring_cloud/tree/master/api-gateway>`_

代码解读
  - ``pom.xml`` 中引入依赖 ``spring-cloud-starter-zuul`` 。
  - 在应用主类中使用 ``@EnableZuulProxy`` 注解开启Zuul的功能。
  - 在 ``application.properties`` 中加入服务名、端口号、eureka注册中心的地址： ``spring.application.name`` 、 ``server.port`` 、 ``eureka.client.serviceUrl.defaultZone`` 。

消息驱动
^^^^^^^^^^^^^^^^^^^^^^^^
Spring Cloud Stream
:::::::::::::::::::::::::
参考
  `Spring Cloud构建微服务架构：消息驱动的微服务（入门）【Dalston版】 <https://segmentfault.com/a/1190000012497486>`_

  `Spring Cloud构建微服务架构：消息驱动的微服务（核心概念）【Dalston版】 <https://segmentfault.com/a/1190000013026925>`_

  `Spring Cloud构建微服务架构：消息驱动的微服务（消费组）【Dalston版】 <https://segmentfault.com/a/1190000013343907>`_

  `Spring Cloud Stream如何处理消息重复消费？ <https://segmentfault.com/a/1190000017150745>`_

  `Spring Cloud Stream消费失败后的处理策略（一）：自动重试 <https://segmentfault.com/a/1190000017369803>`_

  `Spring Cloud Stream消费失败后的处理策略（二）：自定义错误处理逻辑 <https://segmentfault.com/a/1190000017388788>`_

代码示例
  项目： `spring_cloud/stream-client <https://gitee.com/Kuteng/spring_cloud/tree/master/stream-client>`_

**待续**

知识点
^^^^^^^^^^^^^^^^^^^^^^^
Ribbon与负载均衡
  Ribbon中的负载均衡主要是两部分：定期向服务器确认“服务列表”是否变动，并同步这些变动；根据某种“负载均衡策略”选择 **主服务** 进行请求。

  参考： `Ribbon的负载均衡策略及原理 <https://blog.csdn.net/wudiyong22/article/details/80829808>`_

bootstrap.yml 和application.yml
  - bootstrap.yml（bootstrap.properties）先于 application.yml（application.properties）加载。
  - bootstrap.yml 用于应用程序上下文的引导阶段，由父Spring ApplicationContext加载。而父ApplicationContext 被加载在使用 application.yml 的之前。
  - bootstrap.yml 可以理解成系统级别的一些参数配置，这些参数一般是不会变动的。
  - application.yml 可以用来定义应用级别的，如果搭配 spring-cloud-config 使用 application.yml 里面定义的文件可以实现动态替换。

    使用Spring Cloud Config Server时，应在 bootstrap.yml 中指定： ``spring.application.name`` 、 ``spring.cloud.config.server.git.uri`` 以及一些加密/解密信息。

.. _the_explain_of_thread_isolation:

线程隔离
:::::::::::::::::::::
“舱壁模式”对于熟悉Docker的读者一定不陌生，Docker通过“舱壁模式”实现进程的隔离，使得容器与容器之间不会互相影响。而Hystrix则使用该模式实现线程池的隔离，它会为每一个Hystrix命令创建一个独立的线程池，这样就算某个在Hystrix命令包装下的依赖服务出现延迟过高的情况，也只是对该依赖服务的调用产生影响，而不会拖慢其他的服务。

通过对依赖服务的线程池隔离实现，可以带来如下 **优势** ：

- 应用自身得到完全的保护，不会受不可控的依赖服务影响。即便给依赖服务分配的线程池被填满，也不会影响应用自身的额其余部分。
- 可以有效的降低接入新服务的风险。如果新服务接入后运行不稳定或存在问题，完全不会影响到应用其他的请求。
- 当依赖的服务从失效恢复正常后，它的线程池会被清理并且能够马上恢复健康的服务，相比之下容器级别的清理恢复速度要慢得多。
- 当依赖的服务出现配置错误的时候，线程池会快速的反应出此问题（通过失败次数、延迟、超时、拒绝等指标的增加情况）。同时，我们可以在不影响应用功能的情况下通过实时的动态属性刷新（后续会通过Spring Cloud Config与Spring Cloud Bus的联合使用来介绍）来处理它。
- 当依赖的服务因实现机制调整等原因造成其性能出现很大变化的时候，此时线程池的监控指标信息会反映出这样的变化。同时，我们也可以通过实时动态刷新自身应用对依赖服务的阈值进行调整以适应依赖方的改变。
- 除了上面通过线程池隔离服务发挥的优点之外，每个专有线程池都提供了内置的并发实现，可以利用它为同步的依赖服务构建异步的访问。

总之，通过对依赖服务实现线程池隔离，让我们的应用更加健壮，不会因为个别依赖服务出现问题而引起非相关服务的异常。同时，也使得我们的应用变得更加灵活，可以在不停止服务的情况下，配合动态配置刷新实现性能配置上的调整。

同时，我们也无需担心“为每一个依赖服务都分配一个线程池是否会过多地增加系统的负载和开销”。Netflix在设计Hystrix的时候，认为线程池上的开销相对于隔离所带来的好处是无法比拟的。并做了相关测试。

Hystrix中除了使用线程池之外，还可以使用信号量来控制单个依赖服务的并发度，信号量的开销要远比线程池的开销小得多，但是它不能设置超时和实现异步访问。所以，只有 **在依赖服务是足够可靠的情况下才使用信号量** 。在HystrixCommand和HystrixObservableCommand中2处支持信号量的使用：

- 命令执行：如果隔离策略参数execution.isolation.strategy设置为SEMAPHORE，Hystrix会使用信号量替代线程池来控制依赖服务的并发控制。
- 降级逻辑：当Hystrix尝试降级逻辑时候，它会在调用线程中使用信号量。

信号量的默认值为10，我们也可以通过动态刷新配置的方式来控制并发线程的数量。对于信号量大小的估算方法与线程池并发度的估算类似。仅访问内存数据的请求一般耗时在1ms以内，性能可以达到5000rps，这样级别的请求我们可以将信号量设置为1或者2，我们可以按此标准并根据实际请求耗时来设置信号量。

.. _the-explain-of-circuit-breaker:

断路器
::::::::::::::
又叫 **服务熔断** 。

“断路器”本身是一种开关装置，用于在电路上保护线路过载，当线路中有电器发生短路时，“断路器”能够及时的切断故障电路，防止发生过载、发热、甚至起火等严重后果。

在分布式架构中，断路器模式的作用也是类似的，当某个服务单元发生故障（类似用电器发生短路）之后，通过断路器的故障监控（类似熔断保险丝），直接切断原来的主逻辑调用。但是，在Hystrix中的断路器除了切断主逻辑的功能之外，还有 *备用逻辑* 或者是 *更复杂的逻辑* ，这些备用逻辑会在断路器打开时被使用。

断路器开启的条件涉及到断路器的三个重要参数：快照时间窗、请求总数下限、错误百分比下限。这个参数的作用分别是：

- **快照时间窗** ：断路器确定是否打开需要统计一些请求和错误数据，而统计的时间范围就是快照时间窗，默认为最近的10秒。
- **请求总数下限** ：在快照时间窗内，必须满足请求总数下限才有资格根据熔断。默认为20，意味着在10秒内，如果该hystrix命令的调用此时不足20次，即时所有的请求都超时或其他原因失败，断路器都不会打开。
- **错误百分比下限** ：当请求总数在快照时间窗内超过了下限，比如发生了30次调用，如果在这30次调用中，有16次发生了超时异常，也就是超过50%的错误百分比，在默认设定50%下限情况下，这时候就会将断路器打开。

那么当断路器打开之后会发生什么呢？当熔断器在10秒内发现请求总数超过20，并且错误百分比超过50%，这个时候熔断器打开。打开之后，再有请求调用的时候，将不会调用主逻辑，而是直接调用降级逻辑，这个时候就不会等待5秒之后了。在 *示例代码* 中就是，直接调用 ``fallback`` 方法。通过断路器，实现了自动地发现错误并将降级逻辑切换为主逻辑，减少响应延迟的效果。

在断路器打开之后，处理逻辑并没有结束，我们的降级逻辑已经被成了主逻辑，那么原来的主逻辑要 **如何恢复** 呢？对于这一问题，hystrix也为我们实现了自动恢复功能。当断路器打开，对主逻辑进行熔断之后，hystrix会启动一个休眠时间窗，在这个时间窗内，降级逻辑是临时的成为主逻辑，当休眠时间窗到期，断路器将进入半开状态，释放一次请求到原来的主逻辑上，如果此次请求正常返回，那么断路器将继续闭合，主逻辑恢复，如果这次请求依然有问题，断路器继续进入打开状态，休眠时间窗重新计时。

通过上面的一系列机制，hystrix的断路器实现了对依赖资源故障的端口、对降级策略的自动切换以及对主逻辑的自动恢复机制。这使得我们的微服务在依赖外部服务或资源的时候得到了非常好的保护，同时对于一些具备降级逻辑的业务需求可以实现自动化的切换与恢复，相比于设置开关由监控和运维来进行切换的传统实现方式显得更为智能和高效。

异常解决
^^^^^^^^^^^^^^^^^^^^^^^
Type javax.xml.bind.JAXBContext not present
  在 *服务治理* 部分，使用jdk8以上的版本（如 *版本11* ）启动 **Eureka** 时，会报此异常。解决方法，切换到 **1.8** 版本。

问题
^^^^^^^^^^^^^^^^^^^^^^
- 什么是 **负载均衡** ， **Ribbon** 又能在这方面做些什么？
- ``Spring Cloud Config Client`` 项目：在该项目的Java代码中，可以直接获取某些配置吗？
- 一个应用中被 ``@HystrixCommand`` 的 *方法* 是否有数量上限。。
- 在 *Eureka* 与 *Ribbon* （或 *Feign* ）中，是否可以不同应用使用相同 *Application Name* （或者说 *Application ID* ）？这样是否能够做到，一个服务坏掉或负载过高时，使用另一个可用、低负载的 *同名* 服务？

其他
^^^^^^^^^^^^^^^^^^^^^^
Eureka的自我保护机制
  当我们将客户端关闭后，再次打开Eureka的注册页面，发现有一串红字： ``EMERGENCY! EUREKA MAY BE INCORRECTLY CLAIMING INSTANCES ARE UP WHEN THEY'RE NOT. RENEWALS ARE LESSER THAN THRESHOLD AND HENCE THE INSTANCES ARE NOT BEING EXPIRED JUST TO BE SAFE.`` 。

  这是因为Eureka进入了自我保护机制，默认情况下，如果EurekaServer在一定时间内没有接收到某个微服务实例的心跳时，EurekaServer将会注销该实例（默认90s）。但是当网络发生故障时，微服务与EurekaServer之间无法通信，这样就会很危险了，因为微服务本身是很健康的，此时就不应该注销这个微服务，而Eureka通过自我保护机制来预防这种情况，当网络健康后，该EurekaServer节点就会自动退出自我保护模式；

  这时再次将客户端微服务启动，刷新服务注册中心会发现，自我保护状态已取消。

其他参考
^^^^^^^^^^^^^^^^^^^^^
- 《Spring Cloud微服务实战》
- `Spring Cloud 微服务实战 <https://www.cnblogs.com/judesheng/p/10622189.html>`_

.. |the_diagram_of_eureka_server| image:: /images/special_subject/distributed/001_the_diagram_of_eureka_server.jpeg
   :width: 80%
