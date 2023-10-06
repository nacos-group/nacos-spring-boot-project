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

package com.alibaba.boot.nacos.aot.hint;

import com.alibaba.nacos.api.annotation.NacosInjected;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import org.springframework.aot.generate.GenerationContext;
import org.springframework.beans.factory.aot.BeanRegistrationAotContribution;
import org.springframework.beans.factory.aot.BeanRegistrationAotProcessor;
import org.springframework.beans.factory.aot.BeanRegistrationCode;
import org.springframework.beans.factory.support.RegisteredBean;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * {@link NacosInjected} and {@link NacosValue} AotProcessor
 * The fields annotated with {@link NacosInjected} or {@link NacosValue} must be added to the reflect-config.json
 * @author SuperZ1999
 */
public class NacosAnnotationBeanRegistrationAotProcessor implements BeanRegistrationAotProcessor {
    @Override
    public BeanRegistrationAotContribution processAheadOfTime(RegisteredBean registeredBean) {
        Class<?> beanClass = registeredBean.getBeanClass();
        List<Field> fields = new ArrayList<>();
        ReflectionUtils.doWithFields(beanClass, field -> {
            NacosInjected injectedAnnotation = field.getDeclaredAnnotation(NacosInjected.class);
            NacosValue nacosValueAnnotation = field.getDeclaredAnnotation(NacosValue.class);
            if (injectedAnnotation != null || nacosValueAnnotation != null) {
                fields.add(field);
            }
        });
        if (fields.isEmpty()) {
            return null;
        }
        return new AotContribution(fields);
    }

    private static class AotContribution implements BeanRegistrationAotContribution {
        private final List<Field> fields;

        public AotContribution() {
            this.fields = new ArrayList<>();
        }

        public AotContribution(List<Field> fields) {
            this.fields = fields;
        }

        @Override
        public void applyTo(GenerationContext generationContext, BeanRegistrationCode beanRegistrationCode) {
            for (Field field : fields) {
                generationContext.getRuntimeHints().reflection().registerField(field);
            }
        }
    }
}