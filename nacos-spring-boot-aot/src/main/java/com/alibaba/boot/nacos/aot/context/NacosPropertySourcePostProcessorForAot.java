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
import com.alibaba.nacos.spring.context.annotation.config.NacosPropertySource;
import com.alibaba.nacos.spring.core.env.AbstractNacosPropertySourceBuilder;
import com.alibaba.nacos.spring.core.env.NacosPropertySourcePostProcessor;
import com.alibaba.spring.util.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.AnnotatedGenericBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

import java.util.ArrayList;
import java.util.Map;

import static com.alibaba.nacos.spring.util.NacosBeanUtils.getConfigServiceBeanBuilder;

public class NacosPropertySourcePostProcessorForAot extends NacosPropertySourcePostProcessor {
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory)
            throws BeansException {
        String[] abstractNacosPropertySourceBuilderBeanNames = BeanUtils
                .getBeanNames(beanFactory, AbstractNacosPropertySourceBuilder.class);

        this.nacosPropertySourceBuilders = new ArrayList<AbstractNacosPropertySourceBuilder>(
                abstractNacosPropertySourceBuilderBeanNames.length);

        for (String beanName : abstractNacosPropertySourceBuilderBeanNames) {
            this.nacosPropertySourceBuilders.add(beanFactory.getBean(beanName,
                    AbstractNacosPropertySourceBuilder.class));
        }

        NacosPropertySourcePostProcessor.beanFactory = beanFactory;
        this.configServiceBeanBuilder = getConfigServiceBeanBuilder(beanFactory);

        if (AotDetector.useGeneratedArtifacts()) {
            // the type of all BeanDefinitions is RootBeanDefinition in AOT, but what we need is AnnotatedGenericBeanDefinition.
            Map<String, Object> beansWithAnnotation = beanFactory.getBeansWithAnnotation(NacosPropertySource.class);
            for (Map.Entry<String, Object> entry : beansWithAnnotation.entrySet()) {
                processPropertySourceForAot(entry.getKey(), entry.getValue());
            }
        }

        String[] beanNames = beanFactory.getBeanDefinitionNames();

        for (String beanName : beanNames) {
            processPropertySource(beanName, beanFactory);
        }
    }

    private void processPropertySourceForAot(String beanName, Object bean) {
        if (processedBeanNames.contains(beanName)) {
            return;
        }

        BeanDefinition beanDefinition = null;
        Class<?> aClass = bean.getClass();
        NacosPropertySource[] annotations = aClass.getSuperclass().getAnnotationsByType(NacosPropertySource.class);
        if (annotations.length != 0) {
            beanDefinition = new AnnotatedGenericBeanDefinition(aClass.getSuperclass());
        }
        annotations = aClass.getAnnotationsByType(NacosPropertySource.class);
        if (annotations.length != 0) {
            beanDefinition = new AnnotatedGenericBeanDefinition(aClass);
        }

        doProcessPropertySource(beanName, beanDefinition);

        processedBeanNames.add(beanName);
    }
}
