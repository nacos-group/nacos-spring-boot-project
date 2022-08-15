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

package com.alibaba.boot.nacos.util.editor;

import com.alibaba.boot.nacos.config.util.editor.NacosCharSequenceEditor;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @ClassName: NacosCharSequenceEditorTest
 * @Author: ChenHao26
 * @Date: 2022/8/12 17:00
 */
public class NacosCharSequenceEditorTest {
    
    public NacosCharSequenceEditor nacosCharSequenceEditor;
    
    @Before
    public void setup() {
        nacosCharSequenceEditor = new NacosCharSequenceEditor();
        
    }
    @Test
    public void setValue() {
        nacosCharSequenceEditor.setValue("nacosTest");
        String asText = nacosCharSequenceEditor.getAsText();
        Assert.assertEquals(asText,"nacosTest");
    }
    
    @Test
    public void getAsText() {
        String str = nacosCharSequenceEditor.getAsText();
        Assert.assertEquals(str, "null");
    }
}
