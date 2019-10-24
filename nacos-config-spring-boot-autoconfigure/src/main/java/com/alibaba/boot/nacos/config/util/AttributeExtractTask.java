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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;

/**
 * @author <a href="mailto:liaochunyhm@live.com">liaochuntao</a>
 * @since 0.2.3
 */
public class AttributeExtractTask implements Callable<Map<String, String>> {

	private final String prefix;
	private final ConfigurableEnvironment environment;

	public AttributeExtractTask(String prefix, ConfigurableEnvironment environment) {
		this.prefix = prefix;
		this.environment = environment;
	}

	@Override
	public Map<String, String> call() throws Exception {
		List<Map<String, String>> defer = new LinkedList<>();
		MutablePropertySources mutablePropertySources = environment.getPropertySources();

		for (PropertySource propertySource : mutablePropertySources) {
			calculate(propertySource.getSource(), defer);
		}

		Map<String, String> result = new HashMap<>(32);
		Collections.reverse(defer);
		for (Map<String, String> item : defer) {
			result.putAll(item);
		}
		return result;
	}

	private void calculate(Object source, List<Map<String, String>> defer) {
		if (source instanceof PropertySource) {
			calculate(((PropertySource) source).getSource(), defer);
		}
		if (source instanceof Map) {
			Map<String, String> map = new HashMap<>(8);
			for (Object entry : ((Map) source).entrySet()) {
				Map.Entry<Object, Object> element = (Map.Entry<Object, Object>) entry;
				String key = String.valueOf(element.getKey());
				if (key.startsWith(prefix)) {
					map.put(key, String.valueOf(element.getValue()));
				}
			}
			if (!map.isEmpty()) {
				defer.add(map);
			}
		}
		if (source instanceof List || source instanceof Set) {
			Collection sources = (Collection) source;
			for (Object obj : sources) {
				calculate(obj, defer);
			}
		}
	}
}
