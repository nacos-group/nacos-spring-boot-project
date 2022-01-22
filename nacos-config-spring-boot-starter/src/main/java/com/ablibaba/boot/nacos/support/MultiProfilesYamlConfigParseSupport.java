package com.ablibaba.boot.nacos.support;

import com.alibaba.nacos.spring.util.parse.DefaultYamlConfigParse;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author yanglulu
 * @date 2022/1/20
 */
public class MultiProfilesYamlConfigParseSupport extends DefaultYamlConfigParse implements EnvironmentPostProcessor {

    private static String[] profileArray = {};

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        String[] profiles = environment.getActiveProfiles();
        if (profileArray.length == 0) {
            profileArray = profiles;
        }
    }

    @Override
    public Map<String, Object> parse(String configText) {
        final AtomicReference<Map<String, Object>> result = new AtomicReference<>();
        process(map -> {
            if (result.get() == null) {
                result.set(map);
            } else {
                for (String profile : profileArray) {
                    if (profile.equals(map.get("spring.profiles"))) {
                        result.get().putAll(map);
                    }
                }
            }
        }, createYaml(), configText);
        return result.get();
    }

}
