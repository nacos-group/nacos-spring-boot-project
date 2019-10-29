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

import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

/**
 * @author <a href="mailto:liaochunyhm@live.com">liaochuntao</a>
 * @since 0.2.3
 */
public class NacosCustomBooleanEditor extends PropertyEditorSupport {

	public static final String VALUE_TRUE = "true";
	public static final String VALUE_FALSE = "false";

	public static final String VALUE_ON = "on";
	public static final String VALUE_OFF = "off";

	public static final String VALUE_YES = "yes";
	public static final String VALUE_NO = "no";

	public static final String VALUE_1 = "1";
	public static final String VALUE_0 = "0";

	@Nullable
	private final String trueString;

	@Nullable
	private final String falseString;

	private final boolean allowEmpty;

	public NacosCustomBooleanEditor(boolean allowEmpty) {
		this(null, null, allowEmpty);
	}

	public NacosCustomBooleanEditor(@Nullable String trueString,
			@Nullable String falseString, boolean allowEmpty) {
		this.trueString = trueString;
		this.falseString = falseString;
		this.allowEmpty = allowEmpty;
	}

	@Override
	public void setValue(Object value) {
		super.setValue(convert(String.valueOf(value)));
	}

	public Object convert(String text) throws IllegalArgumentException {
		String input = (text != null ? text.trim() : null);
		if (this.allowEmpty && !StringUtils.hasLength(input)) {
			// Treat empty String as null value.
			return null;
		}
		else if (this.trueString != null && this.trueString.equalsIgnoreCase(input)) {
			return Boolean.TRUE;
		}
		else if (this.falseString != null && this.falseString.equalsIgnoreCase(input)) {
			return Boolean.FALSE;
		}
		else if (this.trueString == null
				&& (VALUE_TRUE.equalsIgnoreCase(input) || VALUE_ON.equalsIgnoreCase(input)
						|| VALUE_YES.equalsIgnoreCase(input) || VALUE_1.equals(input))) {
			return Boolean.TRUE;
		}
		else if (this.falseString == null && (VALUE_FALSE.equalsIgnoreCase(input)
				|| VALUE_OFF.equalsIgnoreCase(input) || VALUE_NO.equalsIgnoreCase(input)
				|| VALUE_0.equals(input))) {
			return Boolean.FALSE;
		}
		else {
			throw new IllegalArgumentException("Invalid boolean value [" + text + "]");
		}
	}

	@Override
	public String getAsText() {
		String t = String.valueOf(getValue());
		if (Boolean.TRUE.equals(Boolean.valueOf(t))) {
			return (this.trueString != null ? this.trueString : VALUE_TRUE);
		}
		else if (Boolean.FALSE.equals(Boolean.valueOf(t))) {
			return (this.falseString != null ? this.falseString : VALUE_FALSE);
		}
		else {
			return "";
		}
	}

}
