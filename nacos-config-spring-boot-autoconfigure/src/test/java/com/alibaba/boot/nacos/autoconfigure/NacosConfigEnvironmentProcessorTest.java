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
import com.alibaba.boot.nacos.config.autoconfigure.NacosConfigEnvironmentProcessor;
import com.alibaba.boot.nacos.config.binder.NacosBootConfigurationPropertiesBinder;
import com.alibaba.boot.nacos.config.properties.NacosConfigProperties;
import com.alibaba.boot.nacos.config.util.NacosConfigPropertiesUtils;
import com.alibaba.nacos.client.config.NacosConfigService;
import com.alibaba.nacos.client.utils.LogUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 *  {@link NacosBootConfigurationPropertiesBinder} Test
 * @ClassName: NacosConfigEnvironmentProcessorTest
 * @Author: ChenHao26
 * @Date: 2022/8/12 15:43
 */
@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource(properties = { "nacos.config.server-addr=localhost" })
@SpringBootTest(classes = { NacosConfigAutoConfiguration.class })
public class NacosConfigEnvironmentProcessorTest {

    private NacosConfigEnvironmentProcessor nacosConfigEnvironmentProcessor;
    
    @Autowired
    private ConfigurableEnvironment environment;
    
    private static final Logger LOGGER = LogUtils.logger(NacosConfigEnvironmentProcessorTest.class);
    
    @Mock
    private NacosConfigProperties nacosConfigProperties;
    
    @Before
    public void setup() {
        nacosConfigEnvironmentProcessor = new NacosConfigEnvironmentProcessor();
    }
    
    @Test
    public void postProcessEnvironment() {
        try {
            NacosConfigProperties nacosConfigProperties1 = NacosConfigPropertiesUtils.buildNacosConfigProperties(
                    environment);
            Assert.assertFalse(nacosConfigProperties1.isEnableRemoteSyncConfig());
            nacosConfigEnvironmentProcessor.postProcessEnvironment(environment, new SpringApplication());
        }catch (Exception e) {
            LOGGER.error("error info :{} ",e);
            Assert.assertNull(e);
        }
    }
    
    @Test
    public void getOrder() {
        int order = nacosConfigEnvironmentProcessor.getOrder();
        Assert.assertNotNull(order);
    }
}
