# Nacos Spring Boot Project

[![Build Status](https://travis-ci.org/nacos-group/nacos-spring-project.svg?branch=master)](https://travis-ci.org/nacos-group/nacos-spring-project)

[Alibaba Nacos](https://github.com/alibaba/nacos) ships main core features of Cloud-Native application, 
including:

- Service Discovery and Service Health Check
- Dynamic Configuration Management
- Dynamic DNS Service
- Service and MetaData Management

[Nacos Spring Boot Project](https://github.com/nacos-group/nacos-spring-boot-project) is based on [it](https://github.com/alibaba/nacos) and embraces Spring Boot ECO System so that developers could build Spring Boot application rapidly. 

Nacos Spring Boot Project consist of two parts: `nacos-config-spring-boot` and `nacos-discovery-spring-boot`.

`nacos-config-spring-boot` is using for Dynamic Configuration Management and Service and MetaData Management. 

`nacos-discovery-spring-boot` is using for Service Discovery, Service Health Check and Dynamic DNS Service.

## Demos

- [Nacos Config Simple Demo](https://github.com/nacos-group/nacos-spring-boot-project/tree/master/nacos-spring-boot-samples/nacos-config-sample)

- [Nacos Discovery Simple Demo](https://github.com/nacos-group/nacos-spring-boot-project/tree/master/nacos-spring-boot-samples/nacos-discovery-sample)


## Dependencies & Compatibility

**master branch**

| Dependencies   | Compatibility |
| -------------- | ------------- |
| Java           | 1.7+         |
| Spring Boot | 1.4.1.RELEASE         |

**1.x branch**

| Dependencies   | Compatibility |
| -------------- | ------------- |
| Java           | 1.8+         |
| Spring Boot | 2.0.3.RELEASE         |


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

Then you could using `@SpringBootApplication` to annotate main class like normal SpringBoot Application :

```java
@SpringBootApplication
public class ConfigApplication {
    ...
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

Then you could using `@SpringBootApplication` to annotate main class like normal SpringBoot Application :

```java
@SpringBootApplication
public class ConfigApplication {
    ...
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

## Relative Projects

* [Alibaba Nacos](https://github.com/alibaba/nacos)
* [Alibaba Spring Context Support](https://github.com/alibaba/spring-context-support)
* [Nacos Spring Project](https://github.com/nacos-group/nacos-spring-project)