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
package com.alibaba.boot.nacos.actuate.endpoint;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.alibaba.boot.nacos.common.PropertiesUtils;
import com.alibaba.boot.nacos.config.NacosConfigConstants;
import com.alibaba.nacos.api.config.annotation.NacosConfigListener;
import com.alibaba.nacos.api.config.annotation.NacosConfigurationProperties;
import com.alibaba.nacos.common.utils.JacksonUtils;
import com.alibaba.nacos.spring.context.event.config.NacosConfigMetadataEvent;
import com.alibaba.nacos.spring.util.NacosUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Element;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.type.AnnotationMetadata;

import static com.alibaba.nacos.spring.util.NacosBeanUtils.CONFIG_GLOBAL_NACOS_PROPERTIES_BEAN_NAME;

/**
 * Actuator {@link Endpoint} to expose Nacos Config Meta Data
 *
 * @author <a href="mailto:fangjian0423@gmail.com">Jim</a>
 * @see Endpoint
 */
@Endpoint(id = NacosConfigConstants.ENDPOINT_PREFIX)
public class NacosConfigEndpoint
		implements ApplicationListener<NacosConfigMetadataEvent> {

	@Autowired
	private ApplicationContext applicationContext;

	private Map<String, JsonNode> nacosConfigMetadataMap = new HashMap<>(8);

	@ReadOperation
	public Map<String, Object> invoke() {
		Map<String, Object> result = new HashMap<>(8);

		if (!(ClassUtils.isAssignable(applicationContext.getEnvironment().getClass(),
				ConfigurableEnvironment.class))) {
			result.put("error", "environment type not match ConfigurableEnvironment: "
					+ applicationContext.getEnvironment().getClass().getName());
		}
		else {

			result.put("nacosConfigMetadata", nacosConfigMetadataMap.values());

			result.put("nacosConfigGlobalProperties",
					PropertiesUtils.extractSafeProperties(applicationContext.getBean(
							CONFIG_GLOBAL_NACOS_PROPERTIES_BEAN_NAME, Properties.class)));
		}

		return result;
	}

	@Override
	public void onApplicationEvent(NacosConfigMetadataEvent event) {
		String key = buildMetadataKey(event);
		if (StringUtils.isNotEmpty(key) && !nacosConfigMetadataMap.containsKey(key)) {
			ObjectNode jsonNode = JacksonUtils.createEmptyJsonNode();
			jsonNode.put("groupId", event.getGroupId());
			jsonNode.put("dataId", event.getDataId());
			if (ClassUtils.isAssignable(event.getSource().getClass(),
					AnnotationMetadata.class)) {
				jsonNode.put("origin", "NacosPropertySource");
				jsonNode.put("target",
						((AnnotationMetadata) event.getSource()).getClassName());
			}
			else if (ClassUtils.isAssignable(event.getSource().getClass(),
					NacosConfigListener.class)) {
				jsonNode.put("origin", "NacosConfigListener");
				Method configListenerMethod = (Method) event.getAnnotatedElement();
				jsonNode.put("target",
						configListenerMethod.getDeclaringClass().getName() + ":"
								+ configListenerMethod.toString());
			}
			else if (ClassUtils.isAssignable(event.getSource().getClass(),
					NacosConfigurationProperties.class)) {
				jsonNode.put("origin", "NacosConfigurationProperties");
				jsonNode.put("target", event.getBeanType().getName());
			}
			else if (ClassUtils.isAssignable(event.getSource().getClass(),
					Element.class)) {
				jsonNode.put("origin", "NacosPropertySource");
				jsonNode.put("target", event.getXmlResource().toString());
			}
			else {
				throw new RuntimeException("unknown NacosConfigMetadataEvent");
			}
			nacosConfigMetadataMap.put(key, jsonNode);
		}
	}

	private String buildMetadataKey(NacosConfigMetadataEvent event) {
		if (event.getXmlResource() != null) {
			return event.getGroupId() + NacosUtils.SEPARATOR + event.getDataId()
					+ NacosUtils.SEPARATOR + event.getXmlResource();
		}
		else {
			if (event.getBeanType() == null && event.getAnnotatedElement() == null) {
				return StringUtils.EMPTY;
			}
			return event.getGroupId() + NacosUtils.SEPARATOR + event.getDataId()
					+ NacosUtils.SEPARATOR + event.getBeanType() + NacosUtils.SEPARATOR
					+ event.getAnnotatedElement();
		}
	}

}
