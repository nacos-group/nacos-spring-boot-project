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

package com.alibaba.boot.nacos.util.editor;

import com.alibaba.boot.nacos.config.util.editor.NacosCustomBooleanEditor;
import org.junit.Assert;
import org.junit.Test;

/**
 *  {@link NacosCustomBooleanEditor} Test
 * @ClassName: NacosCustomBooleanEditorTest
 * @Author: SuperZ1999
 * @Date: 2023/9/28
 */
public class NacosCustomBooleanEditorTest {
    @Test
    public void testAllowEmpty() {
        NacosCustomBooleanEditor booleanEditor = new NacosCustomBooleanEditor(true);
        booleanEditor.setValue("");
        Assert.assertEquals(booleanEditor.getAsText(), "false");

        booleanEditor.setValue("true");
        Assert.assertEquals(booleanEditor.getAsText(), "true");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNotAllowEmpty() {
        NacosCustomBooleanEditor booleanEditor = new NacosCustomBooleanEditor(false);
        booleanEditor.setValue("true");
        Assert.assertEquals(booleanEditor.getAsText(), "true");

        booleanEditor.setValue("");
    }

    @Test
    public void testCustomBooleanString() {
        NacosCustomBooleanEditor booleanEditor = new NacosCustomBooleanEditor("TRUE", "FALSE", true);
        booleanEditor.setValue("");
        Assert.assertEquals(booleanEditor.getAsText(), "FALSE");

        booleanEditor.setValue("TRUE");
        Assert.assertEquals(booleanEditor.getAsText(), "TRUE");

        booleanEditor.setValue("false");
        Assert.assertEquals(booleanEditor.getAsText(), "FALSE");
    }
}