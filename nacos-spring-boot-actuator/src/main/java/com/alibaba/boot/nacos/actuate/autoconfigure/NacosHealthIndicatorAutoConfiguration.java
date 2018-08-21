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
package com.alibaba.boot.nacos.actuate.autoconfigure;

import org.springframework.boot.actuate.autoconfigure.endpoint.EndpointAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.health.ConditionalOnEnabledHealthIndicator;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.alibaba.boot.nacos.actuate.health.NacosHealthIndicator;
import com.alibaba.boot.nacos.actuate.health.NacosHealthIndicatorProperties;
import com.alibaba.boot.nacos.autoconfigure.NacosAutoConfiguration;

/**
 * @author xiaojing
 */
@Configuration
@ConditionalOnClass({ HealthIndicator.class })
@AutoConfigureBefore({ EndpointAutoConfiguration.class })
@AutoConfigureAfter(NacosAutoConfiguration.class)
@ConditionalOnEnabledHealthIndicator("nacos")
@EnableConfigurationProperties(NacosHealthIndicatorProperties.class)
public class NacosHealthIndicatorAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	public NacosHealthIndicator nacosHealthIndicator() {
		return new NacosHealthIndicator();
	}

}
