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
import com.alibaba.boot.nacos.config.util.editor.NacosBooleanEditor;
import com.alibaba.boot.nacos.config.util.editor.NacosEnumEditor;
import com.alibaba.boot.nacos.config.util.editor.NacosStringEditor;
import com.alibaba.nacos.api.config.ConfigType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.propertyeditors.CustomCollectionEditor;
import org.springframework.boot.env.EnumerableCompositePropertySource;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:liaochunyhm@live.com">liaochuntao</a>
 * @since
 */
public class NacosConfigPropertiesUtils {

    private static final Logger logger = LoggerFactory.getLogger(NacosConfigPropertiesUtils.class);

    public static NacosConfigProperties buildNacosConfigProperties(ConfigurableEnvironment environment) {
        BeanWrapper wrapper = new BeanWrapperImpl(new NacosConfigProperties());
        wrapper.setAutoGrowNestedPaths(true);
        wrapper.setExtractOldValueForEditor(true);
        wrapper.registerCustomEditor(String.class, new NacosStringEditor());
        wrapper.registerCustomEditor(boolean.class, new NacosBooleanEditor());
        wrapper.registerCustomEditor(ConfigType.class, new NacosEnumEditor(ConfigType.class));
        wrapper.registerCustomEditor(Collection.class, new CustomCollectionEditor(ArrayList.class));
        PropertySource target = findApplicationConfig(environment);
        wrapper.setPropertyValues(dataSource((Map<String, Object>) target.getSource()));
        NacosConfigProperties nacosConfigProperties = (NacosConfigProperties) wrapper.getWrappedInstance();
        logger.info("nacosConfigProperties : {}", nacosConfigProperties);
        return nacosConfigProperties;
    }

    private static PropertySource<Map<String, String>> findApplicationConfig(ConfigurableEnvironment environment) {
        PropertySource<Map<String, String>> target = null;
        MutablePropertySources mutablePropertySources = environment.getPropertySources();
        for (PropertySource tmp : mutablePropertySources) {
            // Spring puts the information of the application.properties file into applicationConfigurationProperties.
            if ("applicationConfigurationProperties".equals(tmp.getName())) {
                ArrayList<Object> list = (ArrayList) tmp.getSource();
                for (Object obj : list) {
                    if (obj instanceof EnumerableCompositePropertySource) {
                        EnumerableCompositePropertySource propertySource = (EnumerableCompositePropertySource) obj;
                        target = (PropertySource<Map<String, String>>) propertySource.getSource().iterator().next();
                        break;
                    }
                }
            }
            if (target != null) {
                break;
            }
        }
        return target;
    }

    private static Map<String, Object> dataSource(Map<String, Object> source) {
        source.remove(NacosConfigConstants.NACOS_BOOTSTRAP);
        String prefix = NacosConfigConstants.PREFIX + ".";
        HashMap<String, Object> targetConfigInfo = new HashMap<>(source.size());
        for (Map.Entry<String, Object> entry : source.entrySet()) {
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
