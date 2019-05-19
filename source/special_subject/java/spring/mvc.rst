Spring MVC
===========================
- 标识控制器的 `注解` ： ``@Controller`` 。
- 标识 `Controller` 或函数的路径的注解： ``RequestMapping(path="/", method="post")`` 。
- 标识函数的路径的注解： ``@GetMapping("/")`` 。
- 方法的参数列表中需要有 ``org.springframework.ui.Model`` ，如此才能传递参数。如： ::

    public String list(@RequestParam(name = "key", required = false, defaultValue = "value") String family, Model model)


  示例代码：

  .. code-block:: java

    import org.springframework.stereotype.Controller;
    import org.springframework.ui.Model;
    import org.springframework.web.bind.annotation.GetMapping;
    import org.springframework.web.bind.annotation.RequestMapping;
    import org.springframework.web.bind.annotation.RequestParam;

    @Controller
    @RequestMapping("/view/idea")
    public class IdeaController {
        @GetMapping("/list")
        public String list(@RequestParam(name = "family", required = false, defaultValue = "") String family, Model model) {
            model.addAttribute("testValue", "name");
            return "idea/list";
        }
    }

- MVC的映射（路由）配置。示例代码：

  .. code-block:: java

    import org.springframework.context.annotation.Configuration;
    import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
    import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

    @Configuration
    public class MvcConfig implements WebMvcConfigurer {
        @Override
        public void addViewControllers(ViewControllerRegistry registry) {
            WebMvcConfigurer.super.addViewControllers(registry);

            // 根路径指引到“templates/home.html”页面，而不是默认的“static/index.html”。
            registry.addViewController("/").setViewName("home");

            // hello路由不需要经过Controller，直接指向 hello.html 页面。
            registry.addViewController("/hello").setViewName("hello");

            // 定义重定向：“/list”重定向为“view/idea/list”。
            registry.addRedirectViewController("/list", "/view/idea/list");
        }
    }

- Hander的方法签名

  .. code-block:: java

    @RequestMapping(value = {"/method.do"}, method = {RequestMethod.GET, RequestMethod.POST})
    public void controllerMethod(
        WebRequest webRequest,
        NativeWebRequest nativeWebRequest,
        HttpServletRequest request,//常见
        HttpServletResponse response,//常见
        HttpSession session,//常见
        PushBuilder pushBuilder,
        Principal principal,
        HttpMethod method,
        Locale locale,//Java8的time API
        TimeZone timeZone,//Java8的time API
        ZoneId zoneId,//Java7的time API
        InputStream is,
        Reader reader,
        OutputStream os,
        Writer writer,
        HttpEntity httpEntity,
        Errors errors,
        BindingResult result,
        SessionStatus sessionStatus,

        @PathVariable String path,//常见
        @MatrixVariable String matri,
        @RequestParam String username,
        @RequestHeader String header,
        @CookieValue String cookie,
        @RequestBody String body,//常见
        @RequestPart String part,
        @SessionAttribute String valueInSession,
        @RequestAttribute String valueInRequest) {
    }


注释
^^^^^^^^^^^^^^^^^^^^^^
- ``@Controller`` : 标识这个类是一个控制器
- ``@RequestMapping`` ：给控制器方法绑定一个uri
- ``@ResponseBody`` ：将java对象转成json，并且发送给客户端
- ``@RequestBody`` ：将客户端请求过来的json转成java对象
- ``@RequestParam`` ：当表单参数和方法形参名字不一致时，做一个名字映射
- ``@PathVarible`` ：用于获取uri中的参数,比如user/1中1的值
- ``@RestController`` ：类似于 ``@Controller`` + ``@ResponseBody`` 的组合。
- ``@GetMapping`` ：
- ``@DeleteMapping`` ：
- ``@PostMapping`` ：
- ``@PutMapping`` ：
- ``@SessionAttribute`` ：声明将什么模型数据存入session
- ``@CookieValue`` ：获取cookie值
- ``@ModelAttribute`` ：将方法返回值存入model中
- ``@HeaderValue`` ：获取请求头中的值
- ``@NotEmpty`` ：校验用到的注解之一
- ``@Length`` ：校验用到的注解之一

重点解读
^^^^^^^^^^^^^^^^^^^^^^^^^^
监听器、过滤器、拦截器
::::::::::::::::::::::::::
**这三者使用都需要 Spring Boot**

监听器
  ``Listener`` 是servlet规范中定义的一种特殊类。用于监听 ``servletContext`` 、 ``HttpSession`` 和 ``servletRequest`` 等域对象的创建和销毁事件。监听域对象的属性发生修改的事件。用于在事件发生前、发生后做一些必要的处理。

  其主要可用于以下方面：1、统计在线人数和在线用户2、系统启动时加载初始化信息3、统计网站访问量4、记录用户访问路径。

过滤器
  ``Filter`` 是Servlet技术中最实用的技术，Web开发人员通过Filter技术，对web服务器管理的所有web资源：例如Jsp, Servlet, 静态图片文件或静态 html 文件等进行拦截，从而实现一些特殊的功能。例如实现URL级别的权限访问控制、过滤敏感词汇、压缩响应信息等一些高级功能。它主要用于对用户请求进行预处理，也可以对HttpServletResponse进行后处理。

  使用Filter的完整流程：Filter对用户请求进行预处理，接着将请求交给Servlet进行处理并生成响应，最后Filter再对服务器响应进行后处理。

拦截器
  ``Interceptor`` 在AOP（Aspect-Oriented Programming）中用于在某个方法或字段被访问之前，进行拦截然后在之前或之后加入某些操作。比如日志，安全等。一般拦截器方法都是通过 **动态代理** 的方式实现。

  可以通过它来进行权限验证，或者判断用户是否登陆，或者是像12306 判断当前时间是否是购票时间。

  三大器在 **Springboot** 中使用时，首先实现相应的接口定义类，然后通过配置类将其加入到spring容器中，从而实现相应的功能。

示例代码
##########################
过滤器类
  .. literalinclude:: /_codes/special_subject/spring/code000-three_machine_in_spring_boot-MyFilter.java
    :language: java

监听器类
  .. literalinclude:: /_codes/special_subject/spring/code000-three_machine_in_spring_boot-MyHttpSessionListener.java
    :language: java

拦截器类
  .. literalinclude:: /_codes/special_subject/spring/code000-three_machine_in_spring_boot-MyInterceptor.java
    :language: java

配置类
  .. literalinclude:: /_codes/special_subject/spring/code000-three_machine_in_spring_boot-MywebConfig.java
    :language: java

控制层
  .. literalinclude:: /_codes/special_subject/spring/code000-three_machine_in_spring_boot-UserController.java
    :language: java

在实际项目中我们还可以在配置的时候设置过滤器、拦截器的执行顺序及其它的参数。

同时Filter和Listener还有对应的注解方式： ``@WebFilter`` 和 ``@WebListener`` ,在使用注解方式时不要忘了在主程序加上 ``@ServletComponentScan`` 注解，这样才能在程序启动时将对应的bean加载进来。
