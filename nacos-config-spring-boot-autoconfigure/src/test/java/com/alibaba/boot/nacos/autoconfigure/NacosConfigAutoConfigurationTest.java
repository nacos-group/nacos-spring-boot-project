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

import com.alibaba.boot.nacos.config.autoconfigure.NacosConfigAutoConfiguration;
import com.alibaba.boot.nacos.config.properties.NacosConfigProperties;
import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.annotation.NacosInjected;
import com.alibaba.nacos.api.config.ConfigService;
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
 * {@link NacosConfigAutoConfiguration} Test
 *
 * @author <a href="mailto:fangjian0423@gmail.com">Jim</a>
 * @see NacosConfigAutoConfiguration
 */
@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource(properties = { "nacos.config.server-addr=localhost" })
@SpringBootTest(classes = { NacosConfigAutoConfiguration.class })
public class NacosConfigAutoConfigurationTest {

	@Autowired
	private NacosConfigProperties nacosConfigProperties;

	@Autowired
	private ApplicationContext applicationContext;

	@NacosInjected
	private ConfigService configService;

	@Test
	public void testNacosConfig() {
		Assert.assertNotNull(configService);
		Assert.assertNotNull(applicationContext
				.getBean(NacosBeanUtils.CONFIG_GLOBAL_NACOS_PROPERTIES_BEAN_NAME));
		Assert.assertEquals("localhost", nacosConfigProperties.getServerAddr());
	}

	@Test
	public void testNacosConfigGlobalBean() {
		Assert.assertNotNull(applicationContext
				.getBean(NacosBeanUtils.GLOBAL_NACOS_PROPERTIES_BEAN_NAME));
	}

	@Test(expected = NoSuchBeanDefinitionException.class)
	public void testNacosDiscoveryGlobalBean() {
		Assert.assertNull(applicationContext
				.getBean(NacosBeanUtils.DISCOVERY_GLOBAL_NACOS_PROPERTIES_BEAN_NAME));
	}

	@Test
	public void testNacosGlobalProperties() {
		Properties properties = applicationContext.getBean(
				NacosBeanUtils.CONFIG_GLOBAL_NACOS_PROPERTIES_BEAN_NAME,
				Properties.class);
		Assert.assertEquals("localhost",
				properties.getProperty(PropertyKeyConst.SERVER_ADDR));
	}

}
