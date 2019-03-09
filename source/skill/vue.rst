Vue
===================
安装
^^^^^^^^^^^^^^^^^^^
- 安装工具软件 ``npm`` 、 ``vue`` 、 ``iview`` ::

    $ sudo apt install npm
    $ sudo npm install --global vue@^2.5.2
    $ sudo npm install -g iview
    $ sudo npm install -g @vue/cli

  注意，目前 ``iview`` 需要的是的 ``Vue`` 的版本是“2.5.2”。所以如果需要 ``iview`` 的话，需要制定vue的本版号。或者运行 ``npm install iview --save`` 。

- 查看版本： ::

    $ vue --version

- 创建前端项目： ::

    $ vue init webpack proj-name

- 运行这个前端项目： ::

    $ cd proj-name
    $ npm run dev

- 访问 ``localhost:8080`` 可以看到这个网站的样子。当然，我们可以通过配置修改这个端口号。
- 之后就可以编辑这个前端项目了，并可以通过 ``localhost:8080`` 实时查看效果。
- 项目编辑完成后打包（编译为只有 `Html` 、 `Css` 、 `JS` 的项目）。命令为 ::

    npm run build

  之后我们就可以在 ``[proj-name]/dist`` 目录下查看打包结果。

- 如果需要与 `Spring-MVC` 等项目集成，需要将 `dist` 目录下的内容 `copy` 到 `Maven` 项目中 ``src/main/webapp`` 中。在这里会遇到一些问题，如访问权限、资源加载失败等，这些就需要具体解决了。

配置
^^^^^^^^^^^^^^^
- 修改网站端口，编辑文件 ``config/index`` ，位置是 ``dev.port`` ，默认是 ``8080`` 。

使用说明
^^^^^^^^^^^^^^^^
创建新组件并应用
  目录 ``src/components`` 专门存放这些组件。在此目录下创建目录 ``test`` , 并在其下创建文件 ``test.vue`` 、 ``index.js`` 。

  ``test.vue`` 文件的内容： ::

    <template>
      <p class="test"> {{msg}} </p>
    </template>

    <script>
    export default {
      name: 'Test',
      data () {
        return {
          msg: '这是一个新组件'
        }
      }
    }
    </script>

    <!-- Add "scoped" attribute to limit CSS to this component only -->
    <style scoped>
    .test {
        font-weight: normal;
    }
    </style>

  ``index.js`` 文件的内容： ::

    import Test from './test.vue';
    export default Test;

  在文件 ``src/main.js`` 中添加如下内容： ::

    import Test from './components/test';
    ...

    new Vue({
      el: '#app',
      router,
      components: { App, Test },
      template: '<App/>'
    })

  这样就可以在 ``src/index.html`` 页面中使用 ``<Test/>`` 组件了。

  当然我们也可以在 ``src/App.vue`` 中添加类似内容，这样我们就可以在组件 ``<App/>`` 中使用组件 ``<Test/>`` 了。

Vue-router（路由）的用法
  在初始项目中， ``src/App.vue`` 的内容如下（节选）： ::

    <template>
      <div id="app">
        <img src="./assets/logo.png">
        <router-view/>
      </div>
    </template>

    <script>
    export default {
      name: 'App'
    }
    </script>

    <style>
    ...
    </style>

  其中 ``<router-vue/>`` 就是对路由的应用。地址栏中，在 ``http://localhost:8080/#/`` 后的变化变化，之后影响 ``src/App.vue`` 中 ``<router-view/>`` 位置的内容，不会影响其他（比如 ``<img src="./assets/logo.png">`` 。它的来源追溯如下：

  - 在 ``src/components`` 下的文件 ``HelloWorld.vue`` 定义了一个名为“HelloWorld”的组件。
  - 在文件 ``src/router/index.js`` 中有如下内容： ::

      import Vue from 'vue'
      import Router from 'vue-router'
      import HelloWorld from '@/components/HelloWorld'

      Vue.use(Router)

      export default new Router({
        routes: [
          {
            path: '/',
            name: 'HelloWorld',
            component: HelloWorld
          }
        ]
      })

    这段内容中 ``routes`` 部分将本网站的“根路径”与组件“HelloWorld”绑定了。
  - 文件 ``src/main.js`` 中存在这个内容 ``import router from './router'`` 它是将路由引入主页面。

  如果我们要将之前定义 ``Test`` 组件页加入路由，可以将 ``src/router/index.js`` 文件的内容作如下修改： ::

    import Vue from 'vue'
    import Router from 'vue-router'
    import HelloWorld from '@/components/HelloWorld'
    import Test from '@/components/test'

    Vue.use(Router)

    export default new Router({
      routes: [
        {
          path: '/',
          name: 'HelloWorld',
          component: HelloWorld
        },
        {
          path: '/test',
          name: 'Test',
          component: Test
        },
      ]
    })

  如此，浏览器的地址栏为 ``http://localhost:8080/#/peter`` 时， ``<router-vue/>`` 部分就会加载组件 ``Test`` 。
