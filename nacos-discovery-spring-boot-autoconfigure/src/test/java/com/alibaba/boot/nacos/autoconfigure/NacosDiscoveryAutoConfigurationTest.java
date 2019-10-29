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
package com.alibaba.boot.nacos.autoconfigure;

import java.util.Properties;

import com.alibaba.boot.nacos.discovery.autoconfigure.NacosDiscoveryAutoConfiguration;
import com.alibaba.boot.nacos.discovery.properties.NacosDiscoveryProperties;
import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.annotation.NacosInjected;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.spring.util.NacosBeanUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * {@link NacosDiscoveryAutoConfiguration} Test
 *
 * @author <a href="mailto:fangjian0423@gmail.com">Jim</a>
 * @see NacosDiscoveryAutoConfiguration
 */
@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource(properties = { "nacos.discovery.server-addr=localhost" })
@SpringBootTest(classes = { NacosDiscoveryAutoConfiguration.class })
public class NacosDiscoveryAutoConfigurationTest {

	@Autowired
	private NacosDiscoveryProperties nacosDiscoveryProperties;

	@Autowired
	private ApplicationContext applicationContext;

	@NacosInjected
	private NamingService namingService;

	@Test
	public void testNacosDiscovery() {
		Assert.assertNotNull(namingService);
		Assert.assertNotNull(applicationContext
				.getBean(NacosBeanUtils.DISCOVERY_GLOBAL_NACOS_PROPERTIES_BEAN_NAME));
		Assert.assertEquals("localhost", nacosDiscoveryProperties.getServerAddr());
	}

	@Test(expected = NoSuchBeanDefinitionException.class)
	public void testNacosConfigGlobalBean() {
		Assert.assertNull(applicationContext
				.getBean(NacosBeanUtils.GLOBAL_NACOS_PROPERTIES_BEAN_NAME));
	}

	@Test(expected = NoSuchBeanDefinitionException.class)
	public void testNacosDiscoveryGlobalBean() {
		Assert.assertNull(applicationContext
				.getBean(NacosBeanUtils.CONFIG_GLOBAL_NACOS_PROPERTIES_BEAN_NAME));
	}

	@Test
	public void testNacosGlobalProperties() {
		Properties properties = applicationContext.getBean(
				NacosBeanUtils.DISCOVERY_GLOBAL_NACOS_PROPERTIES_BEAN_NAME,
				Properties.class);
		Assert.assertEquals("localhost",
				properties.getProperty(PropertyKeyConst.SERVER_ADDR));
	}

}
