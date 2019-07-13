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
import com.alibaba.boot.nacos.config.util.NacosPropertiesBuilder;
import com.alibaba.nacos.api.config.ConfigType;
import com.alibaba.nacos.spring.core.env.NacosPropertySource;
import com.alibaba.nacos.spring.core.env.NacosPropertySourcePostProcessor;
import com.alibaba.nacos.spring.factory.CacheableEventPublishingNacosServiceFactory;
import com.alibaba.nacos.spring.util.NacosBeanUtils;
import com.alibaba.nacos.spring.util.NacosUtils;
import com.alibaba.nacos.spring.util.config.NacosConfigLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationBeanFactoryMetadata;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.ResolvableType;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.util.StringUtils;

import java.util.Properties;

import static com.alibaba.nacos.spring.util.NacosUtils.buildDefaultPropertySourceName;

/**
 * @author <a href="mailto:liaochunyhm@live.com">liaochuntao</a>
 * @since
 */
public class NacosApplicationContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private final Logger logger = LoggerFactory.getLogger(NacosApplicationContextInitializer.class);

    private ConfigurableEnvironment environment;

    private NacosConfigProperties nacosConfigProperties;

    private NacosConfigLoader nacosConfigLoader;

    @Override
    public void initialize(ConfigurableApplicationContext context) {
        CacheableEventPublishingNacosServiceFactory singleton = CacheableEventPublishingNacosServiceFactory.getSingleton();
        singleton.setApplicationContext(context);
        environment = context.getEnvironment();
        nacosConfigLoader = new NacosConfigLoader(environment);
        nacosConfigLoader.setNacosServiceFactory(singleton);
        if (!isEnable()) {
            logger.info("[Nacos Config Boot] : The preload configuration is not enabled");
        } else {
            nacosConfigProperties = NacosConfigPropertiesUtils.buildNacosConfigProperties(environment);
            Properties globalProperties = buildGlobalNacosProperties();
            MutablePropertySources mutablePropertySources = environment.getPropertySources();
            mutablePropertySources.addLast(reqGlobalNacosConfig(globalProperties, nacosConfigProperties.getType()));
            for (NacosConfigProperties.Config config : nacosConfigProperties.getExtConfig()) {
                mutablePropertySources.addLast(reqSubNacosConfig(config, globalProperties, config.getType()));
            }
        }
    }

    private boolean isEnable() {
        return Boolean.valueOf(environment.getProperty(NacosConfigConstants.NACOS_BOOTSTRAP, "false"));
    }

    private Properties buildGlobalNacosProperties() {
        return NacosPropertiesBuilder.buildNacosProperties(nacosConfigProperties.getServerAddr(), nacosConfigProperties.getNamespace(),
                nacosConfigProperties.getEndpoint(), nacosConfigProperties.getSecretKey(), nacosConfigProperties.getAccessKey(),
                nacosConfigProperties.getConfigLongPollTimeout(), nacosConfigProperties.getConfigRetryTime(),
                nacosConfigProperties.getMaxRetry(), nacosConfigProperties.isEnableRemoteSyncConfig());
    }

    private Properties buildSubNacosProperties(Properties globalProperties, NacosConfigProperties.Config config) {
        if (StringUtils.isEmpty(config.getServerAddr())) {
            return globalProperties;
        }
        Properties sub = NacosPropertiesBuilder.buildNacosProperties(config.getServerAddr(), config.getNamespace(),
                config.getEndpoint(), config.getSecretKey(), config.getAccessKey(), config.getConfigLongPollTimeout(),
                config.getConfigRetryTime(), config.getMaxRetry(), config.isEnableRemoteSyncConfig());
        NacosPropertiesBuilder.merge(sub, globalProperties);
        return sub;
    }

    private NacosPropertySource reqGlobalNacosConfig(Properties globalProperties, ConfigType type) {
        NacosPropertySource propertySource = reqNacosConfig(globalProperties, nacosConfigProperties.getDataId(), nacosConfigProperties.getGroup(), type);
        propertySource.setAutoRefreshed(nacosConfigProperties.isAutoRefresh());
        NacosPropertySourcePostProcessor.addListenerIfAutoRefreshed(propertySource, globalProperties, environment);
        return propertySource;
    }

    private NacosPropertySource reqSubNacosConfig(NacosConfigProperties.Config config, Properties globalProperties, ConfigType type) {
        Properties subConfigProperties = buildSubNacosProperties(globalProperties, config);
        NacosPropertySource nacosPropertySource = reqNacosConfig(subConfigProperties, config.getDataId(), config.getGroup(), type);
        nacosPropertySource.setAutoRefreshed(config.isAutoRefresh());
        NacosPropertySourcePostProcessor.addListenerIfAutoRefreshed(nacosPropertySource, subConfigProperties, environment);
        return nacosPropertySource;
    }

    private NacosPropertySource reqNacosConfig(Properties configProperties, String dataId, String groupId, ConfigType type) {
        NacosPropertySource nacosPropertySource;
        String config = nacosConfigLoader.load(dataId, groupId, configProperties);
        nacosPropertySource = new NacosPropertySource(dataId, groupId, buildDefaultPropertySourceName(dataId, groupId, configProperties), config, type.getType());
        nacosPropertySource.setDataId(dataId);
        nacosPropertySource.setType(type.getType());
        nacosPropertySource.setGroupId(groupId);
        return nacosPropertySource;
    }
}
