## Nacos AOT

### prerequisites

- GraalVM22.0+ (JDK17)
- native-image
- Nacos Server

If you haven't downloaded GraalVM, you can download it from [Download GraalVM](https://www.graalvm.org/downloads/).

If you don't have a native-image environment, you can configure according to [Native Image](https://www.graalvm.org/latest/reference-manual/native-image/).

You have to start a Nacos Server in backend , If you don't know steps, you can learn about [quick start](https://nacos.io/en-us/docs/quick-start.html).

### Quick Start

The complete code for this example can be found at: [`nacos-aot-sample`](nacos-spring-boot-samples/nacos-aot-sample)

1. Because SpringBoot2 does not support aot, you must specify SpringBoot as SpringBoot3 in dependency management:

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-dependencies</artifactId>
            <version>3.0.6</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

2. You would add [`nacos-config-spring-boot-starter`](nacos-config-spring-boot-starter)  or [`nacos-discovery-spring-boot-starter`](nacos-discovery-spring-boot-starter) in your  Spring application's dependencies :

```xml
<dependencies>
    <dependency>
        <groupId>com.alibaba.boot</groupId>
        <artifactId>nacos-config-spring-boot-starter</artifactId>
        <version>${latest.version}</version>
    </dependency>
    <dependency>
        <groupId>com.alibaba.boot</groupId>
        <artifactId>nacos-discovery-spring-boot-starter</artifactId>
        <version>${latest.version}</version>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
</dependencies>
```

3. In order to use the process-aot feature of SpringBoot and more conveniently compile native-image programs, we also need `spring-boot-maven-plugin` and `native-maven-plugin`:

```xml
<profiles>
    <profile>
        <id>native</id>
        <build>
            <plugins>
                <plugin>
                    <groupId>org.graalvm.buildtools</groupId>
                    <artifactId>native-maven-plugin</artifactId>
                    <version>0.9.25</version>
                    <executions>
                        <execution>
                            <id>compile</id>
                            <goals>
                                <goal>compile</goal>
                            </goals>
                        </execution>
                    </executions>
                    <configuration>
                        <classesDirectory>${project.build.outputDirectory}</classesDirectory>
                        <mainClass>com.alibaba.boot.nacos.sample.AotApplication</mainClass>
                    </configuration>
                </plugin>
                <plugin>
                    <groupId>org.springframework.boot</groupId>
                    <artifactId>spring-boot-maven-plugin</artifactId>
                    <executions>
                        <execution>
                            <id>process-aot</id>
                            <goals>
                                <goal>process-aot</goal>
                            </goals>
                        </execution>
                    </executions>
                </plugin>
            </plugins>
        </build>
    </profile>
</profiles>
```

4. You could define some configurations in `application.properties`:

```properties
nacos.config.server-addr=localhost:8848
nacos.discovery.server-addr=localhost:8848
```

> `nacos.config.server-addr` and `nacos.discovery.server-addr` attribute configure "\${host}:${port}" of your Nacos Server

5. You could using `@SpringBootApplication` to annotate main class like normal SpringBoot Application and startup:

```java
@SpringBootApplication
@NacosPropertySource(dataId = "example", autoRefreshed = true)
public class AotApplication {
    public static void main(String[] args) {
        SpringApplication.run(AotApplication.class, args);
    }
}
```

Note: `@NacosPropertySource` is used to specify the data-id of the configuration.

6. Let's try the functions of `@NacosInjected` and `@NacosValue`, for more usage, see [Nacos Spring Project](https://github.com/nacos-group/nacos-spring-project).

```java
@Controller
public class AotController {
    @NacosInjected
    private ConfigService configService;

    @NacosInjected
    private NamingService namingService;

    @NacosValue(value = "${flag:false}", autoRefreshed = true)
    private boolean flag;

    @ResponseBody
    @RequestMapping(value = "/config/get", method = GET)
    public String getConfig() throws NacosException {
        return configService.getConfig("example", "DEFAULT_GROUP", 5000);
    }

    @ResponseBody
    @RequestMapping(value = "/naming/get", method = GET)
    public List<Instance> getNaming(@RequestParam("serviceName") String serviceName) throws NacosException {
        return namingService.getAllInstances(serviceName);
    }

    @ResponseBody
    @RequestMapping(value = "/flag/get", method = GET)
    public boolean getFlag() {
        return flag;
    }
}
```

7. Publish the configuration to the Nacos Server by calling the Nacos Open API, with dataId of example and content of `flag=true`

```shell
curl -X POST "http://127.0.0.1:8848/nacos/v1/cs/configs?dataId=example&group=DEFAULT_GROUP&content=flag=true"
```

8. Enable the tracing agent on the command line with the `java` command from the GraalVM JDK:

```shell
$JAVA_HOME/bin/java -agentlib:native-image-agent=config-output-dir=/path/to/config-dir/ ...
```

Note: For more information about tracing agent, see [Collect Metadata with the Tracing Agent](https://www.graalvm.org/latest/reference-manual/native-image/metadata/AutomaticMetadataCollection/).

9. Run the java program and follow the steps below to use the program: 

   1. Open `localhost:8080/config/get` with a browser, and the browser responds with `flag=true`.
   2. Open `localhost:8080/flag/get` with a browser, and the browser responds with `true`.
   3. Publish the configuration to the Nacos Server by calling the Nacos Open API, with dataId of example and content of `flag=false`

   ```shell
   curl -X POST "http://127.0.0.1:8848/nacos/v1/cs/configs?dataId=example&group=DEFAULT_GROUP&content=flag=false"
   ```

   4. Open `localhost:8080/flag/get` with a browser, and the browser responds with `false`.
   5. Register a service named example with Nacos server by calling Nacos Open API.

   ```shell
   curl -X POST 'http://127.0.0.1:8848/nacos/v1/ns/instance?serviceName=example&ip=127.0.0.1&port=8080'
   ```

   6. Open `localhost:8080/naming/get?serviceName=example` with a browser, and the browser responds with information about the service you just registered.

10. Close the program and copy the file just generated to the `classpath:META-INF/native-image/` folder, The file path is specified in step 8.

Note: [`nacos-aot-sample`](nacos-spring-boot-samples/nacos-aot-sample) already includes these files, but you can still use the files you just generated.

11. Run the following command and you will find the compiled executable program in the `target` folder.

```shell
mvn -DskipTests=true clean package -Pnative
```

12. This executable program is what we want! Run the program and try step 9 again.

## Relative Projects

* [Alibaba Nacos](https://github.com/alibaba/nacos)
* [Alibaba Spring Context Support](https://github.com/alibaba/spring-context-support)
* [Nacos Spring Project](https://github.com/nacos-group/nacos-spring-project)
* [Nacos Spring Cloud Project](https://github.com/spring-cloud-incubator/spring-cloud-alibaba)