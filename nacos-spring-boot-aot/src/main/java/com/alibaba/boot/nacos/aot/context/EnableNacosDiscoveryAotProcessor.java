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
import com.alibaba.nacos.spring.context.annotation.discovery.EnableNacosDiscovery;
import com.alibaba.nacos.spring.context.annotation.discovery.NacosDiscoveryBeanDefinitionRegistrar;
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
 * {@link EnableNacosDiscovery} AotProcessor
 * Except for the operation of registering BeanDefinition, all other operations in {@link NacosDiscoveryBeanDefinitionRegistrar} must be done here
 * because spring will not call {@link NacosDiscoveryBeanDefinitionRegistrar#registerBeanDefinitions} in AOT.
 * @author SuperZ1999
 */
public class EnableNacosDiscoveryAotProcessor implements BeanDefinitionRegistryPostProcessor, EnvironmentAware, BeanFactoryAware {
    private Environment environment;

    private BeanFactory beanFactory;

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        if (!AotDetector.useGeneratedArtifacts()) {
            return;
        }
        DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) this.beanFactory;
        Map<String, Object> beansWithAnnotation = beanFactory.getBeansWithAnnotation(EnableNacosDiscovery.class);
        Object[] beans = beansWithAnnotation.values().toArray();
        if (beans.length != 0) {
            // only handle the first one
            Class<?> aClass = beans[0].getClass();
            if (aClass.getAnnotation(EnableNacosDiscovery.class) == null) {
                // cglib proxy object
                aClass = aClass.getSuperclass();
            }
            AnnotationMetadata annotationMetadata = AnnotationMetadata.introspect(aClass);
            AnnotationAttributes attributes = AnnotationAttributes
                    .fromMap(annotationMetadata
                            .getAnnotationAttributes(EnableNacosDiscovery.class.getName()));

            // Register Global Nacos Properties Bean
            registerGlobalNacosProperties(attributes, registry, environment,
                    DISCOVERY_GLOBAL_NACOS_PROPERTIES_BEAN_NAME);
            registerGlobalNacosProperties(attributes, registry, environment,
                    MAINTAIN_GLOBAL_NACOS_PROPERTIES_BEAN_NAME);
        }
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