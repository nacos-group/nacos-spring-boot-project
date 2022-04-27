/*
 * Copyright 2013-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.alibaba.boot.nacos.config.logging;


import com.alibaba.boot.nacos.config.properties.NacosConfigProperties;
import com.alibaba.boot.nacos.config.util.NacosConfigPropertiesUtils;
import com.alibaba.nacos.client.logging.NacosLogging;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.GenericApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.core.ResolvableType;

/**
 * Reload nacos log configuration file, after
 * {@link org.springframework.boot.context.logging.LoggingApplicationListener}.
 *
 * @author mai.jh
 */
public class NacosLoggingListener implements GenericApplicationListener {

	@Override
	public boolean supportsEventType(ResolvableType resolvableType) {
		Class<?> type = resolvableType.getRawClass();
		if (type != null) {
			return ApplicationEnvironmentPreparedEvent.class.isAssignableFrom(type);
		}
		return false;
	}
	
	@Override
	public boolean supportsSourceType(Class<?> aClass) {
		return true;
	}
	
	@Override
	public void onApplicationEvent(ApplicationEvent applicationEvent) {
		//If the managed log is enabled, load your own log configuration after loading the user log configuration
		ApplicationEnvironmentPreparedEvent applicationEnvironmentPreparedEvent = (ApplicationEnvironmentPreparedEvent) applicationEvent;
		NacosConfigProperties nacosConfigProperties = NacosConfigPropertiesUtils.buildNacosConfigProperties(
				applicationEnvironmentPreparedEvent.getEnvironment());
		if(nacosConfigProperties.getBootstrap().isLogEnable()){
			NacosLogging.getInstance().loadConfiguration();
		}
	}

	@Override
	public int getOrder() {
		return Ordered.HIGHEST_PRECEDENCE + 21;
	}

}
