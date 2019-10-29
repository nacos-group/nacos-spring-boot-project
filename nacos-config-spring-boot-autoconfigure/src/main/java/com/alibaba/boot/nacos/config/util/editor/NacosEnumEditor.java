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

import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyEditor;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:liaochunyhm@live.com">liaochuntao</a>
 * @since 0.2.3
 */
public class NacosEnumEditor implements PropertyEditor {

	private final List<PropertyChangeListener> listeners = new ArrayList<>(8);
	private final Class type;
	private final String[] tags;
	private Object value;

	public NacosEnumEditor(Class var1) {
		Object[] var2 = var1.getEnumConstants();
		if (var2 == null) {
			throw new IllegalArgumentException("Unsupported " + var1);
		}
		else {
			this.type = var1;
			this.tags = new String[var2.length];

			for (int var3 = 0; var3 < var2.length; ++var3) {
				this.tags[var3] = ((Enum) var2[var3]).name();
			}
		}
	}

	@Override
	public Object getValue() {
		return this.value;
	}

	@Override
	public void setValue(Object var1) {
		if (var1 != null && !this.type.isInstance(var1)) {
			throw new IllegalArgumentException("Unsupported value: " + var1);
		}
		else {
			Object var2;
			PropertyChangeListener[] var3;
			synchronized (this.listeners) {
				label45: {
					var2 = this.value;
					this.value = var1;
					if (var1 == null) {
						if (var2 != null) {
							break label45;
						}
					}
					else if (!var1.equals(var2)) {
						break label45;
					}

					return;
				}

				int var5 = this.listeners.size();
				if (var5 == 0) {
					return;
				}

				var3 = (PropertyChangeListener[]) this.listeners
						.toArray(new PropertyChangeListener[var5]);
			}

			PropertyChangeEvent var4 = new PropertyChangeEvent(this, (String) null, var2,
					var1);
			PropertyChangeListener[] var10 = var3;
			int var6 = var3.length;

			for (int var7 = 0; var7 < var6; ++var7) {
				PropertyChangeListener var8 = var10[var7];
				var8.propertyChange(var4);
			}

		}
	}

	@Override
	public String getAsText() {
		return this.value != null ? ((Enum) this.value).name() : null;
	}

	@Override
	public void setAsText(String var1) {
		this.setValue(var1 != null ? Enum.valueOf(this.type, var1.toUpperCase()) : null);
	}

	@Override
	public String[] getTags() {
		return (String[]) this.tags.clone();
	}

	@Override
	public String getJavaInitializationString() {
		String var1 = this.getAsText();
		return var1 != null ? this.type.getName() + '.' + var1 : "null";
	}

	@Override
	public boolean isPaintable() {
		return false;
	}

	@Override
	public void paintValue(Graphics var1, Rectangle var2) {
	}

	@Override
	public boolean supportsCustomEditor() {
		return false;
	}

	@Override
	public Component getCustomEditor() {
		return null;
	}

	@Override
	public void addPropertyChangeListener(PropertyChangeListener var1) {
		synchronized (this.listeners) {
			this.listeners.add(var1);
		}
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener var1) {
		synchronized (this.listeners) {
			this.listeners.remove(var1);
		}
	}
}
