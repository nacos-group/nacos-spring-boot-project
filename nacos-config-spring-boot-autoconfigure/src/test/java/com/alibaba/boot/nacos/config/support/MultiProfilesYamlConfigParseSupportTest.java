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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * {@link MultiProfilesYamlConfigParseSupport} Test
 *
 * @author <a href="mailto:yanglu_u@126.com">dbses</a>
 */
public class MultiProfilesYamlConfigParseSupportTest {

    @RunWith(SpringJUnit4ClassRunner.class)
    @ActiveProfiles({"alpha"})
    @SpringBootTest(classes = {NacosConfigAutoConfiguration.class})
    public static class OneProfiles {

        @Autowired
        private Environment environment;

        @Before
        public void setUp() {
            // because MultiProfilesYamlConfigParseSupport # postProcessEnvironment() run once,
            // so it should set profilesArray before test
            MultiProfilesYamlConfigParseSupport.setProfileArray(environment.getActiveProfiles());
        }

        @Test
        public void oneProfiles_normal() {
            String content = "test1:\n" +
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
            Assert.assertEquals(environment.getActiveProfiles()[0], "alpha");
            Object result = ConfigParseUtils.toProperties("test.yaml", "test", content, "yaml")
                    .get("test1.config");
            Assert.assertEquals("alpha", result);
        }

        @Test
        public void oneProfiles_when_content_profiles_isnull() {
            String content = "test1:\n" +
                             "  config: 2\n" +
                             "\n" +
                             "---\n" +
                             "test1:\n" +
                             "  config: alpha\n" +
                             "\n" +
                             "---\n" +
                             "test1:\n" +
                             "  config: beta";
            Object result = ConfigParseUtils.toProperties("test.yaml", "test", content, "yaml")
                    .get("test1.config");
            Assert.assertEquals("beta", result);
        }
    }

    @RunWith(SpringJUnit4ClassRunner.class)
    @ActiveProfiles({"alpha", "beta"})
    @SpringBootTest(classes = {NacosConfigAutoConfiguration.class})
    public static class TwoProfiles {

        @Autowired
        private Environment environment;

        @Before
        public void setUp() {
            MultiProfilesYamlConfigParseSupport.setProfileArray(environment.getActiveProfiles());
        }

        @Test
        public void twoProfiles_normal() {
            String content = "test1:\n" +
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
            Assert.assertEquals(environment.getActiveProfiles()[0], "alpha");
            Assert.assertEquals(environment.getActiveProfiles()[1], "beta");
            Object result = ConfigParseUtils.toProperties("test.yaml", "test", content, "yaml")
                    .get("test1.config");
            Assert.assertEquals("beta", result);
        }

    }

    @RunWith(SpringJUnit4ClassRunner.class)
    @SpringBootTest(classes = {NacosConfigAutoConfiguration.class})
    public static class NoProfiles {

        @Autowired
        private Environment environment;

        @Test
        public void noProfiles_normal() {
            String content = "test1:\n" +
                             "  config: 2\n" +
                             "\n" +
                             "---\n" +
                             "spring:\n" +
                             "  profiles: default\n" +
                             "test1:\n" +
                             "  config: default\n" +
                             "\n" +
                             "---\n" +
                             "spring:\n" +
                             "  profiles: beta\n" +
                             "test1:\n" +
                             "  config: beta";
            Assert.assertEquals(environment.getActiveProfiles().length, 0);
            Object result = ConfigParseUtils.toProperties("test.yaml", "test", content, "yaml")
                    .get("test1.config");
            Assert.assertEquals("default", result);
        }

    }

}