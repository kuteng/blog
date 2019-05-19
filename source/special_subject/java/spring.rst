Spring
=====================


安装
^^^^^^^^^^^^^^^^^
- 构建项目
  - 使用spring-boot-cli创建一个项目： ``spring init --build gradle cloud``

注解解惑
^^^^^^^^^^^^^^^^^^
@Profile
  被 ``@Profile`` 标注的组件只有当指定profile值匹配时才生效。

  可以通过以下方式设置profile值：

  - 设置 ``spring.profiles.active`` 属性（通过JVM参数、环境变量或者web.xml中的Servlet context参数）
  - ``ApplicationContext.getEnvironment().setActiveProfiles("ProfileName")``

  **profile** 除了可以以 *注解* 的形式使用外，还可以在Bean的XML配置中使用。如：

    .. code-block:: xml

      <beans>
        <context:component-scan base-package="com.websystique.spring"/>
        <beans profile="Development">
          <import resource="dev-config-context.xml"/>
        </beans>
        <beans profile="Production">
          <import resource="prod-config-context.xml"/>
        </beans>
      </beans>

@Value
  该注解可以读取系统参数。

  - 指定加载配置的读取，如： ``@Value("#{configProperties['t1.msgname']}")`` ，该语句的前提是有这个配置：

    .. code-block:: xml

      <bean id="configProperties" class="org.springframework.beans.factory.config.PropertiesFactoryBean">
        <property name="locations">
          <list>
            <value>classpath:/config/t1.properties</value>
          </list>
        </property>
      </bean>

  - 不需要指定具体加载配置对象的读取，如： ``@Value("${server.port}")`` 。
  - ``@Value()`` 中 *参数字符串* 有两种情况： ``#{}`` 、 ``${}`` ，分别是 **SpEL表达式** 和 *正常配置名* 。使用 ``#{}`` 时需要注意，properties配置文件中的属性名称 **不能带点** ，否则取不到值，会报错。

@PropertySource
  这是类上的注解，用于读取非 ``application.properties`` 中的配置信息。它一般与 ``@Value`` 配合使用。 如

  .. code-block:: java

    @PropertySource({"classpath:config/my.properties","classpath:config/config.properties"})
    public class Test {
        @Value("${my.name}")
        private String myName;

        ...
    }

@SpringCloudApplication
  它包含了三个注解 ``@SpringBootApplication`` 、 ``@EnableDiscoveryClient`` 、 ``@EnableCircuitBreaker`` 。这意味着被该注解修饰的应用，包含了 *服务治理* 和 *断路器* 。也标明一个Spring Cloud标准应用应包含服务发现以及断路器。

@Slf4j
  - 被该注解修饰的类内，可以直接使用语句 ``log.info("debug message");`` 进行log输出。不论是否为静态方法。
  - 它需要依赖：

    .. code-block:: xml

      <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
      </dependency>

  - 开发过程中，IDE需要安装 *lombok插件* 。

@PropertySource("classpath:properties/thread-pool.properties")
  加载 properties 配置文件。可用在被 @Service 等注解修饰的类上。

@ImportResource("classpath:beans.xml")
  修饰 **驱动类** （一般为 ``Application`` ），可加载相关 bean 的配置文件。让配置文件与注解并存。

配置解惑
^^^^^^^^^^^^^^^^^^^^
spring.profiles.active
  这是 ``application.propertie`` 中的配置。

  当出现 ``spring.profiles.active=dev`` ，意味着该应用是指定使用application-dev.properties文件进行配置。

语句解惑
^^^^^^^^^^^^^^^^^^^
``context.scan("com.websystique.spring");``
  扫描制定包。 ``context`` 的出处如下： ``AnnotationConfigApplicationContext  context = new AnnotationConfigApplicationContext();`` 。

难题解惑
^^^^^^^^^^^^^^^^^^
读取配置
  除了使用前面说的 ``@Value`` 注解外，还可以使用 ``Environment`` 。如下：

  .. code-block:: java

    @RestController
    public class GatewayController {
        @Value("${demo.name}")
        public String demoName;

        @Autowired
        private Environment environment;

        @RequestMapping(value = "/gateway")
        public String gateway() {
            //1、使用@Value注解读取
            String proFromValue = demoName;
            //2、使用Environment读取
            String proFromEnv = environment.getProperty("demo.sex")

            return "get properties value by ''@Value'' : name=" + proFromValue +
                    "<p>get properties value by ''Environment'' : sex=" + proFromEnv +
                    " , address=" + environment.getProperty("demo.address");" + " i
        }
    }
