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

package com.alibaba.boot.nacos.logging;

import com.alibaba.boot.nacos.config.logging.NacosLoggingListener;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ResolvableType;

import java.lang.reflect.Type;

/**
 * {@link NacosLoggingListener} Test
 * @ClassName: AttributeExtractTaskTest
 * @Author: ChenHao26
 * @Date: 2022/8/12 15:08
 */
public class NacosLoggingListenerTest {
    @Autowired
    NacosLoggingListener nacosLoggingListener;
    
    @Before
    public void setup() {
        nacosLoggingListener = new NacosLoggingListener();
    }
    
    @Test
    public void supportsEventType() {
        boolean result = nacosLoggingListener.supportsEventType(ResolvableType.forType(new Type() {
            @Override
            public String getTypeName() {
                return Type.super.getTypeName();
            }
        }));
        Assert.assertEquals(result, false);
    }
    
    @Test
    public void getOrder() {
        int order = nacosLoggingListener.getOrder();
        Assert.assertNotNull(order);
    
    }
}
