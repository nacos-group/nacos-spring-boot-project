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
public class NacosStringEditor extends PropertyEditorSupport {

	public NacosStringEditor() {
	}

	@Override
	public String getJavaInitializationString() {
		Object var1 = this.getValue();
		if (var1 == null) {
			return "null";
		}
		else {
			String var2 = var1.toString();
			int var3 = var2.length();
			StringBuilder var4 = new StringBuilder(var3 + 2);
			var4.append('"');

			for (int var5 = 0; var5 < var3; ++var5) {
				char var6 = var2.charAt(var5);
				String var7;
				int var8;
				switch (var6) {
				case '\b':
					var4.append("\\b");
					continue;
				case '\t':
					var4.append("\\t");
					continue;
				case '\n':
					var4.append("\\n");
					continue;
				case '\f':
					var4.append("\\f");
					continue;
				case '\r':
					var4.append("\\r");
					continue;
				case '"':
					var4.append("\\\"");
					continue;
				case '\\':
					var4.append("\\\\");
					continue;
				default:
					if (var6 >= ' ' && var6 <= '~') {
						var4.append(var6);
						continue;
					}

					var4.append("\\u");
					var7 = Integer.toHexString(var6);
					var8 = var7.length();
				}

				while (var8 < 4) {
					var4.append('0');
					++var8;
				}

				var4.append(var7);
			}

			var4.append('"');
			return var4.toString();
		}
	}

	@Override
	public void setAsText(String var1) {
		this.setValue(var1);
	}
}
