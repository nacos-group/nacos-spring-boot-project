/*
 *
 *  * Licensed to the Apache Software Foundation (ASF) under one or more
 *  * contributor license agreements.  See the NOTICE file distributed with
 *  * this work for additional information regarding copyright ownership.
 *  * The ASF licenses this file to You under the Apache License, Version 2.0
 *  * (the "License"); you may not use this file except in compliance with
 *  * the License.  You may obtain a copy of the License at
 *  *
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package com.alibaba.boot.nacos.sample.controller;

import com.alibaba.nacos.api.annotation.NacosInjected;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
public class AotController {
    @NacosInjected
    private ConfigService configService;

    @NacosInjected
    private NamingService namingService;

    @NacosValue(value = "${flag:false}", autoRefreshed = true)
    private boolean flag;

    @ResponseBody
    @RequestMapping(value = "/config/get", method = GET)
    public String getConfig() throws NacosException {
        return configService.getConfig("example", "DEFAULT_GROUP", 5000);
    }

    @ResponseBody
    @RequestMapping(value = "/naming/get", method = GET)
    public List<Instance> getNaming(@RequestParam("serviceName") String serviceName) throws NacosException {
        return namingService.getAllInstances(serviceName);
    }

    @ResponseBody
    @RequestMapping(value = "/flag/get", method = GET)
    public boolean getFlag() {
        return flag;
    }
}
