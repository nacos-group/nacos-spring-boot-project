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
package com.ablibaba.boot.nacos.support;

import com.alibaba.nacos.spring.util.parse.DefaultYamlConfigParse;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author yanglulu
 * @date 2022/1/20
 */
public class MultiProfilesYamlConfigParseSupport extends DefaultYamlConfigParse implements EnvironmentPostProcessor {

    private static String[] profileArray = {};

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        String[] profiles = environment.getActiveProfiles();
        if (profileArray.length == 0) {
            profileArray = profiles;
        }
    }

    @Override
    public Map<String, Object> parse(String configText) {
        final AtomicReference<Map<String, Object>> result = new AtomicReference<>();
        process(map -> {
            if (result.get() == null) {
                result.set(map);
            } else {
                for (String profile : profileArray) {
                    if (profile.equals(map.get("spring.profiles"))) {
                        result.get().putAll(map);
                    }
                }
            }
        }, createYaml(), configText);
        return result.get();
    }

}
