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
package com.alibaba.boot.nacos.sample;

import java.util.List;
import java.util.Map;

import com.alibaba.nacos.api.config.ConfigType;
import com.alibaba.nacos.api.config.annotation.NacosConfigurationProperties;

import org.springframework.context.annotation.Configuration;

/**
 * @author <a href="mailto:liaochunyhm@live.com">liaochuntao</a>
 * @since
 */
@NacosConfigurationProperties(prefix = "apple", dataId = "apple", type = ConfigType.YAML, autoRefreshed = true)
@Configuration
public class Apple {

	private List<String> list;

	private Map<String, List<String>> listMap;

	public List<String> getList() {
		return list;
	}

	public void setList(List<String> list) {
		this.list = list;
	}

	public Map<String, List<String>> getListMap() {
		return listMap;
	}

	public void setListMap(Map<String, List<String>> listMap) {
		this.listMap = listMap;
	}

	@Override
	public String toString() {
		return "Apple{" + "list=" + list + ", listMap=" + listMap + '}';
	}
}
