Spring AOP
=======================
一些名词
^^^^^^^^^^^^^^^^^^^^^^^
- **Aspect** ( **切面** )：通知、连接点、切入点共同组成了切面：时间、地点和要发生的“故事”。
- **Joinpoint** ( **连接点** ）：程序能够应用通知的一个“时机”，这些“时机”就是连接点，例如方法调用时、异常抛出时、方法返回后等等。
- **Pointcut** ( **切点** ) ：通知定义了切面要发生的“故事”，连接点定义了“故事”发生的时机，那么切入点就定义了“故事”发生的地点，例如某个类或方法的名称，Spring中允许我们方便的用正则表达式来指定。
- **Advice** ( **通知** ) ：通知定义了在切入点代码执行时间点附近需要做的工作。
- **Advisor** ( **通知器** ) ：
- **Introduction** ( **引入** )：引入允许我们向现有的类添加新的方法和属性(Spring提供了一个方法注入的功能）。
- **Target** ( **目标** )：即被通知的对象，如果没有AOP，那么通知的逻辑就要写在目标对象中，有了AOP之后它可以只关注自己要做的事，解耦合！
- **proxy** ( **代理** )：应用通知的对象，详细内容参见设计模式里面的动态代理模式。
- **Weaving** ( **织入** )：把切面应用到目标对象来创建新的代理对象的过程。

一些知识点
^^^^^^^^^^^^^^^^^^^^^^^
待续

一些总结点
^^^^^^^^^^^^^^^^^^^^^^^
Spring支持五种类型的通知
  - **Before** (前)  ``org.apringframework.aop.MethodBeforeAdvice``
  - **After-returning** (返回后) ``org.springframework.aop.AfterReturningAdvice``
  - **After-throwing** (抛出后) ``org.springframework.aop.ThrowsAdvice``
  - **Arround** (周围) ``org.aopaliance.intercept.MethodInterceptor``
  - **Introduction** (引入) ``org.springframework.aop.IntroductionInterceptor``

织入一般发生在如下几个时机
  - 编译时：当一个类文件被编译时进行织入，这需要特殊的编译器才可以做的到，例如AspectJ的织入编译器；
  - 类加载时：使用特殊的ClassLoader在目标类被加载到程序之前增强类的字节代码；
  - 运行时：切面在运行的某个时刻被织入,SpringAOP就是以这种方式织入切面的，原理应该是使用了JDK的动态代理技术。

一些问题
^^^^^^^^^^^^^^^^^^^^^^^
Spring AOP的四种编码方式
:::::::::::::::::::::::::::
1. 采用注解的方式编码
###########################
这种方式又可以认为是 **基于AspectJ** 的。

这种方式主要是将写在spring 配置文件中的连接点, 写到注解里面

通过 ``<aop: aspectj-autoproxy>`` 来配置，使用 **AspectJ** 的注解来标识通知及切入点

首先，编辑配置文件，声明Spring对 ``@AspectJ`` 的支持
  在spring配置文件（beans文件）中加入如下配置（用来申明spring对@AspectJ的支持）： ::

    <aop:aspectj-autoproxy/>

  如果你使用的是DTD，可以在Spring配置文件中加入如下配置（来申明spring对@Aspect的支持）： ::

    <bean class="org.springframework.aop.aspectj.annotation.AnnotationAwareAspectJAutoProxyCreator"/>

  除此之外，还需要开启注解扫描功能（Spring boot中有默认设置不需要此配置）： ::

      <context:component-scan base-package="com.aop"/>

  完整的配置文件可以是这样： ::

    <?xml version="1.0" encoding="UTF-8"?>
    <beans xmlns="http://www.springframework.org/schema/beans"
      xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
      xmlns:context="http://www.springframework.org/schema/context"
      xmlns:aop="http://www.springframework.org/schema/aop"
      xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/aop http://www.springframework.org/schema/aop/spring-aop-4.3.xsd
        http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context-4.3.xsd">
      <!-- 开启注解扫描 -->
      <context:component-scan base-package="com.aop"/>
      <!-- 开启aop注解方式，此步骤s不能少，这样java类中的aop注解才会生效 -->
      <aop:aspectj-autoproxy/>
    </beans>

然后编辑相关类
  目标对象类(CommonEmployee.java)与前面类似，不过加入了注解：

  .. code-block:: java

    @Component("employee")
    public class CommonEmployee implements Employee{
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void signIn() {
           System.out.println(name+"已经签到了...........");
        }
    }

  具体通知类（AspectJLogger.java 相当于前面的Logger.java）如下：

  .. code-block:: java

    import java.util.Date;

    import org.aspectj.lang.ProceedingJoinPoint;
    import org.aspectj.lang.annotation.After;
    import org.aspectj.lang.annotation.Around;
    import org.aspectj.lang.annotation.Aspect;
    import org.aspectj.lang.annotation.Before;

    /**
     * 使用@Aspect 注解的类， Spring 将会把它当作一个特殊的Bean（一个切面），也就是
     * 不对这个类本身进行动态代理
     */
    @Aspect
    @Component("aspectJLogger")
    public class AspectJLogger {
        /**
         * 必须为final String类型的,注解里要使用的变量只能是静态常量类型的
         */
        public static final String EDP = "execution(* com.aop.CommonEmployee.sign*(..))";

        @Before(EDP)    //spring中Before通知
        public void logBefore() {
            System.out.println("logBefore:现在时间是:......");
        }

        @After(EDP)    //spring中After通知
        public void logAfter() {
            System.out.println("logAfter:现在时间是:.....");
        }

        @Around(EDP)   //spring中Around通知
        public Object logAround(ProceedingJoinPoint joinPoint) {
            System.out.println("logAround开始:现在时间是:....."); //方法执行前的代理处理
            Object[] args = joinPoint.getArgs();
            Object obj = null;
            try {
                obj = joinPoint.proceed(args);
            } catch (Throwable e) {
                e.printStackTrace();
            }
            System.out.println("logAround结束:现在时间是:.....");  //方法执行后的代理处理
            return obj;
        }
    }

  该类中还可以增加一个方法： ::

    @Pointcut(EDP)
    public void logpoint(){}

  这样 ``logBefore()`` 等方法前的注解就可以改为： ``@Before("logpoint()")`` 、 ``@After("logpoint()")`` 、 ``Around("logpoint()")`` 。

  测试类：

  .. code-block:: java

    public class Test {
        public static void main(String[] args) throws Exception{
            ApplicationContext act = new ClassPathXmlApplicationContext("applicationContext-aop.xml");
            Employee e = (Employee)act.getBean("employee");
            e.signIn();
        }
    }

2. 采用声明的方式编码
###########################
这种方式又可以认为是 **基于XML** 的。

通过 ``<aop:config>`` 来配置

配置文件中配置pointcut, 在java中用编写实际的aspect 类, 针对对切入点进行相关的业务处理。

编写目标对象类（CommonEmployee.java）

.. code-block:: java

  public class CommonEmployee implements Employee{
      private String name;

      public String getName() {
          return name;
      }

      public void setName(String name) {
          this.name = name;
      }

      public void signIn() {
         System.out.println(name+"已经签到了...........");
      }
  }

具体通知类Logger.java

.. code-block:: java

  public class Logger{
      //spring中Before通知
      public void logBefore() {
          System.out.println("logBefore:现在时间是:....");
      }

      //spring中After通知
      public void logAfter() {
          System.out.println("logAfter:现在时间是:.....");
      }

      //spring中Around通知
      public Object logAround(ProceedingJoinPoint joinPoint) {
          System.out.println("logAround开始:现在时间是:....."); //方法执行前的代理处理
          Object[] args = joinPoint.getArgs();
          Object obj = null;
          try {
              obj = joinPoint.proceed(args);
          } catch (Throwable e) {
              e.printStackTrace();
          }
          System.out.println("logAround结束:现在时间是:.....");  //方法执行后的代理处理
          return obj;
      }
  }

Spring配置：applicationContext-aop.xml

.. code-block:: xml

  <bean id="employee" class="com.aop.CommonEmployee">
    <property name="name" value="good"></property>
  </bean>
  <bean id="advice" class="com.aop.Logger" />
  <aop:config >
    <aop:aspect ref="advice">
      <aop:pointcut id="pointcut" expression="execution(* com.aop.CommonEmployee.sign*(..))"/>
      <aop:before method="logBefore" pointcut-ref="pointcut"/>
      <aop:after method="logAfter" pointcut-ref="pointcut"/>
      <aop:around method="logAround" pointcut-ref="pointcut"/>
    </aop:aspect>
  </aop:config>

测试类：Test.java

.. code-block:: java

  public class Test {
      public static void main(String[] args) throws Exception{
          ApplicationContext act = new ClassPathXmlApplicationContext("applicationContext-aop.xml");
          Employee e = (Employee)act.getBean("employee");
          e.signIn();
      }
  }

输出结果是： ::

  logBefore:现在时间是.....
  logAround开始:现在时间是:....
  good已经签到了...........
  logAfter:现在时间是:...
  logAround结束:现在时间是:......

3. 配置ProxyFactoryBean
#############################
这也是一种基于 XML 的方式。需要显式地设置advisors, advice, target等

待续

4. 配置AutoProxyCreator
##############################
这也是一种基于 XML 的方式。这种方式下，还是如以前一样使用定义的bean，但是从容器中获得的其实已经是代理对象

待续

注意事项
################
- 环绕方法通知，环绕方法通知要注意必须给出调用之后的返回值，否则被代理的方法会停止调用并返回null，除非你真的打算这么做。
- 只有环绕通知才可以使用JoinPoint的子类ProceedingJoinPoint，各连接点类型可以调用代理的方法，并获取、改变返回值。

Spring AOP的两种实现方式
:::::::::::::::::::::::::::
参考： `spring AOP的实现原理 <https://www.cnblogs.com/CHENJIAO120/p/7080790.html>`_

- 基于接口的动态代理(Dynamic Proxy)
- 基于子类化的CGLIB代理

Spring AOP默认使用标准的 **JDK动态代理** 来实现AOP代理。这能使任何接口(或者一组接口)被代理。

Spring AOP也使用CGLIB代理。对于代理classes而非接口这是必要的。如果一个业务对象没有实现任何接口，那么默认会使用CGLIB。由于面向接口而非面向classes编程是一个良好的实践；业务对象通常都会实现一个或者多个业务接口。强制使用CGLIB也是可能的(希望这种情况很少)，此时你需要advise的方法没有被定义在接口中，或者你需要向方法中传入一个具体的对象作为代理对象。

我们在使用Spring AOP的时候，一般是不需要选择具体的实现方式的。Spring AOP能根据上下文环境帮助我们选择一种合适的。

JDK动态代理
###########################
JDK动态代理只能对实现了接口的类生成代理，而不能针对类

下面进行简单的复现其原理，参考： `JDK动态代理实现原理 <https://www.cnblogs.com/zuidongfeng/p/8735241.html>`_

- 首先新建一个接口 ``Subject``

  .. code-block:: java

    package com.lnjecit.proxy;

    /**
     * 抽象主题接口
     **/
    public interface Subject {
        void doSomething();
    }

- 然后为接口 ``Subject`` 新建一个实现类 ``RealSubject``

  .. code-block:: java

    /**
     * 真实主题类
     **/
    public class RealSubject implements Subject {
        @Override
        public void doSomething() {
            System.out.println("RealSubject do something");
        }
    }

- 接着创建一个代理类 ``JDKDynamicProxy`` 实现 ``java.lang.reflect.InvocationHandler`` 接口，重写 ``invoke`` 方法

  .. code-block:: java

    package com.lnjecit.proxy.dynamic.jdk;

    import java.lang.reflect.InvocationHandler;
    import java.lang.reflect.Method;
    import java.lang.reflect.Proxy;

    /**
     * jdk的动态代理
     **/
    public class JDKDynamicProxy implements InvocationHandler {
        private Object target;

        public JDKDynamicProxy(Object target) {
            this.target = target;
        }

        /**
         * 获取被代理接口实例对象
         * @param <T>
         * @return
         */
        public <T> T getProxy() {
            return (T) Proxy.newProxyInstance(target.getClass().getClassLoader(), target.getClass().getInterfaces(), this);
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            System.out.println("Do something before");
            Object result = method.invoke(target, args);
            System.out.println("Do something after");
            return result;
        }
    }

- 新建测试类 ``Client`` 测试结果

  .. code-block:: java

    package com.lnjecit.proxy;

    import com.lnjecit.proxy.dynamic.jdk.JDKDynamicProxy;

    /**
     * client测试代码
     **/
    public class Client {
        public static void main(String[] args) {
            user1();
            // user2();
        }

        /**
         * 可以使用JDKDynamicProxy 这样写
         */
        private static void use1() {
            // 保存生成的代理类的字节码文件
            System.getProperties().put("sun.misc.ProxyGenerator.saveGeneratedFiles", "true");

            // jdk动态代理测试
            Subject subject = new JDKDynamicProxy(new RealSubject()).getProxy();

            // 也可以写成
            subject.doSomething();
        }

        /**
         * 也可以这样写，即不使用 JDKDynamicProxy 中的 ``getProxy()`` 方法。
         */
        private static void use2() {
            // 保存生成的代理类的字节码文件
            System.getProperties().put("sun.misc.ProxyGenerator.saveGeneratedFiles", "true");

            Subject subject = new RealSubject();
            InvocationHandler hander = new JDKDynamicProxy(subject);
            Subject proxy = (Subject) Proxy.newProxyInstance(subject.getClass().getClassLoader(), new Class[]{Subject.class}, hander);
            // 或者是使用getInterfaces()方法，获得其所有接口
            // Subject proxy = (Subject) Proxy.newProxyInstance(subject.getClass().getClassLoader(), subject.getClass().getInterfaces(), hander);
            // jdk动态代理测试
            Subject subject = new JDKDynamicProxy(new RealSubject()).getProxy();

            // 也可以写成
            subject.doSomething();
        }
    }

源码分析
:::::::::::::::::::::::::
JDK1.8.0_65

大概流程

- 为接口创建代理类的字节码文件
- 使用ClassLoader将字节码文件加载到JVM
- 创建代理类实例对象，执行对象的目标方法

动态代理涉及到的主要类

- java.lang.reflect.Proxy
- java.lang.reflect.InvocationHandler
- java.lang.reflect.WeakCache
- sun.misc.ProxyGenerator

源码解读
  待续

CGLIB代理
############################
CGLIB是针对类实现代理，主要是对指定的类生成一个子类，覆盖其中的方法（继承）。

注意：CGLib不能对声明为final的方法进行代理，因为CGLib原理是动态生成被代理类的子类。

强制使用CGLIB很简单
  .. code-block:: java

    @Configuration
    @EnableAspectJAutoProxy(proxyTargetClass = true)
    @ComponentScan(basePackages = "com.destiny1020")
    public class CommonConfiguration {}

  如上面的代码，只需要向 ``@EnableAspectJAutoProxy`` 注解中添加属性 ``proxyTargetClass = true`` 即可。

两种代理的对比
####################
JDK动态代理(Dynamic Proxy)
  - 基于标准JDK的动态代理功能
  - 只针对实现了接口的业务对象

CGLIB
  - 通过动态地对目标对象进行子类化来实现AOP代理。
  - 需要指定 ``@EnableAspectJAutoProxy(proxyTargetClass = true)`` 来强制使用
  - 当业务对象没有实现任何接口的时候默认会选择CGLIB

Spring在选择用JDK还是CGLiB的依据：
  - 当Bean实现接口时，Spring就会用JDK的动态代理
  - 当Bean没有实现接口时，Spring使用CGlib是实现
  - 强制使用CGlib时，使用CGlib

JDK代理与CGLiB的效率对比
  - 使用CGLib实现动态代理，CGLib底层采用ASM字节码生成框架，使用字节码技术生成代理类。在jdk6之前，比使用Java反射效率要高。唯一需要注意的是，CGLib不能对声明为final的方法进行代理，因为CGLib原理是动态生成被代理类的子类。
  - 在对JDK动态代理与CGlib动态代理的代码实验中看，1W次执行下，JDK7及8的动态代理性能比CGlib要好20%左右。

异常解决
#####################
在没有强制使用 ``CGLIB`` 方式时，下面的代码会报错，为什么？
  .. code-block:: java

    public interface SampleInterface {}

    @Component
    public class SampleBean implements SampleInterface {
        public void advicedMethod() {}

        public void invokeAdvicedMethod() {
            advicedMethod();
        }
    }

    @Aspect
    @Component
    public class SampleAspect {
        @Before("execution(void advicedMethod())")
        public void logException() {
            System.out.println("Aspect被调用了");
        }
    }

    @SpringBootApplication
    public class Application {
        public static void main(String[] args) {
            ApplicationContext context = SpringApplication.run(WebsiteApplication.class, args);
            SampleBean sampleBean = (SampleBean)act.getBean(SampleBean.class);
            sampleBean.invokeAdvicedMethod(); // 会打印出 "Aspect被调用了" 吗？
        }
    }

  原因是：针对接口的代理，它使用了 *JDK动态代理* 的实现方式，但是方法 ``advicedMethod()`` 在接口 ``SampleInterface`` 并没有被声明。

一些应用场景
^^^^^^^^^^^^^^^^^^^^^^^^^^
Spring事务的实现
:::::::::::::::::::::
Spring的事务管理机制实现的原理，就是通过这样一个动态代理对所有需要事务管理的Bean进行加载，并根据配置在invoke方法中对当前调用的 方法名进行判定，并在method.invoke方法前后为其加上合适的事务管理代码，这样就实现了Spring式的事务管理。Spring中的AOP实 现更为复杂和灵活，不过基本原理是一致的。

一些代码备忘
^^^^^^^^^^^^^^^^^^^^^^^^^^^^
- 通知里面的内容可以使用逻辑判断符，如： ``@Around("hystrixCommandAnnotationPointcut() || hystrixCollapserAnnotationPointcut()")`` 。
- 在 ``Around`` 中获取方法，并且获取方法是否被某些注解修饰：

  .. code-block:: java

    // joinPoint的类型是ProceedingJoinPoint，它是被 ``@Around`` 修饰的方法的必备参数。
    Method method = getMethodFromTarget(joinPoint);
    // HystrixCommand是一个注解类，是Spring Cloud的熔断组件 Hystrix 里的一个注解。
    method.isAnnotationPresent(HystrixCommand.class)
