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

package com.alibaba.boot.nacos.binder;

import com.alibaba.boot.nacos.config.autoconfigure.NacosConfigAutoConfiguration;
import com.alibaba.boot.nacos.config.binder.NacosBootConfigurationPropertiesBinder;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.annotation.NacosConfigurationProperties;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.spring.context.event.config.EventPublishingConfigService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.lang.reflect.Method;

/**
 * {@link NacosBootConfigurationPropertiesBinder} Test
 * @ClassName: NacosBootConfigurationPropertiesBinderTest
 * @Author: ChenHao26
 * @Date: 2022/8/12 16:00
 */
@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource(properties = { "nacos.config.server-addr=localhost" })
@SpringBootTest(classes = { NacosConfigAutoConfiguration.class })
public class NacosBootConfigurationPropertiesBinderTest {

    private Binder binder;

    @Autowired
    private ConfigurableApplicationContext context;

    @Before
    public void setup() {
        binder = new Binder(context);
    }

    @Test
    public void findFactoryMethod(){
        Method beanName = binder.findFactoryMethod(NacosBootConfigurationPropertiesBinder.BEAN_NAME);
        Assert.assertNull(beanName);
    }

    @Test
    public void testDoBind() {
        People people = new People();
        binder.doBind(people, "people", "people", "people", "properties",
                People.class.getAnnotation(NacosConfigurationProperties.class), "people.name=SuperZ1999\npeople.age=24",
                new EventPublishingConfigService(null, null, context, null));
        Assert.assertEquals(people.getName(), "SuperZ1999");
        Assert.assertEquals(people.getAge(), 24);
    }

    static class Binder extends NacosBootConfigurationPropertiesBinder {

        public Binder(ConfigurableApplicationContext applicationContext) {
            super(applicationContext);
        }

        @Override
        protected void doBind(Object bean, String beanName, String dataId, String groupId, String configType, NacosConfigurationProperties properties, String content, ConfigService configService) {
            super.doBind(bean, beanName, dataId, groupId, configType, properties, content, configService);
        }
    }

    @NacosConfigurationProperties(prefix = "people", dataId = "people", groupId = "people")
    static class People {
        private String name;
        private int age;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }
    }
}
