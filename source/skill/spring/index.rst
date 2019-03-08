Spring技术备忘
==========================
Spring Boot
^^^^^^^^^^^^^^^^^^^^^^^^^^
热部署
  需要在pom.xml中加入此依赖： ::

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-devtools</artifactId>
        <scope>runtime</scope>
    </dependency>

  如果还是不行，在加一个这个： ::

    <optional>true</optional>

Spring MVC
^^^^^^^^^^^^^^^^^^^^^^^^^^^
- 标识控制器的 `注解` ： ``@Controller`` 。
- 标识 `Controller` 或函数的路径的注解： ``RequestMapping(path="/", method="post")`` 。
- 标识函数的路径的注解： ``@GetMapping("/")`` 。
- 方法的参数列表中需要有 ``org.springframework.ui.Model`` ，如此才能传递参数。如： ::

    public String list(@RequestParam(name = "key", required = false, defaultValue = "value") String family, Model model)


  示例代码： ::

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

- MVC的映射（路由）配置。示例代码： ::

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

Spring Security
^^^^^^^^^^^^^^^^^^^^^^^^^^^^
- MVC权限配置，示例： ::

    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.security.config.annotation.web.builders.HttpSecurity;
    import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
    import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
    import org.springframework.security.core.userdetails.User;
    import org.springframework.security.core.userdetails.UserDetails;
    import org.springframework.security.core.userdetails.UserDetailsService;
    import org.springframework.security.provisioning.InMemoryUserDetailsManager;

    @Configuration
    @EnableWebSecurity
    public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            // 如果不注释这句话，下面的设置不起作用。
            // super.configure(http);
            http
                // 开始配置路径相关的权限
                .authorizeRequests()
                    .antMatchers("/home", "/hello").permitAll()
                    .anyRequest().authenticated()
                    .and()
                // 开始配置登录界面相关权限
                .formLogin()
                    // 使用自定义登录界面
                    .loginPage("/login")
                    // 登录界面无权限限制。
                    .permitAll()
                    .and()
                // 开始配置退出界面相关权限。
                .logout()
                    // 退出界面无权限限制。
                    .permitAll();
        }

        @Bean
        @Override
        public UserDetailsService userDetailsService() {
            UserDetails user = User.withDefaultPasswordEncoder()
                    // 设置用户密码
                    .username("peter").password("peter")
                    .roles("USER").build();
            return new InMemoryUserDetailsManager(user);
        }
    }

  然后制作登录页面和登出按钮。

  登录页面需要注意URI为 ``/login`` 的 ``post`` 请求，需要携带参数 ``username`` 、 ``password`` 。页面会接受到参数 ``param.error`` 、 ``param.logout`` 。

  登出界面需要注意URI为 ``/logout`` 的 ``post`` 请求。

Srping JPA
^^^^^^^^^^^^^^^^^^^^^^^^
- 以Derby为例，连接数据库。pom.xml的配置。 ::

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        <dependency>
            <groupId>org.apache.derby</groupId>
            <artifactId>derby</artifactId>
            <scope>runtime</scope>
        </dependency>

  applications.properties中的配置。 ::

    dbBaseDir=/home/username/derbydb_file_system_path
    spring.jpa.hibernate.ddl-auto=update
    spring.datasource.username=app
    spring.datasource.url=jdbc:derby:${dbBaseDir}/idea;create=true

  注意： ``spring.datasource.username`` 需要填写，因为新建的derby数据库没有 ``SA`` 用户。

- 创建Bean实体。示例如下： ::

    import java.util.Date;
    import java.util.List;

    import javax.persistence.Entity;
    import javax.persistence.GeneratedValue;
    import javax.persistence.GenerationType;
    import javax.persistence.Id;
    import javax.persistence.ManyToMany;

    import com.fasterxml.jackson.annotation.JsonIgnore;

    @Entity
    public class Idea {
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        private long id;
        private String content;
        private String family;
        @ManyToMany(mappedBy = "ideas")
        private List<Tag> tags;
        private Date time;

        @JsonIgnore
        public long getId() {
            return id;
        }

        // 其他参数的get/set方法

        @Override
        public String toString() { ...... }
    }

    @Entity
    public class Tag {
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        private long id;
        private String name;
        @ManyToMany
        private List<Idea> ideas;

        // 各个参数的get/set方法。

        @Override
        public String toString() { ...... }
    }

  示例中注解 ``@JsonIgnore`` ，表示在MVC返回此实例时，不会返回ID。

  两个实体类中的注解 ``@ManyToMany`` 是向对应的，在创建表时将会创建第三张关联表，表名为 ``idea_tag`` 。注意在 ``Idea`` 中 ``@ManyToMany`` 注解增加了 ``mappedBy`` 参数，它表示在这段关系中 ``Idea`` 是被维护端。在实际操作中， `Idea`` 表中的数据可以随意删除，但是 `Tag` 表中的数据不能。

  注解 ``@GeneratedValue`` 标记属性ID为自增长属性。

- 借助接口 ``PagingAndSortingRepository`` 创建实体类的 `库` ，因为实现了接口 ``PagingAndSortingRepository`` 所以执行分页与排序。有因为使用了 ``@RepositoryRestResource`` 所以可以直接通过 `REST` 操作此类型的示例（增删改查）。代码如下： ::

    import java.util.List;

    import org.springframework.data.repository.PagingAndSortingRepository;
    import org.springframework.data.repository.query.Param;
    import org.springframework.data.rest.core.annotation.RepositoryRestResource;

    import com.peter.website.data.Idea;

    @RepositoryRestResource(collectionResourceRel = "idea", path = "idea")
    public interface IdeaRepository extends PagingAndSortingRepository<Idea, Long> {
        List<Idea> findByContent(@Param("content") String content);
    }

    @RepositoryRestResource(collectionResourceRel = "tag", path = "tag")
    public interface TagRepository extends PagingAndSortingRepository<Tag, Long> {
        List<Idea> findByName(@Param("name") String name);
    }

