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
