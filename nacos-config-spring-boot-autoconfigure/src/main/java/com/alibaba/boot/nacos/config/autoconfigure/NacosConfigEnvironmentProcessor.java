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
package com.alibaba.boot.nacos.config.autoconfigure;

import com.alibaba.boot.nacos.config.NacosConfigConstants;
import com.alibaba.boot.nacos.config.properties.NacosConfigProperties;
import com.alibaba.boot.nacos.config.util.NacosConfigPropertiesUtils;
import com.alibaba.boot.nacos.config.util.NacosConfigUtils;
import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;

import java.util.LinkedList;
import java.util.Properties;
import java.util.function.Function;

/**
 * In the Context to create premise before loading the log configuration information
 *
 * @author <a href="mailto:liaochunyhm@live.com">liaochuntao</a>
 * @since
 */
public class NacosConfigEnvironmentProcessor implements EnvironmentPostProcessor {

    private final LinkedList<NacosConfigUtils.DeferNacosPropertySource> deferPropertySources = new LinkedList<>();

    private Function<Properties, ConfigService> builder = properties -> {
        try {
            return NacosFactory.createConfigService(properties);
        } catch (
                NacosException e) {
            throw new RuntimeException("ConfigService can't be created with properties : " + properties, e);
        }
    };

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        application.addInitializers(new NacosConfigApplicationContextInitializer(this));
        if (enable(environment)) {
            initLogConfig(environment);
        }
    }

    private void initLogConfig(ConfigurableEnvironment environment) {
        NacosConfigProperties nacosConfigProperties = NacosConfigPropertiesUtils.buildNacosConfigProperties(environment);
        NacosConfigUtils configUtils = new NacosConfigUtils(nacosConfigProperties, environment, builder);
        configUtils.loadConfig(true);

        // get defer nacosPropertySource
        deferPropertySources.addAll(configUtils.getNacosPropertySources());
    }

    boolean enable(Environment environment) {
        return Boolean.parseBoolean(environment.getProperty(NacosConfigConstants.NACOS_LOG_BOOTSTRAP, "false"));
    }

    LinkedList<NacosConfigUtils.DeferNacosPropertySource> getDeferPropertySources() {
        return deferPropertySources;
    }
}
