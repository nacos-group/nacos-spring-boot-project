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

import com.alibaba.nacos.spring.core.env.AbstractNacosPropertySourceBuilder;
import com.alibaba.nacos.spring.core.env.NacosPropertySource;
import com.alibaba.nacos.spring.util.parse.DefaultYamlConfigParse;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * yaml multi profiles.
 * <p>
 * read nacos propertysource:
 * <p>
 * 1. {@link AbstractNacosPropertySourceBuilder#doBuild(String, BeanDefinition, Map)}<br/>
 * 2. {@link NacosPropertySource#NacosPropertySource(String, String, String, String, String)}<br/>
 * 3. {@link com.alibaba.nacos.spring.util.NacosUtils#toProperties(String, String, String, String)}<br/>
 * 4. {@link com.alibaba.nacos.spring.util.ConfigParseUtils#toProperties(String, String, String, String)}<br/>
 *
 * @author <a href="mailto:yanglu_u@126.com">dbses</a>
 */
public class MultiProfilesYamlConfigParseSupport extends DefaultYamlConfigParse implements EnvironmentPostProcessor {

    private static final String SPRING_PROFILES = "spring.profiles";

    private static String[] profileArray = {};

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        String[] profiles = environment.getActiveProfiles();
        // fall back to default profiles
        if (profiles.length == 0) {
            profiles = environment.getDefaultProfiles();
        }
        // set once
        if (profileArray.length == 0) {
            profileArray = profiles;
        }
    }

    @Override
    public Map<String, Object> parse(String configText) {
        final AtomicReference<Map<String, Object>> result = new AtomicReference<>();
        process(map -> {
            // first block
            if (result.get() == null) {
                result.set(map);
            } else {
                setFromOtherBlock(result, map);
            }
        }, createYaml(), configText);
        return result.get();
    }

    private void setFromOtherBlock(AtomicReference<Map<String, Object>> result, Map<String, Object> map) {
        if (map.get(SPRING_PROFILES) == null) {
            result.get().putAll(map);
            return;
        }

        for (String profile : profileArray) {
            if (profile.equals(map.get(SPRING_PROFILES))) {
                result.get().putAll(map);
            }
        }
    }

    /**
     * for unit test
     */
    static void setProfileArray(String[] profiles) {
        profileArray = profiles;
    }

}
