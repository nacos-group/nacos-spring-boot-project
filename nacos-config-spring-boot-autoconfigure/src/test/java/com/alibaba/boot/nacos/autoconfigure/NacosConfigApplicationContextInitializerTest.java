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

package com.alibaba.boot.nacos.autoconfigure;

import com.alibaba.boot.nacos.config.autoconfigure.NacosConfigApplicationContextInitializer;
import com.alibaba.boot.nacos.config.autoconfigure.NacosConfigAutoConfiguration;
import com.alibaba.boot.nacos.config.autoconfigure.NacosConfigBootBeanDefinitionRegistrar;
import com.alibaba.boot.nacos.config.autoconfigure.NacosConfigEnvironmentProcessor;
import com.alibaba.boot.nacos.config.properties.NacosConfigProperties;
import com.alibaba.boot.nacos.config.util.NacosConfigLoader;
import com.alibaba.boot.nacos.config.util.NacosConfigLoaderFactory;
import com.alibaba.boot.nacos.config.util.NacosConfigPropertiesUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * {@link NacosConfigApplicationContextInitializer} Test
 * @ClassName: NacosConfigApplicationContextInitializerTest
 * @Author: ChenHao26
 * @Date: 2022/8/12 15:25
 */
@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource(properties = { "nacos.config.server-addr=localhost" })
@SpringBootTest(classes = { NacosConfigAutoConfiguration.class })
public class NacosConfigApplicationContextInitializerTest {
    
    private NacosConfigApplicationContextInitializer nacosConfigApplicationContextInitializer;
    
    @Autowired
    private ConfigurableEnvironment environment;
    
    @Autowired
    private NacosConfigProperties nacosConfigProperties;
    
    @Autowired
    private ConfigurableApplicationContext context;
    
    @Before
    public void testNacosConfigApplicationContextInitializer() {
        nacosConfigApplicationContextInitializer = new NacosConfigApplicationContextInitializer(new NacosConfigEnvironmentProcessor());
    }
    
    @Test
    public void initialize(){
        try {
            nacosConfigProperties = NacosConfigPropertiesUtils
                    .buildNacosConfigProperties(environment);
            Assert.assertNotNull(nacosConfigProperties);
            NacosConfigLoader singleton = NacosConfigLoaderFactory.getSingleton(nacosConfigProperties, environment,
                    null);
            Assert.assertNotNull(singleton);
            nacosConfigApplicationContextInitializer.initialize(context);
        }catch (Exception e) {
            Assert.assertNotNull(e);
        }
    }
}
