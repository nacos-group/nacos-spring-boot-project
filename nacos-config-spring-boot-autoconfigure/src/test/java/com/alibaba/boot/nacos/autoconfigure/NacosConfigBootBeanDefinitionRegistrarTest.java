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

import com.alibaba.boot.nacos.config.autoconfigure.NacosConfigAutoConfiguration;
import com.alibaba.boot.nacos.config.autoconfigure.NacosConfigBootBeanDefinitionRegistrar;
import com.alibaba.nacos.client.utils.LogUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 *  {@link NacosConfigBootBeanDefinitionRegistrar} Test
 * @ClassName: NacosConfigBootBeanDefinitionRegistrarTest
 * @Author: ChenHao26
 * @Date: 2022/8/12 15:38
 */
@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource(properties = { "nacos.config.server-addr=localhost" })
@SpringBootTest(classes = { NacosConfigAutoConfiguration.class })
public class NacosConfigBootBeanDefinitionRegistrarTest {
    
    private static final Logger LOGGER = LogUtils.logger(NacosConfigBootBeanDefinitionRegistrarTest.class);
    
    private NacosConfigBootBeanDefinitionRegistrar nacosConfigBootBeanDefinitionRegistrar;
    
    @Before
    public void setup() {
        nacosConfigBootBeanDefinitionRegistrar = new NacosConfigBootBeanDefinitionRegistrar();
    }
    
    @Test
    public void setBeanFactory(){
        try {
            BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition("beanName");
            Assert.assertNotNull(builder);
            BeanFactory beanFactory = new DefaultListableBeanFactory();
            nacosConfigBootBeanDefinitionRegistrar.setBeanFactory(beanFactory);
        }catch (Exception e) {
            LOGGER.error("error info: {}",e.toString());
            Assert.assertNull(e);
        }
    }
}
