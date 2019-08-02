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
import com.alibaba.nacos.api.config.ConfigType;
import com.alibaba.nacos.spring.core.env.NacosPropertySource;
import com.alibaba.nacos.spring.core.env.NacosPropertySourcePostProcessor;
import com.alibaba.nacos.spring.util.NacosUtils;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.util.StringUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.function.Function;

import static com.alibaba.nacos.spring.util.NacosUtils.buildDefaultPropertySourceName;

/**
 * @author <a href="mailto:liaochunyhm@live.com">liaochuntao</a>
 * @since
 */
public class NacosConfigUtils {

    private final NacosConfigProperties nacosConfigProperties;
    private final ConfigurableEnvironment environment;
    private Function<Properties, ConfigService> builder;
    private List<DeferNacosPropertySource> nacosPropertySources = new LinkedList<>();

    public NacosConfigUtils(NacosConfigProperties nacosConfigProperties, ConfigurableEnvironment environment, Function<Properties, ConfigService> builder) {
        this.nacosConfigProperties = nacosConfigProperties;
        this.environment = environment;
        this.builder = builder;
    }

    public void loadConfig(boolean isLogPre) {
        Properties globalProperties = buildGlobalNacosProperties();
        MutablePropertySources mutablePropertySources = environment.getPropertySources();

        if (isLogPre) {
            NacosPropertySource first = reqGlobalNacosConfig(globalProperties, nacosConfigProperties.getType());
            mutablePropertySources.addFirst(first);
            String preName = first.getName();
            for (NacosConfigProperties.Config config : nacosConfigProperties.getExtConfig()) {
                NacosPropertySource element = reqSubNacosConfig(config, globalProperties, config.getType());
                mutablePropertySources.addAfter(preName, element);
                preName = element.getName();
            }
        } else {
            mutablePropertySources.addLast(reqGlobalNacosConfig(globalProperties, nacosConfigProperties.getType()));
            for (NacosConfigProperties.Config config : nacosConfigProperties.getExtConfig()) {
                mutablePropertySources.addLast(reqSubNacosConfig(config, globalProperties, config.getType()));
            }
        }
    }

    public Properties buildGlobalNacosProperties() {
        return NacosPropertiesBuilder.buildNacosProperties(nacosConfigProperties.getServerAddr(), nacosConfigProperties.getNamespace(),
                nacosConfigProperties.getEndpoint(), nacosConfigProperties.getSecretKey(), nacosConfigProperties.getAccessKey(),
                nacosConfigProperties.getConfigLongPollTimeout(), nacosConfigProperties.getConfigRetryTime(),
                nacosConfigProperties.getMaxRetry(), nacosConfigProperties.isEnableRemoteSyncConfig());
    }

    public Properties buildSubNacosProperties(Properties globalProperties, NacosConfigProperties.Config config) {
        if (StringUtils.isEmpty(config.getServerAddr())) {
            return globalProperties;
        }
        Properties sub = NacosPropertiesBuilder.buildNacosProperties(config.getServerAddr(), config.getNamespace(),
                config.getEndpoint(), config.getSecretKey(), config.getAccessKey(), config.getConfigLongPollTimeout(),
                config.getConfigRetryTime(), config.getMaxRetry(), config.isEnableRemoteSyncConfig());
        NacosPropertiesBuilder.merge(sub, globalProperties);
        return sub;
    }

    public NacosPropertySource reqGlobalNacosConfig(Properties globalProperties, ConfigType type) {
        NacosPropertySource propertySource = reqNacosConfig(globalProperties, nacosConfigProperties.getDataId(), nacosConfigProperties.getGroup(), type);
        propertySource.setAutoRefreshed(nacosConfigProperties.isAutoRefresh());

        // defer publish auto-refresh NacosPropertySource
        nacosPropertySources.add(new DeferNacosPropertySource(propertySource, globalProperties, environment));
        return propertySource;
    }

    public NacosPropertySource reqSubNacosConfig(NacosConfigProperties.Config config, Properties globalProperties, ConfigType type) {
        Properties subConfigProperties = buildSubNacosProperties(globalProperties, config);
        NacosPropertySource nacosPropertySource = reqNacosConfig(subConfigProperties, config.getDataId(), config.getGroup(), type);
        nacosPropertySource.setAutoRefreshed(config.isAutoRefresh());

        // defer publish auto-refresh NacosPropertySource
        nacosPropertySources.add(new DeferNacosPropertySource(nacosPropertySource, subConfigProperties, environment));
        return nacosPropertySource;
    }

    public NacosPropertySource reqNacosConfig(Properties configProperties, String dataId, String groupId, ConfigType type) {
        String config = NacosUtils.getContent(builder.apply(configProperties), dataId, groupId);
        NacosPropertySource nacosPropertySource = new NacosPropertySource(dataId, groupId,
                buildDefaultPropertySourceName(dataId, groupId, configProperties), config, type.getType());
        nacosPropertySource.setDataId(dataId);
        nacosPropertySource.setType(type.getType());
        nacosPropertySource.setGroupId(groupId);
        return nacosPropertySource;
    }

    public void addListenerIfAutoRefreshed() {
        addListenerIfAutoRefreshed(nacosPropertySources);
    }

    public void addListenerIfAutoRefreshed(final List<DeferNacosPropertySource> deferNacosPropertySources) {
        for (DeferNacosPropertySource deferNacosPropertySource : deferNacosPropertySources) {
            NacosPropertySourcePostProcessor.addListenerIfAutoRefreshed(deferNacosPropertySource.getNacosPropertySource(),
                    deferNacosPropertySource.getProperties(), deferNacosPropertySource.getEnvironment());
        }
    }

    public List<DeferNacosPropertySource> getNacosPropertySources() {
        return nacosPropertySources;
    }

    public static class DeferNacosPropertySource {

        private final NacosPropertySource nacosPropertySource;
        private final ConfigurableEnvironment environment;
        private final Properties properties;

        public DeferNacosPropertySource(NacosPropertySource nacosPropertySource, Properties properties, ConfigurableEnvironment environment) {
            this.nacosPropertySource = nacosPropertySource;
            this.properties = properties;
            this.environment = environment;
        }

        public NacosPropertySource getNacosPropertySource() {
            return nacosPropertySource;
        }

        public ConfigurableEnvironment getEnvironment() {
            return environment;
        }

        public Properties getProperties() {
            return properties;
        }
    }
}
