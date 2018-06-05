Maven
=======================

.. toctree::
   :maxdepth: 1

入门
^^^^^^^^^^^^^^^^^^^^^^^
- 快速启动生成一个项目，可以使用这样的命令 ::

    mvn archetype:generate -DgroupId=com.yiibai -DartifactId=NumberGenerator -DarchetypeArtifactId=maven-archetype-quickstart -DinteractiveMode=false

  `artifactId` : 生成的项目名（及目录名）为 `NumberGenerator`

  `groupId` : 会在 `src/main/java` 目录下生成目录 `com.yiibai` 。

  `archetypeArtifactId` : 主要请求的路径是 ``https://repo.maven.apache.org/maven2/org/apache/maven/archetypes/maven-archetype-quickstart/``

  将这个项目变为Eclipse项目 ::

    mvn eclipse:eclipse

- 基于Maven模板，快速构建一个WEB项目 ::

    mvn archetype:generate -DgroupId=com.yiibai  -DartifactId=CounterWebApp -DarchetypeArtifactId=maven-archetype-webapp -DinteractiveMode=false

  将这个项目变为Eclipse项目 ::

    mvn eclipse:eclipse -Dwtpversion=2.0

  其中 ``-Dwtpversion=2.0`` 就是告诉 Maven 将项目转换到 Eclipse 的 Web 项目(WAR)，而不是默认的Java项目(JAR)。

总结
^^^^^^^^^^^^^^^^^^^^^^^
- ``dependency`` 表示依赖，子标签有： ``groupId`` 、 ``artifactId`` 、 ``version`` 。
- ``repositories`` 仓库集合。子标签有： ``repository`` 。
- ``repository`` 仓库。子标签有 ``id`` 、 ``url`` 。比如： ::

     <repositories>
         <repository>
             <id>java.net</id>
             <url>https://maven.java.net/content/repositories/public/</url>
         </repository>
     </repositories>

- 定制库到Maven本地资源库，以 ``kaptcha`` 为例 ::

    mvn install:install-file -Dfile=c:\kaptcha-2.3.jar -DgroupId=com.google.code -DartifactId=kaptcha -Dversion=2.3 -Dpackaging=jar

  现在，“kaptcha” jar被复制到 Maven 本地存储库。

  其他项目需要它的时候，只需要在pom.xml中这样做： ::

    <dependency>
        <groupId>com.google.code</groupId>
        <artifactId>kaptcha</artifactId>
        <version>2.3</version>
    </dependency>

- 如果想将一个项目那位Eclipse项目 ::

    mvn eclipse:eclipse
    mvn eclipse:eclipse -Dwtpversion=2.0

- 添加插件，比如说制定JDK版本 ::

	<build>
	  <plugins>
		<plugin>
			<groupId>org.apache.maven.plugins</groupId>
			<artifactId>maven-compiler-plugin</artifactId>
			<version>2.3.2</version>
			<configuration>
				<source>1.6</source>
				<target>1.6</target>
			</configuration>
		</plugin>
	  </plugins>
	</build>

- 使用命令 ``mvn archetype:generate`` 可以查看Maven中 `1000+` 个模板。不过因为返回结果过大，所有我建议你这样使用这个命令 ::

    mvn archetype:generate > templates.txt

  需要注意： ``mvn archetype:generate`` 命令最后，需要你输入一个Id，一遍依照对应的模板创建项目。所以使用 `推荐命令` 之后，需要 `Ctrl + C` 来退出进程。

- 常用的Maven模板：

  - org.apache.maven.archetypes:maven-archetype-quickstart (Java Project)
  - org.apache.maven.archetypes:maven-archetype-webapp (Java Web Project)
  - ml.rugal.archetype:springmvc-spring-hibernate (Spring-Hibernate Project)

零散的知识点
^^^^^^^^^^^^^^^^^^^^^^^
- ``scope`` 

  scope在maven的依赖管理中主要负责项目的部署，像下面的代码 ::

    <dependencies>
        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-log4j12</artifactId>
            <version>1.7.7</version>
            <scope>compile</scope>
        </dependency>
        <dependency>
            <groupId>log4j</groupId>
            <artifactId>log4j</artifactId>
            <version>1.2.17</version>
            <scope>runtime</scope>
        </dependency>
    </dependencies>

  ``org.slf4j`` 项目需要参与当前项目的编译。

  ``log4j`` 项目无需参与项目的编译，不过后期的测试和运行周期需要其参与。

  具体取值的细节：

  - compile：默认值 他表示被依赖项目需要参与当前项目的编译，还有后续的测试，运行周期也参与其中，是一个比较强的依赖。打包的时候通常需要包含进去
  - test：依赖项目仅仅参与测试相关的工作，包括测试代码的编译和执行，不会被打包，例如：junit
  - runtime：表示被依赖项目无需参与项目的编译，不过后期的测试和运行周期需要其参与。与compile相比，跳过了编译而已。例如JDBC驱动，适用运行和测试阶段
  - provided：打包的时候可以不用包进去，别的设施会提供。事实上该依赖理论上可以参与编译，测试，运行等周期。相当于compile，但是打包阶段做了exclude操作
  - system：从参与度来说，和provided相同，不过被依赖项不会从maven仓库下载，而是从本地文件系统拿。需要添加systemPath的属性来定义路径

- 运行jar文件的方法之一： ``java -cp target/NumberGenerator-1.0-SNAPSHOT.jar com.yiibai.App`` ，其中 ``com.yiibai.App`` 是个类。

问题
^^^^^^^^^^^^^^^^^^^^^^^
- 如何在 `mvn package` 时，为生成的 `jar` 文件
