/*
 * Copyright (C) 2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.alibaba.boot.nacos.common;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.alibaba.nacos.api.PropertyKeyConst;

/**
 * @author <a href="mailto:fangjian0423@gmail.com">Jim</a>
 */
public class PropertiesUtils {

	public static Map<Object, Object> extractSafeProperties(Properties properties) {
		Map<Object, Object> result = new HashMap<>();
		properties.forEach((key, val) -> {
			if (!PropertyKeyConst.SECRET_KEY.equals(key)) {
				result.put(key, val);
			}
		});
		return result;
	}

}
