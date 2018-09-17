/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.boot.nacos.sample;

import com.alibaba.nacos.api.annotation.NacosInjected;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import java.util.List;

/**
 * @author <a href="mailto:fangjian0423@gmail.com">Jim</a>
 */
@SpringBootApplication
public class DiscoveryApplication {

    public static void main(String[] args) {
        SpringApplication.run(DiscoveryApplication.class, args);
    }

    @Bean
    @Order(Ordered.LOWEST_PRECEDENCE)
    public CommandLineRunner firstCommandLineRunner() {
        return new FirstCommandLineRunner();
    }

    @Bean
    @Order(Ordered.LOWEST_PRECEDENCE - 1)
    public CommandLineRunner secondCommandLineRunner() {
        return new SecondCommandLineRunner();
    }

    public static class FirstCommandLineRunner implements CommandLineRunner {

        @NacosInjected
        private NamingService namingService;

        @Override
        public void run(String... args) throws Exception {
            System.out.println("start to register");
            namingService.registerInstance("test-service", "1.1.1.1", 8080);
        }
    }

    public static class SecondCommandLineRunner implements CommandLineRunner {

        @NacosInjected
        private NamingService namingService;

        @Override
        public void run(String... args) throws Exception {
            List<Instance> instanceList = namingService.getAllInstances("test-service");
            System.out.println("found instance: " + instanceList.size());
            instanceList.forEach(instance -> {
                System.out.println(instance);
            });
        }
    }


}
