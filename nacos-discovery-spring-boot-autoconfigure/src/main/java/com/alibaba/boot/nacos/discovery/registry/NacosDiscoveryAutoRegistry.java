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

package com.alibaba.boot.nacos.discovery.registry;

import com.alibaba.boot.nacos.discovery.properties.NacosDiscoveryProperties;
import com.alibaba.nacos.api.annotation.NacosInjected;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;

import java.util.Map;

/**
 * Auto registry service
 *
 * @author <a href="mailto:liaochunyhm@live.com">liaochuntao</a>
 * @since 0.3.0
 */
public class NacosDiscoveryAutoRegistry implements ApplicationListener<ContextRefreshedEvent> {

    private static final Logger log = LoggerFactory.getLogger(NacosDiscoveryAutoRegistry.class);

    @NacosInjected
    private NamingService namingService;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        ApplicationContext context = event.getApplicationContext();

        NacosDiscoveryProperties discoveryProperties = context.getBean(NacosDiscoveryProperties.class);

        if (!discoveryProperties.isAutoRegistry()) {
            return;
        }

        Environment environment = context.getEnvironment();
        Integer port = environment.getProperty("server.port", Integer.class);
        port = discoveryProperties.getPort() == -1 ? discoveryProperties.getPort() : port;
        Assert.notNull(port, "If you want to auto registry service, You must declaration server.port");

        Map<String, String> metadata = discoveryProperties.getMetadata();

        if (discoveryProperties.isSecure()) {
            metadata.put("secure", "true");
        }

        String serviceId = discoveryProperties.getName();

        Instance instance = new Instance();
        instance.setIp(discoveryProperties.getIp());
        instance.setPort(port);
        instance.setWeight(discoveryProperties.getWeight());
        instance.setClusterName(discoveryProperties.getClusterName());
        instance.setMetadata(metadata);

        try {
            namingService.registerInstance(serviceId, instance);
            log.info("Registry service : {} into Nacos server", serviceId);
        } catch (NacosException e) {
            log.error("Nacos registry, {} register failed...{},", serviceId, e);
        }

    }

}
