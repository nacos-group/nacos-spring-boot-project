/*
 *
 *  * Licensed to the Apache Software Foundation (ASF) under one or more
 *  * contributor license agreements.  See the NOTICE file distributed with
 *  * this work for additional information regarding copyright ownership.
 *  * The ASF licenses this file to You under the Apache License, Version 2.0
 *  * (the "License"); you may not use this file except in compliance with
 *  * the License.  You may obtain a copy of the License at
 *  *
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package com.alibaba.boot.nacos.util;

import com.alibaba.boot.nacos.config.autoconfigure.NacosConfigAutoConfiguration;
import com.alibaba.boot.nacos.config.properties.NacosConfigProperties;
import com.alibaba.boot.nacos.config.util.NacosConfigPropertiesUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 *  {@link NacosConfigPropertiesUtils} Test
 * @ClassName: NacosConfigPropertiesUtilsTest
 * @Author: SuperZ1999
 * @Date: 2023/9/28
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = { NacosConfigAutoConfiguration.class })
@TestPropertySource(properties = {
        "nacos.config.server-addr=8.8.8.8",
        "nacos.config.password=123456"
})
public class NacosConfigPropertiesUtilsTest {
    @Autowired
    private ConfigurableEnvironment environment;

    @Test
    public void testBuildNacosConfigProperties() {
        NacosConfigProperties properties = NacosConfigPropertiesUtils.buildNacosConfigProperties(environment);
        Assert.assertEquals(properties.getServerAddr(), "8.8.8.8");
        Assert.assertEquals(properties.getPassword(), "123456");
    }
}