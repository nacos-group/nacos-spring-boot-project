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

import com.alibaba.nacos.api.annotation.NacosInjected;
import com.alibaba.nacos.api.common.Constants;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.alibaba.nacos.spring.context.annotation.config.EnableNacosConfig;
import com.alibaba.nacos.spring.context.annotation.config.NacosPropertySource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.EnableScheduling;

import static org.springframework.core.env.StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME;
import static org.springframework.core.env.StandardEnvironment.SYSTEM_PROPERTIES_PROPERTY_SOURCE_NAME;

/**
 * @author <a href="mailto:fangjian0423@gmail.com">Jim</a>
 */
@SpringBootApplication
@NacosPropertySource(name = "custom", dataId = ConfigApplication.DATA_ID, first = true, before = SYSTEM_PROPERTIES_PROPERTY_SOURCE_NAME, after = SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME)
@EnableScheduling
@EnableNacosConfig
public class ConfigApplication {

	public static final String content = "dept=Aliware\ngroup=Alibaba";

	public static final String DATA_ID = "test";

	public static void main(String[] args) {
		SpringApplication.run(ConfigApplication.class, args);
	}

	@Bean
	@Order(Ordered.LOWEST_PRECEDENCE)
	public CommandLineRunner firstCommandLineRunner() {
		return new FirstCommandLineRunner();
	}

	@Bean
	@Order(Ordered.LOWEST_PRECEDENCE - 1)
	public CommandLineRunner secondCommandLineRunner() {
		return new SecondCommandLineRunner();
	}

	@Bean
	public Foo foo() {
		return new Foo();
	}

	@Configuration
	@ConditionalOnProperty(prefix = "people", name = "enable", havingValue = "true")
	protected static class People {

		@Bean
		public Object object() {
			System.err.println("[liaochuntao] : " + this.getClass().getCanonicalName());
			return new Object();
		}

	}

	public static class FirstCommandLineRunner implements CommandLineRunner {

		@NacosInjected
		private ConfigService configService;

		@Override
		public void run(String... args) throws Exception {
			if (configService.publishConfig(DATA_ID, Constants.DEFAULT_GROUP, content)) {
				Thread.sleep(200);
				System.out.println("First runner success: " + configService
						.getConfig(DATA_ID, Constants.DEFAULT_GROUP, 5000));
			}
			else {
				System.out.println("First runner error: publish config error");
			}
		}
	}

	public static class SecondCommandLineRunner implements CommandLineRunner {

		@NacosValue("${dept:unknown}")
		private String dept;

		@NacosValue("${group:unknown}")
		private String group;

		@Autowired
		private Foo foo;

		@Override
		public void run(String... args) throws Exception {
			System.out.println("Second runner. dept: " + dept + ", group: " + group);
			System.out.println("Second runner. foo: " + foo);
		}
	}

}
