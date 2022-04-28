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

import java.util.Map;
import java.util.Properties;

import com.alibaba.nacos.api.PropertyKeyConst;
import org.apache.commons.lang3.StringUtils;

import org.springframework.core.env.Environment;
import org.springframework.util.CollectionUtils;

/**
 * @author <a href="mailto:liaochunyhm@live.com">liaochuntao</a>
 * @since 0.2.3
 */
public class NacosPropertiesBuilder {

	public static Properties buildNacosProperties(Environment environment,
			String serverAddr, String namespaceId, String endpoint, String secretKey,
			String accessKey, String ramRoleName, String configLongPollTimeout,
			String configRetryTimeout, String maxRetry, boolean enableRemoteSyncConfig,
			String username, String password) {
		Properties properties = new Properties();
		processPropertiesData(properties,environment,serverAddr,PropertyKeyConst.SERVER_ADDR);
		processPropertiesData(properties,environment,namespaceId,PropertyKeyConst.NAMESPACE);
		processPropertiesData(properties,environment,endpoint,PropertyKeyConst.ENDPOINT);
		processPropertiesData(properties,environment,secretKey,PropertyKeyConst.SECRET_KEY);
		processPropertiesData(properties,environment,accessKey,PropertyKeyConst.ACCESS_KEY);
		processPropertiesData(properties,environment,ramRoleName,PropertyKeyConst.RAM_ROLE_NAME);
		processPropertiesData(properties,environment,configLongPollTimeout,PropertyKeyConst.CONFIG_LONG_POLL_TIMEOUT);
		processPropertiesData(properties,environment,configRetryTimeout,PropertyKeyConst.CONFIG_RETRY_TIME);
		processPropertiesData(properties,environment,maxRetry,PropertyKeyConst.MAX_RETRY);
		processPropertiesData(properties,environment,username,PropertyKeyConst.USERNAME);
		processPropertiesData(properties,environment,password,PropertyKeyConst.PASSWORD);
		
		properties.put(PropertyKeyConst.ENABLE_REMOTE_SYNC_CONFIG,
				String.valueOf(enableRemoteSyncConfig));
		return properties;
	}
	
	private static void processPropertiesData(Properties properties,Environment environment,String keyword,String key) {
		if (StringUtils.isNotBlank(keyword)) {
			properties.put(key ,environment.resolvePlaceholders(keyword));
		}
	}

	public static void merge(Properties targetProperties, Properties sourceProperties) {

		if (CollectionUtils.isEmpty(sourceProperties)) {
			return;
		}

		for (Map.Entry entry : sourceProperties.entrySet()) {
			String propertyName = (String) entry.getKey();
			if (!targetProperties.containsKey(propertyName)) {
				String propertyValue = (String) entry.getValue();
				targetProperties.setProperty(propertyName, propertyValue);
			}
		}

	}

}
