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

import com.alibaba.boot.nacos.config.NacosConfigConstants;
import com.alibaba.boot.nacos.config.properties.NacosConfigProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.core.ResolvableType;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Objects;

/**
 * Springboot used to own property binding configured binding
 *
 * @author <a href="mailto:liaochunyhm@live.com">liaochuntao</a>
 * @since 0.2.3
 */
public class NacosConfigPropertiesUtils {

	private static final Logger logger = LoggerFactory
			.getLogger(NacosConfigPropertiesUtils.class);

    public static NacosConfigProperties buildNacosConfigProperties(
            ConfigurableEnvironment environment) {
        NacosConfigProperties nacosConfigProperties = new NacosConfigProperties();
        Binder binder = Binder.get(environment);
        ResolvableType type = ResolvableType.forClass(NacosConfigProperties.class);
        Bindable<?> target = Bindable.of(type).withExistingValue(nacosConfigProperties);
        binder.bind(NacosConfigConstants.PREFIX, target);
        //extConfig继承mainConfig的参数设置(除dataId和dataIds)
        if (!CollectionUtils.isEmpty(nacosConfigProperties.getExtConfig())) {
            nacosConfigProperties.getExtConfig().forEach(ext -> {
                if (StringUtils.isEmpty(ext.getServerAddr())) {
                    ext.setServerAddr(nacosConfigProperties.getServerAddr());
                }
                if (StringUtils.isEmpty(ext.getEndpoint())) {
                    ext.setEndpoint(nacosConfigProperties.getEndpoint());
                }
                if (StringUtils.isEmpty(ext.getNamespace())) {
                    ext.setNamespace(nacosConfigProperties.getNamespace());
                }
                if (StringUtils.isEmpty(ext.getAccessKey())) {
                    ext.setAccessKey(nacosConfigProperties.getAccessKey());
                }
                if (StringUtils.isEmpty(ext.getSecretKey())) {
                    ext.setSecretKey(nacosConfigProperties.getSecretKey());
                }
                if (StringUtils.isEmpty(ext.getRamRoleName())) {
                    ext.setRamRoleName(nacosConfigProperties.getRamRoleName());
                }
                if (StringUtils.isEmpty(ext.getGroup())) {
                    ext.setGroup(nacosConfigProperties.getGroup());
                }
                if (Objects.isNull(ext.getType())) {
                    ext.setType(nacosConfigProperties.getType());
                }
                if (StringUtils.isEmpty(ext.getMaxRetry())) {
                    ext.setMaxRetry(nacosConfigProperties.getMaxRetry());
                }
                if (StringUtils.isEmpty(ext.getConfigLongPollTimeout())) {
                    ext.setConfigLongPollTimeout(nacosConfigProperties.getConfigLongPollTimeout());
                }
                if (StringUtils.isEmpty(ext.getConfigRetryTime())) {
                    ext.setConfigRetryTime(nacosConfigProperties.getConfigRetryTime());
                }
                if (Objects.isNull(ext.getAutoRefresh())) {
                    ext.setAutoRefresh(nacosConfigProperties.isAutoRefresh());
                }
                if (Objects.isNull(ext.getEnableRemoteSyncConfig())) {
                    ext.setEnableRemoteSyncConfig(nacosConfigProperties.isEnableRemoteSyncConfig());
                }
            });
        }
        logger.info("nacosConfigProperties : {}", nacosConfigProperties);
        return nacosConfigProperties;
    }

}
