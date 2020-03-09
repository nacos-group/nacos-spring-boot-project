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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Properties;
import java.util.function.Function;

import com.alibaba.boot.nacos.config.properties.NacosConfigProperties;
import com.alibaba.boot.nacos.config.util.NacosConfigLoader;
import com.alibaba.boot.nacos.config.util.NacosConfigPropertiesUtils;
import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.spring.factory.CacheableEventPublishingNacosServiceFactory;
import com.alibaba.nacos.spring.util.NacosUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * In the Context to create premise before loading the log configuration information
 *
 * @author <a href="mailto:liaochunyhm@live.com">liaochuntao</a>
 * @since 0.2.3
 */
public class NacosConfigEnvironmentProcessor
		implements EnvironmentPostProcessor, Ordered {

	private final Logger logger = LoggerFactory
			.getLogger(NacosConfigEnvironmentProcessor.class);
	private final CacheableEventPublishingNacosServiceFactory nacosServiceFactory = CacheableEventPublishingNacosServiceFactory
			.getSingleton();
	private final Map<String, ConfigService> serviceCache = new HashMap<>(8);
	private final LinkedList<NacosConfigLoader.DeferNacosPropertySource> deferPropertySources = new LinkedList<>();
	private NacosConfigProperties nacosConfigProperties;

	// Because ApplicationContext has not been injected at preload time, need to manually
	// cache the created Service to prevent duplicate creation
	private Function<Properties, ConfigService> builder = properties -> {
		try {
			final String key = NacosUtils.identify(properties);
			if (serviceCache.containsKey(key)) {
				return serviceCache.get(key);
			}
			final ConfigService configService = NacosFactory
					.createConfigService(properties);
			serviceCache.put(key, configService);
			return nacosServiceFactory.deferCreateService(configService, properties);
		}
		catch (NacosException e) {
			throw new NacosBootConfigException(
					"ConfigService can't be created with properties : " + properties, e);
		}
	};

	@Override
	public void postProcessEnvironment(ConfigurableEnvironment environment,
			SpringApplication application) {
		application.addInitializers(new NacosConfigApplicationContextInitializer(this));
		nacosConfigProperties = NacosConfigPropertiesUtils
				.buildNacosConfigProperties(environment);
		if (enable()) {
			System.out.println(
					"[Nacos Config Boot] : The preload log configuration is enabled");
			loadConfig(environment);
		}
	}

	private void loadConfig(ConfigurableEnvironment environment) {
		NacosConfigLoader configLoader = new NacosConfigLoader(nacosConfigProperties,
				environment, builder);
		configLoader.loadConfig();
		// set defer NacosPropertySource
		deferPropertySources.addAll(configLoader.getNacosPropertySources());
	}

	boolean enable() {
		return nacosConfigProperties != null
				&& nacosConfigProperties.getBootstrap().isLogEnable();
	}

	LinkedList<NacosConfigLoader.DeferNacosPropertySource> getDeferPropertySources() {
		return deferPropertySources;
	}

	// Do not set the minimum priority for future expansion needs

	@Override
	public int getOrder() {
		return Ordered.LOWEST_PRECEDENCE - 5;
	}

	void publishDeferService(ApplicationContext context) {
		try {
			nacosServiceFactory.publishDeferService(context);
			serviceCache.clear();
		}
		catch (Exception e) {
			logger.error("publish defer ConfigService has some error : {}", e);
		}
	}
}
