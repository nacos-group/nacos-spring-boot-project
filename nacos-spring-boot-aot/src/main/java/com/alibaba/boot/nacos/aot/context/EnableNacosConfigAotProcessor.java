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

package com.alibaba.boot.nacos.aot.context;

import com.alibaba.boot.nacos.aot.util.AotDetector;
import com.alibaba.nacos.spring.context.annotation.config.EnableNacosConfig;
import com.alibaba.nacos.spring.context.annotation.config.NacosConfigBeanDefinitionRegistrar;
import com.alibaba.nacos.spring.core.env.NacosPropertySourcePostProcessor;
import com.alibaba.nacos.spring.util.NacosBeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Map;

import static com.alibaba.nacos.spring.util.NacosBeanUtils.*;

/**
 * {@link EnableNacosConfig} AotProcessor
 * Except for the operation of registering BeanDefinition, all other operations in {@link NacosConfigBeanDefinitionRegistrar} must be done here
 * because spring will not call {@link NacosConfigBeanDefinitionRegistrar#registerBeanDefinitions} in AOT.
 * @author SuperZ1999
 */
public class EnableNacosConfigAotProcessor implements BeanDefinitionRegistryPostProcessor, EnvironmentAware, BeanFactoryAware {
    private Environment environment;

    private BeanFactory beanFactory;

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        if (!AotDetector.useGeneratedArtifacts()) {
            return;
        }
        DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) this.beanFactory;
        Map<String, Object> beansWithAnnotation = beanFactory.getBeansWithAnnotation(EnableNacosConfig.class);
        Object[] beans = beansWithAnnotation.values().toArray();
        if (beans.length != 0) {
            // only handle the first one
            Class<?> aClass = beans[0].getClass();
            if (aClass.getAnnotation(EnableNacosConfig.class) == null) {
                // cglib proxy object
                aClass = aClass.getSuperclass();
            }
            AnnotationMetadata annotationMetadata = AnnotationMetadata.introspect(aClass);
            AnnotationAttributes attributes = AnnotationAttributes
                    .fromMap(annotationMetadata
                            .getAnnotationAttributes(EnableNacosConfig.class.getName()));

            // Register Global Nacos Properties Bean
            registerGlobalNacosProperties(attributes, registry, environment,
                    CONFIG_GLOBAL_NACOS_PROPERTIES_BEAN_NAME);
        }

        registerNacosConfigListenerExecutor(registry, environment);
        // replace NacosPropertySourcePostProcessor with NacosPropertySourcePostProcessorForAot
        if (registry.containsBeanDefinition(NacosPropertySourcePostProcessor.BEAN_NAME)) {
            registry.removeBeanDefinition(NacosPropertySourcePostProcessor.BEAN_NAME);
        }
        NacosBeanUtils.registerInfrastructureBeanIfAbsent(registry, NacosPropertySourcePostProcessorForAot.BEAN_NAME,
                NacosPropertySourcePostProcessorForAot.class);
        // Invoke NacosPropertySourcePostProcessor immediately
        // in order to enhance the precedence of @NacosPropertySource process
        invokeNacosPropertySourcePostProcessor(this.beanFactory);
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }
}