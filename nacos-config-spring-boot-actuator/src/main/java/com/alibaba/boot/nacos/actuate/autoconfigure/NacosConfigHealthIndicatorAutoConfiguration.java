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

import com.alibaba.boot.nacos.actuate.health.NacosConfigHealthIndicator;
import com.alibaba.boot.nacos.config.NacosConfigConstants;
import com.alibaba.boot.nacos.config.autoconfigure.NacosConfigAutoConfiguration;

import org.springframework.boot.actuate.autoconfigure.endpoint.EndpointAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.health.ConditionalOnEnabledHealthIndicator;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Nacos {@link NacosConfigHealthIndicator} Auto Configuration
 *
 * @author <a href="mailto:fangjian0423@gmail.com">Jim</a>
 */
@Configuration
@ConditionalOnClass({ HealthIndicator.class })
@AutoConfigureBefore({ EndpointAutoConfiguration.class })
@AutoConfigureAfter(NacosConfigAutoConfiguration.class)
@ConditionalOnEnabledHealthIndicator(NacosConfigConstants.ENDPOINT_PREFIX)
public class NacosConfigHealthIndicatorAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	public NacosConfigHealthIndicator nacosConfigHealthIndicator() {
		return new NacosConfigHealthIndicator();
	}

}
