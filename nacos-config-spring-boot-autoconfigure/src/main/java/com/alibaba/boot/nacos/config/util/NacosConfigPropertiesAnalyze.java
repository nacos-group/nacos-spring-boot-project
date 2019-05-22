/**
 * @author <a href="mailto:liaochunyhm@live.com">liaochuntao</a>
 * @since
 */
package com.alibaba.boot.nacos.config.util;

import com.alibaba.boot.nacos.config.NacosConfigConstants;
import com.alibaba.boot.nacos.config.properties.NacosConfigProperties;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;
import org.springframework.beans.propertyeditors.CustomCollectionEditor;
import org.springframework.boot.env.OriginTrackedMapPropertySource;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author liaochuntao
 */
public class NacosConfigPropertiesAnalyze {

    public static NacosConfigProperties buildNacosConfigProperties(ConfigurableEnvironment environment) {
        BeanWrapper wrapper = new BeanWrapperImpl(new NacosConfigProperties());
        wrapper.setAutoGrowNestedPaths(true);
        wrapper.setExtractOldValueForEditor(true);
        wrapper.registerCustomEditor(String.class, new NacosCharSequenceEditor());
        wrapper.registerCustomEditor(Collection.class, new CustomCollectionEditor(ArrayList.class));
        wrapper.registerCustomEditor(boolean.class, new NacosCustomBooleanEditor(true));
        PropertySource target = findApplicationConfig(environment);
        wrapper.setPropertyValues(dataSource((Map<String, String>) target.getSource()));
        return (NacosConfigProperties) wrapper.getWrappedInstance();
    }

    public static PropertySource<Map<String, String>> findApplicationConfig(ConfigurableEnvironment environment) {
        PropertySource<Map<String, String>> target = null;
        MutablePropertySources mutablePropertySources = environment.getPropertySources();
        for (PropertySource tmp : mutablePropertySources) {
            if (tmp instanceof OriginTrackedMapPropertySource) {
                target = tmp;
            }
        }
        return target;
    }

    private static Map<String, String> dataSource(Map<String, String> source) {
        source.remove(NacosConfigConstants.NACOS_BOOTSTRAP);
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
