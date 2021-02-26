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
		if (StringUtils.isNotEmpty(serverAddr)) {
			properties.put(PropertyKeyConst.SERVER_ADDR,
					environment.resolvePlaceholders(serverAddr));
		}
		if (StringUtils.isNotEmpty(namespaceId)) {
			properties.put(PropertyKeyConst.NAMESPACE,
					environment.resolvePlaceholders(namespaceId));
		}
		if (StringUtils.isNotEmpty(endpoint)) {
			properties.put(PropertyKeyConst.ENDPOINT,
					environment.resolvePlaceholders(endpoint));
		}
		if (StringUtils.isNotEmpty(secretKey)) {
			properties.put(PropertyKeyConst.SECRET_KEY,
					environment.resolvePlaceholders(secretKey));
		}
		if (StringUtils.isNotEmpty(accessKey)) {
			properties.put(PropertyKeyConst.ACCESS_KEY,
					environment.resolvePlaceholders(accessKey));
		}
		if (StringUtils.isNotEmpty(ramRoleName)) {
			properties.put(PropertyKeyConst.RAM_ROLE_NAME,
					environment.resolvePlaceholders(ramRoleName));
		}
		if (StringUtils.isNotEmpty(configLongPollTimeout)) {
			properties.put(PropertyKeyConst.CONFIG_LONG_POLL_TIMEOUT,
					environment.resolvePlaceholders(configLongPollTimeout));
		}
		if (StringUtils.isNotEmpty(configRetryTimeout)) {
			properties.put(PropertyKeyConst.CONFIG_RETRY_TIME,
					environment.resolvePlaceholders(configRetryTimeout));
		}
		if (StringUtils.isNotEmpty(maxRetry)) {
			properties.put(PropertyKeyConst.MAX_RETRY,
					environment.resolvePlaceholders(maxRetry));
		}
		if (StringUtils.isNotBlank(username)) {
			properties.put(PropertyKeyConst.USERNAME,
					environment.resolvePlaceholders(username));
		}
		if (StringUtils.isNotBlank(password)) {
			properties.put(PropertyKeyConst.PASSWORD,
					environment.resolvePlaceholders(password));
		}
		properties.put(PropertyKeyConst.ENABLE_REMOTE_SYNC_CONFIG,
				String.valueOf(enableRemoteSyncConfig));
		return properties;
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
