Spring IoC 容器
=====================

网址：`Version 5.1.5.RELEASE <https://docs.spring.io/spring-framework/docs/5.1.5.RELEASE/spring-framework-reference/core.html>`_

备忘
^^^^^^^^^^^^
- IOC是一个对象定义自己依赖关系的过程。定义的方式有三种：通过构造方法及其参数；通过工厂方法及其参数；通过构造(工厂创造）完成后对象的set方法。 `依赖关系` 可以理解为：可以理解为创建某个对象需要的 `类` 及 `其他对象实例` ,类似于构造方法与方法参数。
- ``ApplicationContext`` 接口就是 *Spring* 里的 `IoC容器` ，它负责Bean的实例化、配置和组装。

  ``ApplicationContext`` 是 ``BeanFactory`` 的子接口。相对与后者，前者有如下特性。

  - 它更容易与 Spring 的 **AOP** 功能集成
  - 消息资源处理（用于国际化）
  - 事件发布功能( `Event publication` )
  - 特定于应用程序层的上下文，例如 ``WebApplicationContext`` （这是用于Web应用程序的）。

- `Bean` : 在 `Spring` 中，构成应用程序主干的对象和被 `Spring IoC` 容器管理的对象都称之为 ``Bean`` ，它是完成 `实例化` 完成 `组装` 的。
- `配置元数据` : `Bean` 和它们间的依赖关系是记录在 `配置元数据` 中的。Spring 中的 `IoC` 容器也是通过读取 `配置元数据` 获取有关对象的说明（及要如何实例化、配置和组装）。它可以通过下面三种方式定义： `XML文件` 、 `Java注解` 和 `Java代码` 。
- Spring 为 ``ApplicationContext`` 提供了许多实现，而在 `stand-alone` 应用程序中常用的做法是创建一个 ``ClassPathXmlApplicationContext`` 或 ``FileSystemXmlApplicationContext`` 的实例。Spring-boot中，默认使用 ``AnnotationConfigApplicationContext`` 。
- `Spring MVC` 中，通过 ``web.xml`` 的八行内容就创建了一个 `IoC容器` ： ::

    <context-param>
        <param-name>contextConfigLocation</param-name>
        <param-value>/WEB-INF/daoContext.xml /WEB-INF/applicationContext.xml</param-value>
    </context-param>

    <listener>
        <listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
    </listener>

- 疑问： ``Annotation-based configuration`` 与 ``Java-based configuration`` 的区别？
- 配置Bean的时候， `XML-base` 方式中使用 ``<beans>`` 里的 ``<bean>`` 进行配置；而 `java configuration` 方式中是使用在被 ``@Configuration`` 注解的类中被 ``@Bean`` 注解的方法（对于 `java configuration` 中有些疑惑）。

  在 `XML-base` 方式中， ``<bean>`` 标签有 ``id`` 和 ``class`` 属性。 ``id`` 是唯一的，且用它可以参与到与其他Bean的“协作”中。

  `XML-base` 的示例：

  .. code-block:: xml

    <?xml version="1.0" encoding="UTF-8"?>
    <beans xmlns="http://www.springframework.org/schema/beans"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xsi:schemaLocation="http://www.springframework.org/schema/beans
            http://www.springframework.org/schema/beans/spring-beans.xsd">
        <bean id="accountDao"
            class="org.springframework.samples.jpetstore.dao.jpa.JpaAccountDao">
            <!-- additional collaborators and configuration for this bean go here -->
        </bean>

        <bean id="petStore" class="org.springframework.samples.jpetstore.services.PetStoreServiceImpl">
            <property name="accountDao" ref="accountDao"/>
            <property name="itemDao" ref="itemDao"/>
            <!-- additional collaborators and configuration for this bean go here -->
        </bean>

        <bean id="itemDao" class="org.springframework.samples.jpetstore.dao.jpa.JpaItemDao">
            <!-- additional collaborators and configuration for this bean go here -->
        </bean>

        <bean id="..." class="...">
            <!-- collaborators and configuration for this bean go here -->
        </bean>
        <!-- more bean definitions go here -->
    </beans>

  默认情况下， `Spring IoC` 容器不强制要求Bean的先后顺序，如上例中的 `petStore` 和 `itemDao` 。

- 我们创建Bean对象时，通常是创建服务层对象、数据访问对象（如Dao）、 `Struts Actions` 、基础结构对象（如 `Hibernate SessionFactories` 、 `JMS Queues` ）。而很少使用 `Bean` 去定义 “细粒度”的对象，将它们交给业务层自己服务就好。
- 在 `java code` 中手动创建 `IoC容器` 的方法：

  .. code-block:: java

    ApplicationContext context = new ClassPathXmlApplicationContext("services.xml", "daos.xml");

- 如上例，将 `Bean` 根据性质不同，分别定义在多个 `XML` 文件中，是被推荐的。通常，每个单独的XML配置文件都代表架构中的一个逻辑层或模块。
- 前面的例子是在创建 `IOC容器` 时读多个配置文件，但在这里，我们可以将多个配置文件集合在一个文件中。这样方便管理。

  .. code-block:: xml

    <beans>
        <import resource="services.xml"/>
        <import resource="daos.xml"/>
        <import resource="resources/messageSource.xml"/>
        <import resource="/resources/themeSource.xml"/>

        <bean id="bean1" class="..."/>
        <bean id="bean2" class="..."/>
    </beans>

  注意例子中 `messageSource.xml` 和 `themeSource.xml` 文件位置是一样的，都是在resource文件夹里面。

- Java路径引用是，不建议使用 ``../`` 的方式调用父路径（可以但不建议使用），特别是在对于 ``classpath`` URLs，例如： ``classpath:../services.xml`` 。

  Java路径引用中，如果要访问程序文件外的资源的话，可以这样： ``file:C:/config/services.xml`` 或 ``classpath:/config/services.xml`` 。

- 听说 `Bean` 还可以通过 ``Spring’s Groovy Bean Definition DSL`` 方式定义。我目前对它的求知欲不高，所以略略略...
- Spring中，可以通过 ``ApplicationContext`` 的 ``T getBean(String name, Class<T> requiredType)`` 方法使用 `IoC容器` 。如下：

  .. code-block:: java

    // 配置和创建Bean。
    ApplicationContext context = new ClassPathXmlApplicationContext("services.xml", "daos.xml");

    // 检索已配置的Bean实例。
    PetStoreService service = context.getBean("petStore", PetStoreService.class);

    // 使用这个已配置的Bean实例
    List<String> userList = service.getUsernameList();

- ``BeanDefinition`` ：在Spring的 `IoC` 容器中，所有的Bean信息都存储在 `BeanDefinition` 里。它里面主要有如下信息：

  - 该Bean的实现类。
  - `Bean behavioral configuration elements` ，说明bean在容器中的行为方式（如：范围(scope)，生命周期回调等）。其中 ``Scope`` 的默认选项是“单例”和“原型”，我个人就认为 ``Scope`` 定义了它生命周期的类型。
  - 该Bean的依赖项：及执行工作需要的其他Bean。
  - 要在新创建的对象中设置的其他配置设置 - 例如，池的大小限制或在管理连接池的Bean中使用的连接数。

- ``BeanDefinition`` 的主要构成：

  +--------------------------+-----------------------------------------+
  | Property                 |   扩展阅读                              |
  +==========================+=========================================+
  | 类(Class)                | `Instantiating Beans`_                  |
  +--------------------------+-----------------------------------------+
  | 名称（Name）             | `Naming Beans`_                         |
  +--------------------------+-----------------------------------------+
  | 范围（Scope）            | `Bean Scopes`_                          |
  +--------------------------+-----------------------------------------+
  | 构造方法的参数           | `Dependency Injection`_                 |
  +--------------------------+-----------------------------------------+
  | 其他属性                 | `Dependency Injection`_                 |
  +--------------------------+-----------------------------------------+
  | 自动装配模式             | `Autowiring Collaborators`_             |
  +--------------------------+-----------------------------------------+
  | 延迟初始化模式           | `Lazy-initialized Beans`_               |
  +--------------------------+-----------------------------------------+
  | Bean创建后的回调         | `Initialization Callbacks`_             |
  +--------------------------+-----------------------------------------+
  | 销毁前的回调方法         | `Destruction Callbacks`_                |
  +--------------------------+-----------------------------------------+

.. _Instantiating Beans: https://docs.spring.io/spring-framework/docs/5.1.5.RELEASE/spring-framework-reference/core.html#beans-factory-class
.. _Naming Beans: https://docs.spring.io/spring-framework/docs/5.1.5.RELEASE/spring-framework-reference/core.html#beans-beanname
.. _Bean Scopes: https://docs.spring.io/spring-framework/docs/5.1.5.RELEASE/spring-framework-reference/core.html#beans-factory-scopes
.. _Dependency Injection: https://docs.spring.io/spring-framework/docs/5.1.5.RELEASE/spring-framework-reference/core.html#beans-factory-collaborators
.. _Autowiring Collaborators: https://docs.spring.io/spring-framework/docs/5.1.5.RELEASE/spring-framework-reference/core.html#beans-factory-autowire
.. _Lazy-initialized Beans: https://docs.spring.io/spring-framework/docs/5.1.5.RELEASE/spring-framework-reference/core.html#beans-factory-lazy-init
.. _Initialization Callbacks: https://docs.spring.io/spring-framework/docs/5.1.5.RELEASE/spring-framework-reference/core.html#beans-factory-lifecycle-initializingbean
.. _Destruction Callbacks: https://docs.spring.io/spring-framework/docs/5.1.5.RELEASE/spring-framework-reference/core.html#beans-factory-lifecycle-disposablebean

- 获得 `BeanFactory` 的的方法： ``ApplicationContext.getAutowireCapableBeanFactory()`` 和 ``ConfigurableApplicationContext.getBeanFactory()`` 。因为 ``SpringApplication.run(Class<>, String...)`` 的返回类型是 ``ConfigurableApplicationContext`` ，所以我认为后者更常用（后者接口继承自前者）。

  这里的 ``getBeanFactory`` 方法返回的类型是 ``DefaultListableBeanFactory`` ，不过我们需要 `强转` 。

  通过 ``DefaultListableBeanFactory`` 的 ``registerSingleton(..)`` 或 ``registerBeanDefinition(..)`` ，我们可以跳出`IoC容器` 的 **配置元数据** 手动注册Bean。虽然我不认为这是应该被提倡的。

- 每个bean都有一个或多个标识符。这些标识符在托管bean的容器中必须是唯一的。 bean通常只有一个标识符（ ``id`` ）。但是，如果它需要多个，则额外的可以被视为别名（ ``alias`` ）。需要注意：Bean标识符的唯一性是由Bean容器执行的，而非XML解析器。使用标识符，方便 ``getBean`` 和配置Bean间的依赖。如果我们没有手动为Bean定义标识符，容器也会自动为其生成的。依照惯例，这些标识符的命名需要遵循“驼峰”。

  在 `XML-base` 中，默认使用 ``id`` 属性作为标识。如果要多个标识的话，可以使用 ``name`` 属性，在这个属性中，多个标识使用 ``,`` 、 ``;`` 或者 `空格` 进行分割。通过 ``ref`` 属性指向被依赖的 *Bean* 的标识符。

  除了使用 ``name`` 外， `XML-base` 里还提供了标签 ``<alias>`` 来定义别名。它的应用场景是：为其他组件里的bean定义别名，例如：在文件 `services.xml` 中为文件 `dao.xml` 里某个Bean定义别名。示例：

  .. code-block:: xml

    <beans>
      <bean id="baseBean" name="name1, name2" class="..."/>

      <alias name="baseBean" alias="systemA-alias1"/>
      <alias name="baseBean" alias="systemB-alias2"/>
    </beans>

  如上例，两个子系统中分别使用别名 ``systemA-alias1`` 、 ``systemB-alias2`` ，而主系统中使用 ``baseBean`` 。

- `Spring` 的 `IoC容器` 实例化一个Bean通过下面三种方式：

  - 通过 *构造方法* 创建Bean：就如同前面几个例子那样，提供一个能用的构造方法就好（注意构造方法的参数）。推荐的方式是所有的Bean都有“默认”构造方法（及无参数），所有属性都通过set、get方法来设置、访问。（这是我的推荐，但是 Spring 团队似乎并不这样认为。
  - 通过 *静态工厂方法* 创建Bean：通过其他类的 *静态工厂方法* 生成Bean对象。如下：

    .. code-block:: xml

      <bean id="clientService" class="examples.ClientService" factory-method="createInstance"/>

    .. code-block:: java

      public class ClientService {
          private static ClientService clientService = new ClientService();
          private ClientService() {}

          public static ClientService createInstance() {
              return clientService;
          }
      }

    `XML` 里面， ``class`` 属性的值并非 *Bean的类* 而是工厂方法所在的类（虽然在此例中这个 *类* 都是一个类）； ``factory-method`` 属性指向 *静态工厂方法* ，当然这个方法也是可以有参数（并配置好参数）的。

    每次看着这个例子，我都会想到单例。不过有了 `Spring Ioc` ，不需要手动创建 *单例* ！

    实验证明，默认情况下，哪怕没有示例中的静态成员，Bean依旧是保证 *单例* 状态的。

  - 通过 *实例工厂方法* 创建Bean：通过其他Bean的 *工厂方法* 生成Bean对象。如下：

    .. code-block:: xml

      <!-- the factory bean, which contains a method called createInstance() -->
      <bean id="serviceLocator" class="examples.DefaultServiceLocator">
          <!-- inject any dependencies required by this locator bean -->
      </bean>

      <!-- the bean to be created via the factory bean -->
      <bean id="clientService" factory-bean="serviceLocator" factory-method="createClientServiceInstance"/>
      <bean id="accountService" factory-bean="serviceLocator" factory-method="createAccountServiceInstance"/>

    .. code-block:: java

      public class DefaultServiceLocator {
          private static ClientService clientService = new ClientServiceImpl();
          private static AccountService accountService = new AccountServiceImpl();

          public ClientService createClientServiceInstance() {
              return clientService;
          }

          public AccountService createAccountServiceInstance() {
              return accountService;
          }
      }

    `XML` 里，第一个 *Bean* 是 *工厂方法* 所在的类。第二个Bean中 ``factory-bean`` 属性指向 *工厂方法* 所在的Bean（及第一个Bean）， ``factory-method`` 属性是 *工厂方法* 的名称。

    注意，如示例所示，一个 *工厂对象* 中可以放多个工厂方法。

    实验证明，默认情况下，哪怕没有示例中的静态成员，Bean依旧是保证 *单例* 状态的。

- 注意：在名称中使用 ``$`` 字符可以将嵌套类名与外部类名分开。场景：加入我们有一个类 ``com.example.SomeThing`` ，它里面还有一个静态类 ``OtherThing`` 。如果要对 ``OtherThing`` 进行Bean注册，需要在 `XML` 里这样写：

  .. code-block:: xml

    <bean id="otherThing" class="com.example.SomeThing$OtherThing"/>

- **依赖注入** ( *DI* )：几乎所有的项目都会出现，想要创建某个Bean时，需要某些其他的Bean或值作为参数成为这个Bean的 *成员* 。我们定义这些Bean的过程，就被成为 **依赖注入** 。
- *依赖注入* 有三种方式：

  - 通过构造方法的参数。 ``<constructor-arg>``
  - 通过工程方法的参数。 ``<constructor-arg>``
  - 通过set方法。 ``<property>``

- 三种注入的例子对比：

  - 构造方法注入：

    .. code-block:: xml

      <bean id="exampleBean" class="examples.ExampleBean">
          <!-- constructor injection using the nested ref element -->
          <constructor-arg>
              <ref bean="anotherExampleBean"/>
          </constructor-arg>

          <!-- constructor injection using the neater ref attribute -->
          <constructor-arg ref="yetAnotherBean"/>
          <constructor-arg type="int" value="1"/>
      </bean>

      <bean id="anotherExampleBean" class="examples.AnotherBean"/>
      <bean id="yetAnotherBean" class="examples.YetAnotherBean"/>

    .. code-block:: java

      public class ExampleBean {
          private AnotherBean beanOne;
          private YetAnotherBean beanTwo;
          private int i;

          public ExampleBean(
              AnotherBean anotherBean, YetAnotherBean yetAnotherBean, int i) {
              this.beanOne = anotherBean;
              this.beanTwo = yetAnotherBean;
              this.i = i;
          }
      }

  - *setter* 方法注入：

    .. code-block:: xml

      <bean id="exampleBean" class="examples.ExampleBean">
          <!-- setter injection using the nested ref element -->
          <property name="beanOne">
              <ref bean="anotherExampleBean"/>
          </property>

          <!-- setter injection using the neater ref attribute -->
          <property name="beanTwo" ref="yetAnotherBean"/>
          <property name="integerProperty" value="1"/>
      </bean>

      <bean id="anotherExampleBean" class="examples.AnotherBean"/>
      <bean id="yetAnotherBean" class="examples.YetAnotherBean"/>

    .. code-block:: java

      public class ExampleBean {
          private AnotherBean beanOne;
          private YetAnotherBean beanTwo;
          private int i;

          public void setBeanOne(AnotherBean beanOne) {
              this.beanOne = beanOne;
          }
          public void setBeanTwo(YetAnotherBean beanTwo) {
              this.beanTwo = beanTwo;
          }
          public void setIntegerProperty(int i) {
              this.i = i;
          }
      }

  - 工厂方法注入（已经静态方法为例）：

    .. code-block:: xml

      <bean id="exampleBean" class="examples.ExampleBean" factory-method="createInstance">
          <constructor-arg ref="anotherExampleBean"/>
          <constructor-arg ref="yetAnotherBean"/>
          <constructor-arg value="1"/>
      </bean>

      <bean id="anotherExampleBean" class="examples.AnotherBean"/>
      <bean id="yetAnotherBean" class="examples.YetAnotherBean"/>

    .. code-block:: java

      public class ExampleBean {
          // a private constructor
          private ExampleBean(...) {
              ...
          }

          // a static factory method; the arguments to this method can be
          // considered the dependencies of the bean that is returned,
          // regardless of how those arguments are actually used.
          public static ExampleBean createInstance (
              AnotherBean anotherBean, YetAnotherBean yetAnotherBean, int i)
          {

              ExampleBean eb = new ExampleBean (...);
              // some other operations...
              return eb;
          }
      }

    工厂方法的参数依旧使用标签 ``<constructor-arg/>`` 进行配置。

- 在 *通过构造方法的参数* 进行依赖注入的时候，需要注意默认情况下提供参数的顺序（ ``<constructor-arg>`` 的顺序）与构造方法的参数顺序一致，特别是存在两个类型相同的参数时。不过如何参数的类型不同， `IoC容器` 会根据参数类型，将其与构造方法的参数进行匹配。 **我推荐两者的顺序保持一致** 。
- 在 *通过构造方法的参数* 中，为了保证参数不会混乱，除了让其顺序与构造方法的顺序保持一致这种方式外，可以通通过 ``name`` 参数和 ``index`` 参数这两种方式，前者对应参数名称，后者对应参数顺序。

  - 使用 ``name`` 方式时，需要要注意在编译时开启 ``debug flag`` ，或者在构造方法上使用注解 ``@ConstructorProperties`` 如：

    .. code-block:: java

      public class ExampleBean {
          @ConstructorProperties({"years", "ultimateAnswer"})
          public ExampleBean(int years, String ultimateAnswer) {...}
      }

  - 使用 ``index`` 方式时，需要注意它的 *下标* 是从 ``0`` 开始的。

- 如果构造方法的某些参数是 *基本类型* 。就需要借助属性 ``type`` 和 ``value`` 了。如：

  .. code-block:: xml

    <bean id="exampleBean" class="examples.ExampleBean">
        <constructor-arg type="int" value="7500000"/>
        <constructor-arg type="java.lang.String" value="42"/>
    </bean>

  我们也可以通过 ``index`` 属性告诉容器该参数的位置，如：

  .. code-block:: xml

    <bean id="exampleBean" class="examples.ExampleBean">
        <constructor-arg index="0" value="7500000"/>
        <constructor-arg index="1" value="42"/>
    </bean>

  疑问：如果是 ``Date`` 、 ``DateTimestemp`` 类型该怎么办呢？

  实际上通过 *默认顺序* 且不标明类型和名词的方式应对 *基本类型* 的参数也是可以的。虽然我 **不提倡** ，因为它容易出错，如：

  .. code-block:: xml

    <bean id="exampleBean" class="examples.ExampleBean">
        <constructor-arg value="asdfa"/>
        <constructor-arg value="7500000"/>
    </bean>

  上面的例子就会报错，因为 *容器* 无法判断这里的哪个参数的类型是 ``String`` 哪个参数的类型是 ``Integer`` ，它只会默认配置中参数的顺序有构造方法的参数顺序一致，然后就出现了将字符串 ``asdfa`` 强转为 ``Integer`` ，所以报错。

- ``BeanDefinition`` 与 ``PropertyEditor`` 一起将XML中的参数值转化为 **正确的类型** 。

  ``PropertyEditor`` 的官方实现中，大部分都是重写 ``setAsText`` 方法，在这里完成 *String -> Object* 的过程。

- 关于注解的一些备忘： ``@Component`` 、 ``@Controllder`` 、 ``@Configuration`` 注解在 `类` 上，而 ``@Bean`` 注解在 ``@Configuration`` 类的方法中。

- 在 *setter* 方法上使用 ``@Required`` 注解可以告诉容器，这里有该 Bean 必须依赖。我觉的与 Bean 类属性中的 ``@Autowired`` 一样（虽然该文档中到目前还没有说到）。
- *Spring* 的开发团队推荐使用 *构造方法注入* ，因为它保证Bean的组成（主要是依赖项、成员属性）的不可变和不会出现空依赖项。通过 *构造方法注入* ，那么在容器中构造完此对象后就是 **完整** 的了。

  需要注意的是：拥有大量 *构造参数* 的构造方法是一种 **坏** 的代码风格，它暗示了该类承担了太多的责任， **我们需要重构它** 。

  而相对的 *setter注入* 应该用于“可选”依赖项，这些依赖项是有默认值的，否则我们需要在使用该依赖项的每个位置记性 **非空检验** 。

  在使用第三方包的时候需要注意一种情况：某些依赖它只提供了一种注入接口，构造方法或setter方法。当然我怀疑还有一种情况提供的 **setter** 方法的 *方法名* 并不规范（及不是一 `set` 开头的）。

  虽然 Spring 团队推荐 *构造方法注入* ，但是我更喜欢 *setter注入* ，认为它更友好。特别是面临 **循环依赖** 的时候。

- 细说依赖解析的过程：

  - 首先创建 ``ApplicationContext`` 对象并根据 *配置数据元* （包含了所有Bean的信息）初始化该对象。
  - 将所有的Bean的依赖汇总成 **表单** （包含构造方法参数、工厂方法参数、类的成员）。在每个Bean创建时，将该信息提供给这个Bean。
  - 确保前面表单中的每个方法参数或类的成员都指向明确的 **值** 或容器中的其他Bean。
  - 将参数或成员的值转换为 *正确* 类型，然后在交给Bean。

- `Spring容器` 默认情况下，会在容器创建时首先将所有的 *作用于为单例* 的Bean 全部创建完成。当然我们可以更改为 *用到时创建* ，我觉的可以称后者为 **惰性加载单例Bean** 。

  需要注意，在 **实例Bean** 和 设置为单例Bean **惰性加载** 的时候， **依赖项之间的解决方案不匹配** 这个问题可以会发现的比较晚，及在创建相关Bean的时候才会发现。

- **循环依赖** 只能通过 *setter注入* 来解决， *构造方法注入* 和 *工厂方法注入* 会报 ``BeanCurrentlyInCreationException`` 错误的。
- 因为 ``Spring容器`` 会尽可能晚的 *set* 属性和解析依赖，而且 *实例Bean* 是 **惰性加载** 的，所以对于相关的配置异常和依赖错误， *容器* 会发现的比较晚。

  这也是为什么 ``Spring容器`` 对于单例Bean，默认情况下是提前创建的。这样虽然花费了一些时间和内存的代价，但是我们可以提前发现配置问题（在启动时就发现而不是几天之后）。

- 还有一种方法能够提前检验是使用 ``<idref>`` 标签，它能够容器在 **部署** 的时候检查此 ``bean`` 是否存在。所以使用 ``<idref>`` 可以有效的 **防止拼写错误** 。示例：

  .. code-block:: xml

    <bean id="theTargetBean" class="..."/>

    <bean id="theClientBean" class="...">
        <property name="targetName">
            <idref bean="theTargetBean"/>
        </property>
    </bean>

  ``<idref>`` 和 ``<ref>`` 标签的 ``local`` 属性，可以在 *XML解析* 的时候（还在 *部署* 之前）检查同一个 `XML` 文件内相关bean是否存在。

  注意，在 ``4.0`` 版本之后，对 ``<idref>`` 和 ``<ref>`` 标签里的 ``local`` 属性已经不进行支持了。所以做版本迁移的时候注意 **修改为 idref 标签** 。

- ``<ref>`` 标签与 ``<idref>`` 标签类似，不过它不会强制进行 *前置检验* 。两者都是用在 ``<constructor-arg>`` 和 ``<property>`` 下，具有属性 ``local`` 、 ``bean`` 和 ``parent`` 。其中 ``local`` 前面已经说过了， ``bean`` 是指向本容器内的依赖（Bean），而 ``parent`` 是指向父容器里的依赖。下一个内容有对 ``parent`` 属性的距离， ``bean`` 属性的用法与之相似。

- 如何创建一个有层次结构的 *IoC容器* （即有父容器有子容器）。 如下：

  .. code-block:: java

    package com.example;

    public class Test {
        private Test child;

        public void setChild(Test child) {
            this.child = child;
        }

        public Test getChild() {
            return child;
        }
    }


  .. code-block:: xml

    <!-- 父容器配置 parent.xml -->
    <?xml version="1.0" encoding="UTF-8"?>
    <beans xmlns="http://www.springframework.org/schema/beans"
      xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
      xsi:schemaLocation="http://www.springframework.org/schema/beans
            http://www.springframework.org/schema/beans/spring-beans.xsd">
      <bean id="test" class="com.example.Test">
      </bean>
      <bean id="test2" class="com.example.Test">
        <property name="child">
          <bean class="com.example.peter.test.Test">
            <property name="child">
              <!-- 这里也不能用 parent 属性，因为还是在一个容器内 -->
              <ref bean="test" />
            </property>
          </bean>
        </property>
      </bean>
    </beans>


  .. code-block:: xml

    <!-- 子容器配置 child.xml -->
    <?xml version="1.0" encoding="UTF-8"?>
    <beans xmlns="..." xmlns:xsi="..." xsi:schemaLocation="...">
      <bean id="child" class="com.example.Test">
        <property name="child">
          <ref parent="test"/>
        </property>
      </bean>
    </beans>

  .. code-block:: java

    public class Main {
        public static void main(String[] args) {
            // 创建父容器
            ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("bean.xml");
            // 创建子容器
            ClassPathXmlApplicationContext childContext = new ClassPathXmlApplicationContext(new String[] {"child.xml"}, context);

            Test test = context.getBean("test", Test.class);
            System.out.println(test);

            Test child = childContext.getBean("child", Test.class);
            System.out.println(child);
            System.out.println(child.getChild());
        }
    }

  注意：通过 ``<import>`` 引入的XML文件 *不是* 子容器，这两个配置文件实际上是被同一个容器读取解析的。

- **内部Bean** 上面的例子中， ``parent.xml`` 文件里的Bean ``test2`` 里面就由一个 *内部Bean* 。

  它不需要 ``id`` 和 ``name`` 属性，因为外部的其他Bean关联不到它。

  一般 *内部Bean* 与包含它的Bean共享作用域( *Scope* )，同期创建与销毁。

  一种不常见的情况，例如请求域( ``request-scope`` )。 **这里的内容我没有读懂** ，也没有找到相关的解释。它的意思是如下吗？在请求域中，对于一个包含 *单例Bean* 的内部Bean，它虽然伴随包含它的Bean一起创建，但是 **销毁回调** 让他参与到整个请求域的生命周期中，而不是随着 *包含它的Bean* 一起销毁。

- *容器* 对 *集合类型* ( ``Properties`` 、 ``Map`` 、 ``Set`` 、 ``List`` 、 ``Array`` 等）的配置举例：

  .. code-block:: xml

    <bean id="mappings"
        class="org.springframework.beans.factory.config.PropertyPlaceholderConfigurer">

        <!-- typed as a java.util.Properties -->
        <property name="properties">
            <value>
                jdbc.driver.className=com.mysql.jdbc.Driver
                jdbc.url=jdbc:mysql://localhost:3306/mydb
            </value>
        </property>
    </bean>

  例子中注意 ``<value>`` 标签的内容。

  容器会使用 ``PropertyEditor`` 对value的值进行转换。如果我们的项目中有 **新的自定义类型** ，不要忘了定义对应的 **PropertyEditor** 。

  这里还有个例子：

  .. code-block:: xml

    <bean id="moreComplexObject" class="example.ComplexObject">
      <!-- results in a setAdminEmails(java.util.Properties) call -->
      <property name="adminEmails">
        <props>
          <prop key="administrator">administrator@example.org</prop>
          <prop key="support">support@example.org</prop>
          <prop key="development">development@example.org</prop>
        </props>
      </property>
      <!-- results in a setSomeList(java.util.List) call -->
      <property name="someList">
        <list>
          <value>a list element followed by a reference</value>
          <ref bean="myDataSource" />
        </list>
      </property>
      <!-- results in a setSomeMap(java.util.Map) call -->
      <property name="someMap">
        <map>
          <entry key="an entry" value="just some string"/>
          <entry key ="a ref" value-ref="myDataSource"/>
        </map>
      </property>
      <!-- results in a setSomeSet(java.util.Set) call -->
      <property name="someSet">
        <set>
          <value>just some string</value>
          <ref bean="myDataSource" />
        </set>
      </property>
    </bean>

  `Map` 的 `key` 或 `value` 、 `Set` 的 `value` 可以是如下的元素： ::

    bean | ref | idref | list | set | map | props | value | null

- **集合合并** ( ``Collection Merging`` )

  子集合可以与父集合的内容进行合并，重复内容进行覆盖。开启的方式是 ``merge`` 属性 ( ``merge=true`` ) 如：

  .. code-block:: xml

    <beans>
      <bean id="parent" abstract="true" class="example.ComplexObject">
        <property name="adminEmails">
          <props>
            <prop key="administrator">administrator@example.com</prop>
            <prop key="support">support@example.com</prop>
          </props>
        </property>
      </bean>
      <bean id="child" parent="parent">
        <property name="adminEmails">
          <!-- the merge is specified on the child collection definition -->
          <props merge="true">
            <prop key="sales">sales@example.com</prop>
            <prop key="support">support@example.co.uk</prop>
          </props>
        </property>
      </bean>
    <beans>

  ``merge`` 属性的定义需要在 **低集合** 上定义，否则无效。

  ``<list>`` 元素的merge结果还是有 **顺序** 的，是父集合的元素在前，子集合的元素在后。注意 ``Array`` 与 ``List`` 的配置文件中的配置格式是 *一样* 的。

- XML配置文件中 *空* 与 *Null* 的表述（以String为例）：

  .. code-block:: xml

    <bean id="one" class="ExampleBean">
        <property name="email" value=""/>
    </bean>
    <bean id="two" class="ExampleBean">
        <property name="email">
            <null/>
        </property>
    </bean>

- XML配置文件中还有 ``p-namespace`` 和 ``p-namespace`` 两个命名空间，不过我排斥它。示例如下：

  .. code-block:: xml

    <beans xmlns="..." xmlns:xsi="..." xsi:schemaLocation="...">
      <bean name="john-classic" class="com.example.Person">
        <property name="name" value="John Doe"/>
        <property name="spouse" ref="jane"/>
      </bean>

      <!-- p-命名空间，定义了两个属性，其中一个属性还依赖了其他Bean -->
      <bean name="john-modern"
        class="com.example.Person"
        p:name="John Doe"
        p:spouse-ref="jane"/>

      <bean name="jane" class="com.example.Person">
        <property name="name" value="Jane Doe"/>
      </bean>

      <!-- c-命名空间示例 -->
      <!-- traditional declaration with optional argument names -->
      <bean id="beanOne" class="x.y.ThingOne">
         <constructor-arg name="thingTwo" ref="beanTwo"/>
         <constructor-arg name="thingThree" ref="beanThree"/>
         <constructor-arg name="email" value="something@somewhere.com"/>
      </bean>

      <!-- c-namespace declaration with argument names -->
      <bean id="beanOne" class="x.y.ThingOne" c:thingTwo-ref="beanTwo"
        c:thingThree-ref="beanThree" c:email="something@somewhere.com"/>

      <!-- 以下标定位构造方法参数 -->
      <bean id="beanOne" class="x.y.ThingOne" c:_0-ref="beanTwo" c:_1-ref="beanThree"
        c:_2="something@somewhere.com"/>

      <bean id="beanTwo" class="x.y.ThingTwo"/>
      <bean id="beanThree" class="x.y.ThingThree"/>
    </beans>

- **复合属性名称** ，我能够明白它的用意，但是从未见过用它的场景：

  .. code-block:: xml

    <bean id="something" class="things.ThingOne">
        <property name="fred.bob.sammy" value="123" />
    </bean>

- ``depends-on`` 属性：当Bean的某个 *依赖项* 没有通过任何注入方式（ *构造方法注入* 等）显式注入时。如：

  .. code-block:: xml

    <bean id="beanOne" class="ExampleBean" depends-on="manager,accountDao">
      <property name="manager" ref="manager" />
    </bean>

    <bean id="manager" class="ManagerBean" />
    <bean id="accountDao" class="x.y.jdbc.JdbcAccountDao" />

  如例子中所示，如果有多个依赖时，可以使用 ``,`` 、 ``;`` 或 `空格` 分开。

  ``depends-on`` 不仅可以定义Bean构造时的依赖关系，还可以销毁时的顺序。及 *被依赖的Bean* 要先于 *当前Bean* 进行销毁。注意：这个说法只实用于 **单例Bean** 。

- Bean的 **惰性加载** 或 **惰性初始化** 。可在 ``<bean>`` 标签上设置 ``lazy-init`` 属性 ( ``true`` or ``false`` )，定义某个Bean的初始化时间。也可以在 ``<beans>`` 标签上设置 ``default-lazy-init`` 属性，设置所有Bean的默认初始化时间。例如：

  .. code-block:: xml

    <!-- 默认情况下索引的Bean都是惰性初始化 -->
    <beans default-lazy-init="true">
      <!-- 根据默认设置这个Bean惰性初始化 -->
      <bean id="defult.set" class="com.something.ExpensiveToCreateBean"/>

      <!-- 显式设置这个Bean惰性初始化 -->
      <bean id="lazy" class="com.something.ExpensiveToCreateBean" lazy-init="true"/>

      <!-- 显式设置这个Bean提前初始化 -->
      <bean name="not.lazy" class="com.something.AnotherBean"/>

      <!-- 这个Bean不是单例Bean，所以 default-lazy-init 对齐无效，它一直是惰性初始化的 -->
      <bean name="not.singleton" class="com.something.AnotherBean" scope="property"/>
    </beans>

  注意这里的设置只对 **单例Bean** 有效。

- spring BeanFactory层次结构UML图

  |beanfactory_uml|

.. |beanfactory_uml| image:: /images/spring/BeanFactory\ UML.png
   :width: 100%

冷门、难点备忘
^^^^^^^^^^^^^^^^^^^^^^^
如何为类的静态成员 *注入*
:::::::::::::::::::::::::::::
XML方式实现
  .. code-block:: xml

    <bean id="mongoFileOperationUtil" class="com.*.*.MongoFileOperationUtil" init-method="init">
        <property name="dsForRW" ref="dsForRW"/>
    </bean>

  .. code-block:: java

    public class MongoFileOperationUtil {
        private static AdvancedDatastore dsForRW;
        private static MongoFileOperationUtil mongoFileOperationUtil;

        public void init() {
            mongoFileOperationUtil = this;
            mongoFileOperationUtil.dsForRW = this.dsForRW;
        }
    }

  构建 ``mongoFileOperationUtil`` 这个bean的时候，执行完构造方法后，还会执行该对象的 ``init()`` 方法，以便注入 `dsForRW`` 。

``@PostConstruct`` 方式实现
  .. code-block:: java

    import org.mongodb.morphia.AdvancedDatastore;
    import org.springframework.beans.factory.annotation.Autowired;

    @Component
    public class MongoFileOperationUtil {
        @Autowired
        private static AdvancedDatastore dsForRW;

        private static MongoFileOperationUtil mongoFileOperationUtil;

        @PostConstruct
        public void init() {
            mongoFileOperationUtil = this;
            mongoFileOperationUtil.dsForRW = this.dsForRW;
        }
    }

  ``@PostConstruct`` 注解的方法在加载类的构造函数之后执行，也就是在加载了构造函数之后，执行init方法；( ``@PreDestroy`` 注解定义容器销毁之前的所做的操作)
  这种方式和在xml中配置 ``init-method`` 和 ``destory-method`` 方法差不多，定义spring 容器在初始化bean 和容器销毁之前的所做的操作；

``set`` 方法上添加 ``@Autowired`` 注解，类定义上添加 ``@Component`` 注解
  .. code-block:: java

    import org.mongodb.morphia.AdvancedDatastore;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.stereotype.Component;

    @Component
    public class MongoFileOperationUtil {
        private static AdvancedDatastore dsForRW;

        @Autowired
        public void setDatastore(AdvancedDatastore dsForRW) {
            MongoFileOperationUtil.dsForRW = dsForRW;
        }
    }

  首先Spring要能扫描到AdvancedDatastore的bean，然后通过setter方法注入；

  注意：成员变量上不需要再添加@Autowired注解；

问题
  上面的三种方法，都是会创建 ``MongoFileOperationUtil`` 这个类的Bean，将它们用IOC容器管理起来。既然已经创建对象了，那么这些静态成员和方法，为什么还要保持静态呢？

有趣的代码
^^^^^^^^^^^^^^^^^^^^^^^
- 追踪 ``ApplicationContext`` 发现在 ``AbstractApplicationContext`` 有一段这个代码。它可以在某个类实例化之前加载其他类：

  .. code-block:: java

    static {
      // Eagerly load the ContextClosedEvent class to avoid weird classloader issues
      // on application shutdown in WebLogic 8.1. (Reported by Dustin Woods.)
      ContextClosedEvent.class.getName();
    }

英语
^^^^^^^^^^^^^^^^^^^^^^^
没有读懂的句式。
:::::::::::::::::::::::
- While XML has been the traditional format for defining configuration metadata, you can instruct the container to use Java annotations or code as the metadata format by providing a small amount of XML configuration to declaratively enable support for these additional metadata formats.
- XML-based configuration metadata configures these beans as <bean/> elements inside a top-level <beans/> element. Java configuration typically uses @Bean-annotated methods within a @Configuration class.
- Motivations for not supplying a name are related to using inner beans and autowiring collaborators.
- This process is fundamentally the inverse (hence the name, Inversion of Control) of the bean itself controlling the instantiation or location of its dependencies on its own by using direct construction of classes or the Service Locator pattern.
- A common place (at least in versions earlier than Spring 2.0) where the <idref/> element brings value is in the configuration of AOP interceptors in a ProxyFactoryBean bean definition. Using <idref/> elements when you specify the interceptor names prevents you from misspelling an interceptor ID.
- As a corner case, it is possible to receive destruction callbacks from a custom scope — for example, for a request-scoped inner bean contained within a singleton bean. The creation of the inner bean instance is tied to its containing bean, but destruction callbacks let it participate in the request scope’s lifecycle. This is not a common scenario. Inner beans typically simply share their containing bean’s scope.


比较难懂的句子
::::::::::::::::::::::::
- Even the simplest application has a few objects that work together to present what the end-user sees as a coherent application
-  This next section explains how you go from defining a number of bean definitions that stand alone to a fully realized application where objects collaborate to achieve a goal.
- Code is cleaner with the DI principle, and decoupling is more effective when objects are provided with their dependencies. The object does not look up its dependencies and does not know the location or class of the dependencies. As a result, your classes become easier to test, particularly when the dependencies are on interfaces or abstract base classes, which allow for stub or mock implementations to be used in unit tests.
