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
package com.alibaba.boot.nacos.config.binder;

import java.lang.reflect.Method;

import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.annotation.NacosConfigurationProperties;
import com.alibaba.nacos.spring.context.properties.config.NacosConfigurationPropertiesBinder;
import com.alibaba.nacos.spring.core.env.NacosPropertySource;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.ResolvableType;
import org.springframework.core.env.StandardEnvironment;

/**
 * @author <a href="mailto:liaochunyhm@live.com">liaochuntao</a>
 * @since 0.2.2
 */
public class NacosBootConfigurationPropertiesBinder
		extends NacosConfigurationPropertiesBinder {

	private final ConfigurableApplicationContext applicationContext;

	private final StandardEnvironment environment = new StandardEnvironment();

	public NacosBootConfigurationPropertiesBinder(
			ConfigurableApplicationContext applicationContext) {
		super(applicationContext);
		this.applicationContext = applicationContext;
	}

	@Override
	protected void doBind(Object bean, String beanName, String dataId, String groupId,
			String configType, NacosConfigurationProperties properties, String content,
			ConfigService configService) {
		synchronized (this) {
			String name = "nacos-bootstrap-" + beanName;
			NacosPropertySource propertySource = new NacosPropertySource(name, dataId, groupId, content, configType);
			environment.getPropertySources().addLast(propertySource);
			Binder binder = Binder.get(environment);
			ResolvableType type = getBeanType(bean, beanName);
			Bindable<?> target = Bindable.of(type).withExistingValue(bean);
			binder.bind(properties.prefix(), target);
			publishBoundEvent(bean, beanName, dataId, groupId, properties, content, configService);
			publishMetadataEvent(bean, beanName, dataId, groupId, properties);
			environment.getPropertySources().remove(name);
		}
	}

	private ResolvableType getBeanType(Object bean, String beanName) {
		Method factoryMethod = findFactoryMethod(beanName);
		if (factoryMethod != null) {
			return ResolvableType.forMethodReturnType(factoryMethod);
		}
		return ResolvableType.forClass(bean.getClass());
	}

	public Method findFactoryMethod(String beanName) {
		ConfigurableListableBeanFactory beanFactory = this.applicationContext.getBeanFactory();
		if (beanFactory.containsBeanDefinition(beanName)) {
			BeanDefinition beanDefinition = beanFactory.getMergedBeanDefinition(beanName);
			if (beanDefinition instanceof RootBeanDefinition) {
				return ((RootBeanDefinition) beanDefinition).getResolvedFactoryMethod();
			}
		}
		return null;
	}
}
