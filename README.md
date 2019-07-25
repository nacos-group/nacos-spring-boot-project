# Nacos Spring Boot Project

[![Build Status](https://travis-ci.org/nacos-group/nacos-spring-boot-project.svg?branch=master)](https://travis-ci.org/nacos-group/nacos-spring-boot-project)

[Alibaba Nacos](https://github.com/alibaba/nacos) ships main core features of Cloud-Native application, 
including:

- Service Discovery and Service Health Check
- Dynamic Configuration Management
- Dynamic DNS Service
- Service and MetaData Management

[Nacos Spring Boot Project](https://github.com/nacos-group/nacos-spring-boot-project) is based on [it](https://github.com/alibaba/nacos) and embraces Spring Boot ECO System so that developers could build Spring Boot application rapidly. 

Nacos Spring Boot Project consist of two parts: `nacos-config-spring-boot` and `nacos-discovery-spring-boot`.

`nacos-config-spring-boot` module is using for Dynamic Configuration Management and Service and MetaData Management. 

`nacos-discovery-spring-boot` module is using for Service Discovery, Service Health Check and Dynamic DNS Service.

## Samples

- [Nacos Config Sample](https://github.com/nacos-group/nacos-spring-boot-project/tree/master/nacos-spring-boot-samples/nacos-config-sample)

- [Nacos Discovery Sample](https://github.com/nacos-group/nacos-spring-boot-project/tree/master/nacos-spring-boot-samples/nacos-discovery-sample)

## Dependencies & Compatibility

**master branch**

| Dependencies   | Compatibility |
| -------------- | ------------- |
| Java           | 1.8+         |
| Spring Boot | 2.0.3.RELEASE         |

**1.x branch**

| Dependencies   | Compatibility |
| -------------- | ------------- |
| Java           | 1.7+         |
| Spring Boot | 1.4.1.RELEASE         |


## Quick Start

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

After that, you could define some configurations in `application.properties`:
 
```properties
nacos.config.server-addr=localhost
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

### Nacos Discovery

First, you have to start a Nacos Server in backend , If you don't know steps, you can learn about [quick start](https://nacos.io/en-us/docs/quick-start.html).

Suppose your Nacos Server is startup, you would add [`nacos-discovery-spring-boot-starter`](nacos-discovery-spring-boot-starter) in your  Spring application's dependencies :

```xml
    <dependencies>
        ...
        
        <dependency>
            <groupId>com.alibaba.boot</groupId>
            <artifactId>nacos-discovery-spring-boot-starter</artifactId>
            <version>${latest.version}</version>
        </dependency>
        
        ...
    </dependencies>
```

After that, you could define some configurations in `application.properties`:
 
```properties
nacos.discovery.server-addr=localhost
```

> `nacos.discovery.server-addr` attribute configures "\${host}:${port}" of your Nacos Server

Then you could using `@SpringBootApplication` to annotate main class like normal SpringBoot Application and startup:

```java
@SpringBootApplication
public class DiscoveryApplication {
    public static void main(String[] args) {
        SpringApplication.run(DiscoveryApplication.class, args);
    }
}
```

If you'd like to use "Service Registry" features, `NamingService` is a core service interface to get or publish config, you could use "Dependency Injection" to inject `NamingService` instance in your Spring Beans.

```java
@Service
public class NamingServiceDemo {

    @NacosInjected
    private NamingService namingService;
    
    public void demoRegisterService() {
        try {
            namingService.registerInstance("test-service", "1.1.1.1", 8080);
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
    // Initialize the naming service, and the console automatically obtains the following parameters through the sample code.
    String serverAddr = "{serverAddr}";
    Properties properties = new Properties();
    properties.put("serverAddr", serverAddr);
    NamingService naming = NamingFactory.createNamingService(properties);
namingService.registerInstance("test-service", "1.1.1.1", 8080);
} catch (NacosException e) {
    // TODO Auto-generated catch block
    e.printStackTrace();
}
```

For more information about Nacos Spring, see [Nacos Spring Project](https://github.com/nacos-group/nacos-spring-project).

## Endpoint

Nacos config starter and Nacos discovery starter also support the implementation of Spring Boot actuator endpoints.

**Prerequisite:**

Adding `nacos-config-spring-boot-actuator` to your pom.xml in Nacos Config Project.

Adding `nacos-discovery-spring-boot-actuator` to your pom.xml in Nacos Discovery Project.

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

Nacos Config Project :

* Spring Boot1.x: Nacos Config Endpoint URL is http://127.0.0.1:10011/nacos-config.
* Spring Boot2.x: Nacos Config Endpoint URL is http://127.0.0.1:10011/actuator/nacos-config.

Nacos Discovery Project:

* Spring Boot1.x: Nacos Discovery Endpoint URL is http://127.0.0.1:10012/nacos-discovery.
* Spring Boot2.x: Nacos Discovery Endpoint URL is http://127.0.0.1:10012/actuator/nacos-discovery.

## Health Checks

`nacos-config-spring-boot-actuator` and `nacos-discovery-spring-boot-actuator` support the standard Spring Boot `HealthIndicator` as a production-ready feature , which will be aggregated into Spring Boot's `Health` and exported on `HealthEndpoint` that works MVC (Spring Web MVC) if it is available.

Suppose a Spring Boot Web application did not specify `management.server.port`(SpringBoot1.x using `management.port`), you can access http://localhost:{port}/actuator/health (SpringBoot1.x visit http://localhost:{port}/health) via Web Client and will get a response with JSON format is like below : 

Nacos Config Project:

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

Nacos Discovery Project:

```json
{
"status": "UP",
"details": {
    "nacosDiscovery": {
        "status": "UP"
    },
    "diskSpace": {
        "status": "UP",
        "details": {
            "total": 250140434432,
            "free": 52323680256,
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
