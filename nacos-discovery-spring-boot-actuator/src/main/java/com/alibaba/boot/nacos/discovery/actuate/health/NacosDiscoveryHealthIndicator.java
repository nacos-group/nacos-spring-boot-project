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
package com.alibaba.boot.nacos.discovery.actuate.health;

import java.util.Properties;

import com.alibaba.boot.nacos.common.PropertiesUtils;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.common.utils.JacksonUtils;
import com.alibaba.nacos.spring.factory.CacheableEventPublishingNacosServiceFactory;
import com.alibaba.nacos.spring.factory.NacosServiceFactory;
import com.alibaba.nacos.spring.metadata.NacosServiceMetaData;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.context.ApplicationContext;

/**
 * Nacos Discovery {@link HealthIndicator}
 *
 * @author <a href="mailto:fangjian0423@gmail.com">Jim</a>
 * @see HealthIndicator
 */
public class NacosDiscoveryHealthIndicator extends AbstractHealthIndicator {

	@Autowired
	private ApplicationContext applicationContext;

	private static final String UP_STATUS = "up";

	@Override
	protected void doHealthCheck(Health.Builder builder) throws Exception {
		builder.up();
		NacosServiceFactory nacosServiceFactory = CacheableEventPublishingNacosServiceFactory
				.getSingleton();
		for (NamingService namingService : nacosServiceFactory.getNamingServices()) {
			if (namingService instanceof NacosServiceMetaData) {
				NacosServiceMetaData nacosServiceMetaData = (NacosServiceMetaData) namingService;
				Properties properties = nacosServiceMetaData.getProperties();
				builder.withDetail(
						JacksonUtils.toJson(
								PropertiesUtils.extractSafeProperties(properties)),
						namingService.getServerStatus());
			}
			if (!namingService.getServerStatus().equalsIgnoreCase(UP_STATUS)) {
				builder.down();
			}
		}
	}
}
