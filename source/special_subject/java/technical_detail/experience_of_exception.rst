异常经验
==================================
在使用spring-mybatis时，报这个错误： ::

  Caused by: org.springframework.beans.factory.NoSuchBeanDefinitionException: No qualifying bean of type 'com.peter.springmybatis.mapper.UserMapper' available: expected at least 1 bean which qualifies as autowire candidate. Dependency annotations: {@org.springframework.beans.factory.annotation.Autowired(required=true)}

原因：

1. 该类没有被扫描到。解决思路： ``@SpringBootApplication(scanBasePackages={"com.peter"})`` .
2. 该类没有被 ``@Component`` 、 ``@Service`` 的注解修饰或没有相关的 ``BeanFoctory`` 。
3. 该类的Bean被重复定义。
