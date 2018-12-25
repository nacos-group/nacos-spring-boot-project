### Nacos Config

First, you have to start a Nacos Server in backend , If you don't know steps, you can learn about [quick start](https://nacos.io/en-us/docs/quick-start.html).

Suppose your Nacos Server is startup, you would add [`nacos-config-spring-boot-starter`](nacos-config-spring-boot-starter) in your  Spring application's dependencies :

```xml
    <dependencies>
        ...
        
        <dependency>
            <groupId>com.alibaba.boot</groupId>
            <artifactId>nacos-config-spring-boot-starter</artifactId>
            <version>${latest.version}</version>
        </dependency>
        
        ...
    </dependencies>
```

Note: Version [0.2.x.RELEASE](https://mvnrepository.com/artifact/com.alibaba.boot/nacos-config-spring-boot-starter) is compatible with the Spring Boot 2.x. Version [0.1.x.RELEASE](https://mvnrepository.com/artifact/com.alibaba.boot/nacos-config-spring-boot-starter) is compatible with the Spring Boot 1.x.

After that, you could define some configurations in `application.properties`:
 
```properties
nacos.config.server-addr=localhost:8848
```

> `nacos.config.server-addr` attribute configures "\${host}:${port}" of your Nacos Server

Then you could using `@SpringBootApplication` to annotate main class like normal SpringBoot Application and startup:

```java
@SpringBootApplication
public class ConfigApplication {
    public static void main(String[] args) {
        SpringApplication.run(ConfigApplication.class, args);
    }
}
```

If you'd like to use "Distributed Configuration" features, `ConfigService` is a core service interface to get or publish config, you could use "Dependency Injection" to inject `ConfigService` instance in your Spring Beans.

```java
@Service
public class ConfigServiceDemo {

    @NacosInjected
    private ConfigService configService;
    
    public void demoGetConfig() {
        try {
            String dataId = "{dataId}";
            String group = "{group}";
            String content = configService.getConfig(dataId, groupId, 5000);
        	System.out.println(content);
        } catch (NacosException e) {
            e.printStackTrace();
        }
    }
    ...
}
```

above code equals below one: 

```java
try {
    // Initialize the configuration service, and the console automatically obtains the following parameters through the sample code.
    String serverAddr = "{serverAddr}";
    String dataId = "{dataId}";
    String group = "{group}";
    Properties properties = new Properties();
    properties.put("serverAddr", serverAddr);
    ConfigService configService = NacosFactory.createConfigService(properties);
    // Actively get the configuration.
    String content = configService.getConfig(dataId, group, 5000);
    System.out.println(content);
} catch (NacosException e) {
    // TODO Auto-generated catch block
    e.printStackTrace();
}
```

## Endpoint

Nacos config starter support the implementation of Spring Boot actuator endpoints.

**Prerequisite:**

Adding `nacos-config-spring-boot-actuator` to your pom.xml in Nacos Config Project.

Then Configure your endpoint security strategy.

* Spring Boot1.x 

```properties
management.security.enabled=false
```

* Spring Boot2.x

```properties
management.endpoints.web.exposure.include=*
```

To view the endpoint information, visit the following URLS:

* Spring Boot1.x: URL is http://127.0.0.1:10011/nacos-config.
* Spring Boot2.x: URL is http://127.0.0.1:10011/actuator/nacos-config.

## Health Checks

`nacos-config-spring-boot-actuator` support the standard Spring Boot `HealthIndicator` as a production-ready feature , which will be aggregated into Spring Boot's `Health` and exported on `HealthEndpoint` that works MVC (Spring Web MVC) if it is available.

Suppose a Spring Boot Web application did not specify `management.server.port`(SpringBoot1.x using `management.port`), you can access http://localhost:{port}/actuator/health (SpringBoot1.x visit http://localhost:{port}/health) via Web Client and will get a response with JSON format is like below : 

```json
{
"status": "UP",
"details": {
    "nacosConfig": {
        "status": "UP"
    },
    "diskSpace": {
        "status": "UP",
        "details": {
            "total": 250140434432,
            "free": 52323512320,
            "threshold": 10485760
        }
    }
}
}
```

For more information about Nacos Spring, see [Nacos Spring Project](https://github.com/nacos-group/nacos-spring-project).

## Relative Projects

* [Alibaba Nacos](https://github.com/alibaba/nacos)
* [Alibaba Spring Context Support](https://github.com/alibaba/spring-context-support)
* [Nacos Spring Project](https://github.com/nacos-group/nacos-spring-project)
* [Nacos Spring Cloud Project](https://github.com/spring-cloud-incubator/spring-cloud-alibaba)
