/**
 * @author <a href="mailto:liaochunyhm@live.com">liaochuntao</a>
 * @since
 */
package com.alibaba.boot.nacos.config.util;

import java.beans.PropertyEditorSupport;

public class NacosCharSequenceEditor extends PropertyEditorSupport {

    @Override
    public void setValue(Object value) {
        if (value instanceof CharSequence) {
            CharSequence sequence = (CharSequence) value;
            super.setValue(sequence.toString());
        } else {
            super.setValue(value);
        }
    }

    @Override
    public String getAsText() {
        Object value = getValue();
        return String.valueOf(value);
    }
}
