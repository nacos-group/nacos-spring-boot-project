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

/**
 * @author <a href="mailto:liaochunyhm@live.com">liaochuntao</a>
 * @since
 */
package com.alibaba.boot.nacos.config.autoconfigure;

import com.alibaba.boot.nacos.config.NacosConfigConstants;
import com.alibaba.boot.nacos.config.properties.NacosConfigProperties;
import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.spring.core.env.NacosPropertySource;
import com.alibaba.nacos.spring.core.env.NacosPropertySourcePostProcessor;
import com.alibaba.nacos.spring.factory.CacheableEventPublishingNacosServiceFactory;
import com.alibaba.nacos.spring.util.config.NacosConfigLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.util.StringUtils;

import java.util.Properties;

import static com.alibaba.boot.nacos.config.util.NacosConfigPropertiesAnalyze.*;
import static com.alibaba.nacos.spring.util.NacosUtils.buildDefaultPropertySourceName;

/**
 * In order to support the {@link ConditionalOnProperty}, need to get the configuration information under
 * nacos config server in advance and add it to the {@link org.springframework.core.env.Environment}
 * with the highest priority
 *
 * @author liaochuntao
 */
public class NacosApplicationContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private final Logger logger = LoggerFactory.getLogger(getClass());

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
            nacosConfigProperties = buildNacosConfigProperties(environment);
            Properties globalProperties = buildGlobalNacosProperties();
            MutablePropertySources mutablePropertySources = environment.getPropertySources();
            mutablePropertySources.addLast(reqGlobalNacosConfig(globalProperties));
            for (NacosConfigProperties.Config config : nacosConfigProperties.getExtConfig()) {
                mutablePropertySources.addLast(reqSubNacosConfig(config, globalProperties));
            }
        }
    }

    private boolean isEnable() {
        String isEnable = environment.getProperty(NacosConfigConstants.NACOS_BOOTSTRAP, "false");
        return Boolean.valueOf(isEnable);
    }

    private Properties buildGlobalNacosProperties() {
        Properties properties = new Properties();
        properties.put(PropertyKeyConst.SERVER_ADDR, nacosConfigProperties.getServerAddr());
        properties.put(PropertyKeyConst.NAMESPACE, nacosConfigProperties.getNamespace());
        properties.put(PropertyKeyConst.ENDPOINT, nacosConfigProperties.getEndpoint());
        properties.put(PropertyKeyConst.CLUSTER_NAME, nacosConfigProperties.getClusterName());
        properties.put(PropertyKeyConst.SECRET_KEY, nacosConfigProperties.getSecretKey());
        properties.put(PropertyKeyConst.ACCESS_KEY, nacosConfigProperties.getAccessKey());
        return properties;
    }

    private Properties buildSubNacosProperties(Properties globalProperties, NacosConfigProperties.Config config) {
        if (StringUtils.isEmpty(config.getServerAddr())) {
            return globalProperties;
        }
        Properties properties = new Properties();
        properties.put(PropertyKeyConst.SERVER_ADDR, config.getServerAddr());
        properties.put(PropertyKeyConst.NAMESPACE, config.getNamespace());
        properties.put(PropertyKeyConst.ENDPOINT, config.getEndpoint());
        properties.put(PropertyKeyConst.CLUSTER_NAME, config.getClusterName());
        properties.put(PropertyKeyConst.SECRET_KEY, config.getSecretKey());
        properties.put(PropertyKeyConst.ACCESS_KEY, config.getAccessKey());
        return properties;
    }

    private NacosPropertySource reqGlobalNacosConfig(Properties globalProperties) {
        NacosPropertySource propertySource = reqNacosConfig(globalProperties, nacosConfigProperties.getDataId(), nacosConfigProperties.getGroup());
        propertySource.setAutoRefreshed(nacosConfigProperties.isAutoRefresh());
        NacosPropertySourcePostProcessor.addListenerIfAutoRefreshed(propertySource, globalProperties, environment);
        return propertySource;
    }

    private NacosPropertySource reqSubNacosConfig(NacosConfigProperties.Config config, Properties globalProperties) {
        Properties subConfigProperties = buildSubNacosProperties(globalProperties, config);
        NacosPropertySource nacosPropertySource = reqNacosConfig(subConfigProperties, config.getDataId(), config.getGroup());
        nacosPropertySource.setAutoRefreshed(config.isAutoRefresh());
        NacosPropertySourcePostProcessor.addListenerIfAutoRefreshed(nacosPropertySource, subConfigProperties, environment);
        return nacosPropertySource;
    }

    private NacosPropertySource reqNacosConfig(Properties configProperties, String dataId, String groupId) {
        NacosPropertySource nacosPropertySource;
        String config = nacosConfigLoader.load(dataId, groupId, configProperties);
        nacosPropertySource = new NacosPropertySource(buildDefaultPropertySourceName(dataId, groupId, configProperties), config);
        nacosPropertySource.setDataId(dataId);
        nacosPropertySource.setGroupId(groupId);
        return nacosPropertySource;
    }

}
