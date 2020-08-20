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

package com.alibaba.boot.nacos.discovery.actuate.endpoint;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.alibaba.boot.nacos.common.PropertiesUtils;
import com.alibaba.boot.nacos.discovery.NacosDiscoveryConstants;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.spring.factory.CacheableEventPublishingNacosServiceFactory;
import com.alibaba.nacos.spring.factory.NacosServiceFactory;
import com.alibaba.nacos.spring.util.NacosUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.context.ApplicationContext;

import static com.alibaba.nacos.spring.util.NacosBeanUtils.DISCOVERY_GLOBAL_NACOS_PROPERTIES_BEAN_NAME;

/**
 * Actuator {@link Endpoint} to expose Nacos Discovery Meta Data.
 *
 * @author <a href="mailto:fangjian0423@gmail.com">Jim</a>
 * @see Endpoint
 */
@Endpoint(id = NacosDiscoveryConstants.ENDPOINT_PREFIX)
public class NacosDiscoveryEndpoint {
    
    @Autowired
    private ApplicationContext applicationContext;
    
    private static final Integer PAGE_SIZE = 100;
    
    /**
     * invoke.
     * @author klw(213539@qq.com)
     * @Date 2020/8/20 12:52
     * @param:
     * @return java.util.Map<java.lang.String,java.lang.Object>
     */
    @ReadOperation
    public Map<String, Object> invoke() {
        Map<String, Object> result = new HashMap<>(8);
        
        result.put("nacosDiscoveryGlobalProperties", PropertiesUtils.extractSafeProperties(
                applicationContext.getBean(DISCOVERY_GLOBAL_NACOS_PROPERTIES_BEAN_NAME, Properties.class)));
        
        NacosServiceFactory nacosServiceFactory = CacheableEventPublishingNacosServiceFactory.getSingleton();
        
        Collection<NamingService> namingServiceList = nacosServiceFactory.getNamingServices();
        List<Map<String, Object>> array = new ArrayList<>(namingServiceList.size());
        for (NamingService namingService : namingServiceList) {
            Map<String, Object> jsonObject = new HashMap<>(2);
            try {
                jsonObject.put("servicesOfServer", namingService.getServicesOfServer(0, PAGE_SIZE));
                jsonObject.put("subscribeServices", namingService.getSubscribeServices());
                array.add(jsonObject);
            } catch (Exception e) {
                jsonObject.put("serverStatus",
                        namingService.getServerStatus() + ": " + NacosUtils.SEPARATOR + e.getMessage());
            }
        }
        
        result.put("namingServersStatus", array);
        return result;
    }
    
}
