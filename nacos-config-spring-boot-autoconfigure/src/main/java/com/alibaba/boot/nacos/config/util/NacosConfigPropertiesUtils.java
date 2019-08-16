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
package com.alibaba.boot.nacos.config.util;

import com.alibaba.boot.nacos.config.NacosConfigConstants;
import com.alibaba.boot.nacos.config.properties.NacosConfigProperties;
import com.alibaba.boot.nacos.config.util.editor.NacosCharSequenceEditor;
import com.alibaba.boot.nacos.config.util.editor.NacosCustomBooleanEditor;
import com.alibaba.boot.nacos.config.util.editor.NacosEnumEditor;
import com.alibaba.nacos.api.config.ConfigType;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.propertyeditors.CustomCollectionEditor;
import org.springframework.boot.env.OriginTrackedMapPropertySource;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:liaochunyhm@live.com">liaochuntao</a>
 * @since
 */
public class NacosConfigPropertiesUtils {

    private static final String PROPERTIES_PREFIX = "nacos";

    private static final Logger logger = LoggerFactory.getLogger(NacosConfigPropertiesUtils.class);

    public static NacosConfigProperties buildNacosConfigProperties(ConfigurableEnvironment environment) {
        BeanWrapper wrapper = new BeanWrapperImpl(new NacosConfigProperties());
        wrapper.setAutoGrowNestedPaths(true);
        wrapper.setExtractOldValueForEditor(true);
        wrapper.registerCustomEditor(ConfigType.class, new NacosEnumEditor(ConfigType.class));
        wrapper.registerCustomEditor(Collection.class, new CustomCollectionEditor(ArrayList.class));

        AttributeExtractTask task = new AttributeExtractTask(PROPERTIES_PREFIX, environment);

        try {
            wrapper.setPropertyValues(dataSource(task.call()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        NacosConfigProperties nacosConfigProperties = (NacosConfigProperties) wrapper.getWrappedInstance();
        logger.debug("nacosConfigProperties : {}", nacosConfigProperties);
        return nacosConfigProperties;
    }

    private static Map<String, String> findApplicationConfig(ConfigurableEnvironment environment) {
        Map<String, String> result = new HashMap<>(8);

        // find order follow spring.profiles.active=dev,prof => find first is prod, then dev
        List<PropertySource> defer = new LinkedList<>();
        MutablePropertySources mutablePropertySources = environment.getPropertySources();
        for (PropertySource tmp : mutablePropertySources) {
            // Spring puts the information of the application.properties file into class{OriginTrackedMapPropertySource}.
            if (tmp instanceof OriginTrackedMapPropertySource) {
                defer.add(tmp);
            }
        }

        Collections.reverse(defer);

        for (PropertySource propertySource : defer) {
            result.putAll((Map<String, String>) propertySource.getSource());
        }

        return result;
    }

    private static Map<String, String> dataSource(Map<String, String> source) {
        source.remove(NacosConfigConstants.NACOS_BOOTSTRAP);
        source.remove(NacosConfigConstants.NACOS_LOG_BOOTSTRAP);
        String prefix = NacosConfigConstants.PREFIX + ".";
        HashMap<String, String> targetConfigInfo = new HashMap<>(source.size());
        for (Map.Entry<String, String> entry : source.entrySet()) {
            if (entry.getKey().startsWith(prefix)) {
                String key = entry.getKey().replace(prefix, "");
                if (key.contains("-")) {
                    String[] subs = key.split("-");
                    key = buildJavaField(subs);
                }
                targetConfigInfo.put(key, entry.getValue());
            }
        }
        return targetConfigInfo;
    }

    private static String buildJavaField(String[] subs) {
        StringBuilder sb = new StringBuilder();
        sb.append(subs[0]);
        for (int i = 1; i < subs.length; i ++) {
            char[] chars = subs[i].toCharArray();
            chars[0] = Character.toUpperCase(chars[0]);
            sb.append(chars);
        }
        return sb.toString();
    }

}
