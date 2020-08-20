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

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.alibaba.boot.nacos.config.NacosConfigConstants;
import com.alibaba.boot.nacos.config.properties.NacosConfigProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.core.env.ConfigurableEnvironment;

/**
 * @author <a href="mailto:liaochunyhm@live.com">liaochuntao</a>
 * @since 0.1.3
 */
public class NacosConfigPropertiesUtils {

	private static final String PROPERTIES_PREFIX = "nacos";

	private static final Logger logger = LoggerFactory
			.getLogger(NacosConfigPropertiesUtils.class);

	private static Set<String> OBJ_FIELD_NAME = new HashSet<>();

	static {
		Field[] fields = NacosConfigProperties.class.getDeclaredFields();
		for (Field field : fields) {
			OBJ_FIELD_NAME.add(field.getName());
		}
	}

	public static NacosConfigProperties buildNacosConfigProperties(
			ConfigurableEnvironment environment) {
		NacosConfigProperties bean = new NacosConfigProperties();

		AttributeExtractTask task = new AttributeExtractTask(PROPERTIES_PREFIX,
				environment);

		try {
			Map<String, Object> properties = new HashMap<>();
			properties.putAll(task.call());
			BinderUtils.bind(bean, NacosConfigConstants.PREFIX, properties);
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
		logger.debug("nacosConfigProperties : {}", bean);
		return bean;
	}

}
