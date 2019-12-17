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

import com.alibaba.boot.nacos.config.properties.NacosConfigProperties;
import com.alibaba.boot.nacos.config.util.Function;
import com.alibaba.boot.nacos.config.util.NacosConfigPropertiesUtils;
import com.alibaba.boot.nacos.config.util.NacosConfigUtils;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.spring.factory.CacheableEventPublishingNacosServiceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * @author <a href="mailto:liaochunyhm@live.com">liaochuntao</a>
 * @since 0.1.2
 */
public class NacosConfigApplicationInitializer
		implements ApplicationContextInitializer<ConfigurableApplicationContext> {

	private final Logger logger = LoggerFactory
			.getLogger(NacosConfigApplicationInitializer.class);

	private ConfigurableEnvironment environment;

	private final NacosConfigEnvironmentProcessor processor;

	private NacosConfigProperties nacosConfigProperties;

	public NacosConfigApplicationInitializer(
			NacosConfigEnvironmentProcessor configEnvironmentProcessor) {
		this.processor = configEnvironmentProcessor;
	}

	@Override
	public void initialize(ConfigurableApplicationContext context) {
		final CacheableEventPublishingNacosServiceFactory singleton = CacheableEventPublishingNacosServiceFactory
				.getSingleton();
		singleton.setApplicationContext(context);
		environment = context.getEnvironment();
		nacosConfigProperties = NacosConfigPropertiesUtils
				.buildNacosConfigProperties(environment);
		processor.publishDeferService(context);
		if (!enable()) {
			logger.info("[Nacos Config Boot] : The preload configuration is not enabled");
		}
		else {
			final Function<Properties, ConfigService> builder = new Function<Properties, ConfigService>() {
				@Override
				public ConfigService apply(Properties input) {
					try {
						return singleton.createConfigService(input);
					}
					catch (NacosException e) {
						throw new NacosBootConfigException(
								"ConfigService can't be created with properties : "
										+ input,
								e);
					}
				}
			};
			final NacosConfigUtils configUtils = new NacosConfigUtils(
					nacosConfigProperties, environment, builder);
			if (processor.enable()) {
				configUtils
						.addListenerIfAutoRefreshed(processor.getDeferPropertySources());
			}
			else {
				configUtils.loadConfig();
				configUtils.addListenerIfAutoRefreshed();
			}
		}
	}

	private boolean enable() {
		return nacosConfigProperties.getBootstrap().isEnable();
	}
}
