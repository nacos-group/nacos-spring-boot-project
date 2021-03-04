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

import com.alibaba.nacos.api.config.annotation.NacosValue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author <a href="mailto:liaochunyhm@live.com">liaochuntao</a>
 * @since
 */
@RestController
public class TestController {

	@NacosValue(value = "${people.enable:bbbbb}", autoRefreshed = true)
	private String enable;

	@Value("${people.enable:}")
	private String springEnable;

	@Autowired
	private Apple apple;

	@Autowired
	private TestConfiguration configuration;

	@Scheduled(cron = "0/10 * * * * *")
	public void print() {
		System.out.println(configuration.getCount());
	}

	@RequestMapping()
	@ResponseBody
	public String testGet() {
		return enable + "-" + springEnable;
	}

	@GetMapping("/apple")
	public String getApplr() {
		return apple.toString();
	}

}
