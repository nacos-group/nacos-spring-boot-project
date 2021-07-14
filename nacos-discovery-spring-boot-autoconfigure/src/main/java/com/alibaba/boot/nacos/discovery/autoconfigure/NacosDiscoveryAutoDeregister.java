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

import com.alibaba.boot.nacos.discovery.properties.NacosDiscoveryProperties;
import com.alibaba.boot.nacos.discovery.properties.Register;
import com.alibaba.nacos.api.annotation.NacosInjected;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.client.naming.utils.NetUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.server.WebServer;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

/**
 * @author <a href="mailto:liaochunyhm@live.com">liaochuntao</a>
 * @since 0.2.4
 */
@Component
public class NacosDiscoveryAutoDeregister
		implements ApplicationListener<ContextClosedEvent> {

	private static final Logger logger = LoggerFactory
			.getLogger(NacosDiscoveryAutoRegister.class);

	@NacosInjected
	private NamingService namingService;

	private final NacosDiscoveryProperties discoveryProperties;
	private final WebServer webServer;

	@Value("${spring.application.name:}")
	private String applicationName;

	public NacosDiscoveryAutoDeregister(NacosDiscoveryProperties discoveryProperties,
			WebServer webServer) {
		this.discoveryProperties = discoveryProperties;
		this.webServer = webServer;
	}

	@Override
	public void onApplicationEvent(ContextClosedEvent event) {
		if (!discoveryProperties.isAutoRegister()) {
			return;
		}

		Register register = discoveryProperties.getRegister();

		if (StringUtils.isEmpty(register.getIp())) {
			register.setIp(NetUtils.localIP());
		}

		if (register.getPort() == 0) {
			register.setPort(webServer.getPort());
		}

		String serviceName = register.getServiceName();

		if (StringUtils.isEmpty(serviceName)){
			if (StringUtils.isEmpty(applicationName)){
				throw new AutoDeregisterException("serviceName notNull");
			}
			serviceName = applicationName;
		}

		try {
			namingService.deregisterInstance(serviceName, register.getGroupName(),
					register);
			logger.info("Finished auto deregister service : {}, ip : {}, port : {}",
					serviceName, register.getIp(), register.getPort());
		}
		catch (NacosException e) {
			throw new AutoDeregisterException(e);
		}
	}
}
