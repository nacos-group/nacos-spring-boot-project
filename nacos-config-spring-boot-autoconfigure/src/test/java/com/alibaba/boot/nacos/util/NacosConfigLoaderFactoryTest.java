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

import com.alibaba.boot.nacos.config.autoconfigure.NacosConfigAutoConfiguration;
import com.alibaba.boot.nacos.config.properties.NacosConfigProperties;
import com.alibaba.boot.nacos.config.util.NacosConfigLoader;
import com.alibaba.boot.nacos.config.util.NacosConfigLoaderFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.ConfigType;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.client.config.NacosConfigService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Properties;
import java.util.function.Function;

/**
 * @ClassName: NacosConfigLoaderFactoryTest
 * @Author: ChenHao26
 * @Date: 2022/8/12 16:42
 */
@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource(properties = { "nacos.config.server-addr=localhost" })
@SpringBootTest(classes = { NacosConfigAutoConfiguration.class })
public class NacosConfigLoaderFactoryTest {
    
    private NacosConfigProperties nacosConfigProperties;
    
    private Properties globalProperties;
    
    private Function<Properties, ConfigService> builder;
    
    @Autowired
    private ConfigurableEnvironment environment;
    
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
    }
    
    @Test
    public void getSingleton() {
        NacosConfigLoader singleton = NacosConfigLoaderFactory.getSingleton(nacosConfigProperties, environment,
                builder);
        NacosConfigLoader singleton2 = NacosConfigLoaderFactory.getSingleton(nacosConfigProperties, environment,
                builder);
        // Verify that it is the same object
        Assert.assertEquals(singleton2, singleton);
    }
}
