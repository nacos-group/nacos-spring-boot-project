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
package com.alibaba.boot.nacos.config.support;

import com.alibaba.boot.nacos.config.autoconfigure.NacosConfigAutoConfiguration;
import com.alibaba.nacos.spring.util.ConfigParseUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * {@link MultiProfilesYamlConfigParseSupport} Test
 *
 * @author <a href="mailto:yanglu_u@126.com">dbses</a>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {NacosConfigAutoConfiguration.class}, properties = {"spring.profiles.active=alpha"})
public class MultiProfilesYamlConfigParseSupportTest {

    private String content;

    @Before
    public void setUp() {
        content = "test1:\n" +
                "  config: 2\n" +
                "\n" +
                "---\n" +
                "spring:\n" +
                "  profiles: alpha\n" +
                "test1:\n" +
                "  config: alpha\n" +
                "\n" +
                "---\n" +
                "spring:\n" +
                "  profiles: beta\n" +
                "test1:\n" +
                "  config: beta";
    }

    @Test
    public void testParse() {

        String dataId = "test.yaml";
        String group = "test";
        String type = "yaml";

        Assert.assertEquals("alpha", ConfigParseUtils.toProperties(dataId, group, content, type).get("test1.config"));
    }

}