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

package com.alibaba.boot.nacos.util;

import com.alibaba.boot.nacos.autoconfigure.NacosConfigEnvironmentProcessorTest;
import com.alibaba.boot.nacos.config.autoconfigure.NacosConfigAutoConfiguration;
import com.alibaba.boot.nacos.config.properties.NacosConfigProperties;
import com.alibaba.boot.nacos.config.util.NacosConfigLoader;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.ConfigType;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.client.config.NacosConfigService;
import com.alibaba.nacos.client.utils.LogUtils;
import com.alibaba.nacos.spring.core.env.NacosPropertySourcePostProcessor;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.function.Function;

/**
 * @ClassName: NacosConfigLoaderTest
 * @Author: ChenHao26
 * @Date: 2022/8/12 16:12
 */

@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource(properties = { "nacos.config.server-addr=localhost" })
@SpringBootTest(classes = { NacosConfigAutoConfiguration.class })
public class NacosConfigLoaderTest {
    
    private NacosConfigLoader nacosConfigLoader;
    
    
    private NacosConfigProperties nacosConfigProperties;
    
    private Properties globalProperties;
    
    @Autowired
    private ConfigurableEnvironment environment;
    
    private Function<Properties, ConfigService> builder;
    
    private List<NacosConfigLoader.DeferNacosPropertySource> nacosPropertySources;
    
    private static final Logger LOGGER = LogUtils.logger(NacosConfigEnvironmentProcessorTest.class);
    
    private NacosPropertySourcePostProcessor nacosPropertySourcePostProcessor;
    
    @Before
    public void setup() {
        nacosConfigProperties = new NacosConfigProperties();
        nacosConfigProperties.setServerAddr("localhost");
        nacosConfigProperties.setUsername("nacos");
        nacosConfigProperties.setPassword("nacos");
        nacosConfigProperties.setMaxRetry("4");
        nacosConfigProperties.setType(ConfigType.TEXT);
        nacosConfigProperties.setDataId("xiaomi");
        nacosConfigProperties.setGroup("group01");
        nacosConfigProperties.setAutoRefresh(true);
        nacosConfigProperties.setEndpoint("localhost");
        globalProperties = new Properties();
        globalProperties.setProperty("maxRetry","3");
        globalProperties.setProperty("content","key=01");
        globalProperties.setProperty("endpoint","localhost");
        builder = properties -> {
            try {
                return new NacosConfigService(globalProperties);
            } catch (NacosException e) {
                throw new RuntimeException(e);
            }
        };
        nacosPropertySources = new LinkedList<>();
        nacosConfigLoader = new NacosConfigLoader(nacosConfigProperties, environment, builder);
        nacosPropertySourcePostProcessor = new NacosPropertySourcePostProcessor();
    }

    @Test
    public void buildGlobalNacosProperties() {
        Properties properties = nacosConfigLoader.buildGlobalNacosProperties();
        LOGGER.info("buildGlobalNacosProperties properties : {}", properties);
        Assert.assertNotNull(properties);
        Assert.assertEquals(properties.size(), 6);
    }
    
    @Test
    public void addListenerIfAutoRefreshed() {
        nacosConfigLoader.addListenerIfAutoRefreshed();
        List<NacosConfigLoader.DeferNacosPropertySource> propertySources = nacosConfigLoader.getNacosPropertySources();
        Assert.assertEquals(propertySources.size(), 0);
    }
}
