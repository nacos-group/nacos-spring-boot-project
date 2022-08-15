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
import com.alibaba.boot.nacos.config.util.NacosPropertiesBuilder;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Properties;

/**
 * @ClassName: NacosPropertiesBuilderTest
 * @Author: ChenHao26
 * @Date: 2022/8/12 16:49
 */
@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource(properties = { "nacos.config.server-addr=localhost" })
@SpringBootTest(classes = { NacosConfigAutoConfiguration.class })
public class NacosPropertiesBuilderTest {
    
    @Autowired
    private Environment environment;
    
    @Test
    public void buildNacosProperties() {
        String serverAddr = "localhost";
        String namespaceId = "namespaceId";
        String secretKey = "secretKey";
        String ramRoleName = "ramRoleName";
        String configLongPollTimeout = "configLongPollTimeout";
        String configRetryTimeout = "configRetryTimeout";
        String maxRetry = "maxRetry";
        String enableRemoteSyncConfig = "enableRemoteSyncConfig";
        String username = "nacos";
        String password = "password";
        Properties properties = NacosPropertiesBuilder.buildNacosProperties(environment, serverAddr, namespaceId, secretKey,
                "ak", ramRoleName, configLongPollTimeout, configRetryTimeout, maxRetry, enableRemoteSyncConfig, true,
                username, password);
        Assert.assertEquals(properties.size(), 12);
    
    }
}
