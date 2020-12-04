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

import com.alibaba.boot.nacos.config.util.BinderUtils;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.annotation.NacosConfigurationProperties;
import com.alibaba.nacos.spring.context.properties.config.NacosConfigurationPropertiesBinder;
import com.alibaba.nacos.spring.util.NacosUtils;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Map;


/**
 * @author <a href="mailto:liaochunyhm@live.com">liaochuntao</a>
 * @since 0.1.2
 */
public class NacosBootConfigurationPropertiesBinder
		extends NacosConfigurationPropertiesBinder {

	private ConfigurableApplicationContext context;

	public NacosBootConfigurationPropertiesBinder(
			ConfigurableApplicationContext applicationContext) {
		super(applicationContext);
		this.context = applicationContext;
	}

	@Override
	protected void doBind(Object bean, String beanName, String dataId, String groupId,
			String configType, NacosConfigurationProperties properties, String content,
			ConfigService configService) {
		Map<String, Object> prop = NacosUtils.toProperties(dataId, groupId, content, configType);
		BinderUtils.bind(bean, properties.prefix(), prop);
		publishBoundEvent(bean, beanName, dataId, groupId, properties, content,
				configService);
		publishMetadataEvent(bean, beanName, dataId, groupId, properties);
	}

}
