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

import java.util.Properties;
import java.util.function.Function;

import com.alibaba.boot.nacos.config.properties.NacosConfigProperties;
import com.alibaba.boot.nacos.config.util.NacosConfigLoader;
import com.alibaba.boot.nacos.config.util.NacosConfigPropertiesUtils;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.spring.factory.CacheableEventPublishingNacosServiceFactory;
import com.alibaba.nacos.spring.util.NacosBeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * @author <a href="mailto:liaochunyhm@live.com">liaochuntao</a>
 * @since 0.2.2
 */
public class NacosConfigApplicationContextInitializer
		implements ApplicationContextInitializer<ConfigurableApplicationContext> {

	private final Logger logger = LoggerFactory
			.getLogger(NacosConfigApplicationContextInitializer.class);
	private final NacosConfigEnvironmentProcessor processor;
	private final CacheableEventPublishingNacosServiceFactory singleton = CacheableEventPublishingNacosServiceFactory
			.getSingleton();
	private final Function<Properties, ConfigService> builder = properties -> {
		try {
			return singleton.createConfigService(properties);
		}
		catch (NacosException e) {
			throw new NacosBootConfigException(
					"ConfigService can't be created with properties : " + properties, e);
		}
	};
	private ConfigurableEnvironment environment;
	private NacosConfigProperties nacosConfigProperties;

	public NacosConfigApplicationContextInitializer(
			NacosConfigEnvironmentProcessor configEnvironmentProcessor) {
		this.processor = configEnvironmentProcessor;
	}

	@Override
	public void initialize(ConfigurableApplicationContext context) {
		singleton.setApplicationContext(context);
		environment = context.getEnvironment();
		nacosConfigProperties = NacosConfigPropertiesUtils
				.buildNacosConfigProperties(environment);
		final NacosConfigLoader configLoader = new NacosConfigLoader(
				nacosConfigProperties, environment, builder);
		if (!enable()) {
			logger.info("[Nacos Config Boot] : The preload configuration is not enabled");
		}
		else {

			// If it opens the log level loading directly will cache
			// DeferNacosPropertySource release

			if (processor.enable()) {
				processor.publishDeferService(context);
				configLoader
						.addListenerIfAutoRefreshed(processor.getDeferPropertySources());
			}
			else {
				configLoader.loadConfig();
				configLoader.addListenerIfAutoRefreshed();
			}
		}

		final ConfigurableListableBeanFactory factory = context.getBeanFactory();
		if (!factory
				.containsSingleton(NacosBeanUtils.GLOBAL_NACOS_PROPERTIES_BEAN_NAME)) {
			factory.registerSingleton(NacosBeanUtils.GLOBAL_NACOS_PROPERTIES_BEAN_NAME,
					configLoader.buildGlobalNacosProperties());
		}
	}

	private boolean enable() {
		return processor.enable() || nacosConfigProperties.getBootstrap().isEnable();
	}

}
