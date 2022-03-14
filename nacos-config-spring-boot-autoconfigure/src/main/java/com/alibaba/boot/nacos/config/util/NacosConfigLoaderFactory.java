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
package com.alibaba.boot.nacos.config.util;

import com.alibaba.boot.nacos.config.properties.NacosConfigProperties;
import com.alibaba.nacos.api.config.ConfigService;
import org.springframework.core.env.ConfigurableEnvironment;

import java.util.Properties;
import java.util.function.Function;

/**
 * NacosConfigLoaderFactory.
 * @author hujun
 */
public class NacosConfigLoaderFactory {

    private volatile static NacosConfigLoader nacosConfigLoader;

    public static NacosConfigLoader getSingleton(NacosConfigProperties nacosConfigProperties,
                                                 ConfigurableEnvironment environment,
                                                 Function<Properties, ConfigService> builder) {
        if (nacosConfigLoader == null) {
            synchronized (NacosConfigLoaderFactory.class) {
                if (nacosConfigLoader == null) {
                    nacosConfigLoader = new NacosConfigLoader(nacosConfigProperties, environment, builder);
                }
            }
        }
        return nacosConfigLoader;
    }
}
