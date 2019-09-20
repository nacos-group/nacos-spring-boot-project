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
import com.alibaba.boot.nacos.config.util.editor.NacosEnumEditor;
import com.alibaba.nacos.api.config.ConfigType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.propertyeditors.CustomCollectionEditor;
import org.springframework.core.env.ConfigurableEnvironment;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:liaochunyhm@live.com">liaochuntao</a>
 * @since
 */
public class NacosConfigPropertiesUtils {

    private static final String PROPERTIES_PREFIX = "nacos";

    private static final Logger logger = LoggerFactory.getLogger(NacosConfigPropertiesUtils.class);

    private static final Set<String> OBJ_FILED_NAME = new HashSet<>();

    static {
        Class<NacosConfigProperties> cls = NacosConfigProperties.class;
        Field[] fields = cls.getDeclaredFields();
        for (Field field : fields) {
            OBJ_FILED_NAME.add(field.getName());
        }
    }

    public static NacosConfigProperties buildNacosConfigProperties(ConfigurableEnvironment environment) {
        BeanWrapper wrapper = new BeanWrapperImpl(new NacosConfigProperties());
        wrapper.setAutoGrowNestedPaths(true);
        wrapper.setExtractOldValueForEditor(true);
        wrapper.registerCustomEditor(ConfigType.class, new NacosEnumEditor(ConfigType.class));
        wrapper.registerCustomEditor(Collection.class, new CustomCollectionEditor(ArrayList.class));

        AttributeExtractTask task = new AttributeExtractTask(PROPERTIES_PREFIX, environment);

        try {
            // Filter object property values
            Map<String, String> properties = new HashMap<>(8);
            for (Map.Entry<String, String> entry : task.call().entrySet()) {
                for (String fieldName : OBJ_FILED_NAME) {
                    if (entry.getKey().matches(fieldName)) {
                        properties.put(entry.getKey(), entry.getValue());
                        break;
                    }
                }
            }
            wrapper.setPropertyValues(dataSource(properties));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        NacosConfigProperties nacosConfigProperties = (NacosConfigProperties) wrapper.getWrappedInstance();
        logger.debug("nacosConfigProperties : {}", nacosConfigProperties);
        return nacosConfigProperties;
    }

    private static Map<String, String> dataSource(Map<String, String> source) {
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
