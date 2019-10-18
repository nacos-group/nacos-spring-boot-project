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
package com.alibaba.boot.nacos.actuate.endpoint;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.boot.nacos.discovery.actuate.endpoint.NacosDiscoveryEndpoint;
import com.alibaba.boot.nacos.discovery.autoconfigure.NacosDiscoveryAutoConfiguration;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * {@link NacosDiscoveryEndpoint} Test
 *
 * @author <a href="mailto:fangjian0423@gmail.com">Jim</a>
 * @see NacosDiscoveryEndpoint
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = { NacosDiscoveryEndpoint.class,
		NacosDiscoveryAutoConfiguration.class })
public class NacosDiscoveryEndpointTest {

	@Autowired
	private NacosDiscoveryEndpoint nacosDiscoveryEndpoint;

	@Test
	public void testInvoke() {
		Map<String, Object> metadata = nacosDiscoveryEndpoint.invoke();

		HashMap nacosDiscoveryGlobalProperties = (HashMap) metadata
				.get("nacosDiscoveryGlobalProperties");

		Assert.assertNotNull(nacosDiscoveryGlobalProperties);
	}

}
