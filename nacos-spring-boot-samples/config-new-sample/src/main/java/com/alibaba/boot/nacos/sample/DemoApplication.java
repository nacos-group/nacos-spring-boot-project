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

/**
 * @author <a href="mailto:liaochunyhm@live.com">liaochuntao</a>
 * @since
 */
package com.alibaba.boot.nacos.sample;

import com.alibaba.boot.nacos.config.binder.NacosBootConfigurationPropertiesBinder;
import com.alibaba.nacos.api.annotation.NacosInjected;
import com.alibaba.nacos.api.config.annotation.NacosConfigurationProperties;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.spring.context.annotation.config.NacosPropertySource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@NacosPropertySource(dataId = "test-2", autoRefreshed = true)
@NacosPropertySource(dataId = "test-3", autoRefreshed = true)
@NacosPropertySource(dataId = "test-4", autoRefreshed = true)
@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class);
    }

    @RestController
    protected static class MyController {

        @NacosInjected
        private NamingService namingService;

        @Autowired
        private ApplicationContext context;

        @Autowired
        private My my;

        @Autowired
        private Lc lc;

        @NacosValue(value = "${people.have}", autoRefreshed = true)
        private String enable;

        @GetMapping
        public String get() throws NacosException {
            namingService.selectOneHealthyInstance("spring.application.name");
            System.out.println("here-get");
            return my.toString() + "\n" + lc.toString() + " / " + (context.getBean(NacosBootConfigurationPropertiesBinder.BEAN_NAME) == null);
        }

        @PostMapping
        public String post() {
            System.out.println("here-post");
            return my.toString() + "\n" + lc.toString() + " / " + (context.getBean(NacosBootConfigurationPropertiesBinder.BEAN_NAME) == null);
        }

    }

    @Configuration
    @ConditionalOnProperty(prefix = "people", name = "enable", havingValue = "true")
    protected static class People {

        @Bean
        public Object object() {
            System.out.println(this.getClass().getCanonicalName());
            return new Object();
        }

    }

    @Component
    @ConfigurationProperties(prefix = "people")
    protected static class Lc {

        private String name;
        private List<Map<String, String>> list;
        private Map<String, String> map;

        public Lc() {
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<Map<String, String>> getList() {
            return list;
        }

        public void setList(List<Map<String, String>> list) {
            this.list = list;
        }

        public Map<String, String> getMap() {
            return map;
        }

        public void setMap(Map<String, String> map) {
            this.map = map;
        }

        @Override
        public String toString() {
            return "Lc{" +
                    "name='" + name + '\'' +
                    ", list=" + list +
                    ", map=" + map +
                    '}';
        }
    }

    @Component
    @NacosConfigurationProperties(dataId = "test-1", yaml = true, autoRefreshed = true, ignoreNestedProperties = true)
    protected static class My {
        private String name;

        private Map<String, String> map;

        public My() {
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Map<String, String> getMap() {
            return map;
        }

        public void setMap(Map<String, String> map) {
            this.map = map;
        }

        @Override
        public String toString() {
            return "My{" +
                    "name='" + name + '\'' +
                    ", map=" + map +
                    '}';
        }
    }

}
