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
package com.alibaba.boot.nacos.config.util.editor;

import java.beans.PropertyEditorSupport;

/**
 * @author <a href="mailto:liaochunyhm@live.com">liaochuntao</a>
 * @since 0.1.3
 */
public class NacosBooleanEditor extends PropertyEditorSupport {

	public NacosBooleanEditor() {
	}

	@Override
	public String getJavaInitializationString() {
		Object var1 = this.getValue();
		return var1 != null ? var1.toString() : "null";
	}

	@Override
	public String getAsText() {
		Object var1 = this.getValue();
		return var1 instanceof Boolean ? this.getValidName((Boolean) var1) : null;
	}

	@Override
	public void setAsText(String var1) throws IllegalArgumentException {
		if (var1 == null) {
			this.setValue((Object) null);
		}
		else if (this.isValidName(true, var1)) {
			this.setValue(Boolean.TRUE);
		}
		else {
			if (!this.isValidName(false, var1)) {
				throw new IllegalArgumentException(var1);
			}

			this.setValue(Boolean.FALSE);
		}

	}

	@Override
	public String[] getTags() {
		return new String[] { this.getValidName(true), this.getValidName(false) };
	}

	private String getValidName(boolean var1) {
		return var1 ? "True" : "False";
	}

	private boolean isValidName(boolean var1, String var2) {
		return this.getValidName(var1).equalsIgnoreCase(var2);
	}
}
