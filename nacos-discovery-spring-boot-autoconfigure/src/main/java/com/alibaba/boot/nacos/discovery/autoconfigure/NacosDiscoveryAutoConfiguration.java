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
package com.alibaba.boot.nacos.discovery.autoconfigure;

import com.alibaba.nacos.spring.context.annotation.discovery.EnableNacosDiscovery;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import com.alibaba.boot.nacos.discovery.NacosDiscoveryConstants;
import com.alibaba.boot.nacos.discovery.properties.NacosDiscoveryProperties;

import static com.alibaba.nacos.spring.util.NacosBeanUtils.DISCOVERY_GLOBAL_NACOS_PROPERTIES_BEAN_NAME;

/**
 * Nacos Discovery Auto {@link Configuration}
 *
 * @author <a href="mailto:fangjian0423@gmail.com">Jim</a>
 */
@ConditionalOnProperty(name = NacosDiscoveryConstants.ENABLED, matchIfMissing = true)
@ConditionalOnMissingBean(name = DISCOVERY_GLOBAL_NACOS_PROPERTIES_BEAN_NAME)
@EnableNacosDiscovery
@EnableConfigurationProperties(value = {NacosDiscoveryProperties.class})
public class NacosDiscoveryAutoConfiguration {

}
