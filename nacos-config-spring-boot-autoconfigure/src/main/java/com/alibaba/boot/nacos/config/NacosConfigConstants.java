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
package com.alibaba.boot.nacos.config;

import com.alibaba.nacos.spring.context.annotation.config.EnableNacosConfig;

/**
 * Nacos Config Constants
 *
 * @author <a href="mailto:fangjian0423@gmail.com">Jim</a>
 */
public interface NacosConfigConstants {

	String ENDPOINT_PREFIX = "nacos-config";

	String ENABLED = EnableNacosConfig.CONFIG_PREFIX + "enabled";

	String PREFIX = "nacos.config";

	String NACOS_BOOTSTRAP = PREFIX + ".bootstrap.enable";

	String NACOS_LOG_BOOTSTRAP = PREFIX + ".bootstrap.log.enable";

}
