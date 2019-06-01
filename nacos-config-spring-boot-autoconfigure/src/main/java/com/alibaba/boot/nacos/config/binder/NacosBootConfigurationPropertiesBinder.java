package com.alibaba.boot.nacos.config.binder;

import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.annotation.NacosConfigurationProperties;
import com.alibaba.nacos.spring.context.properties.config.NacosConfigurationPropertiesBinder;
import com.alibaba.nacos.spring.core.env.NacosPropertySource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationBeanFactoryMetadata;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.ResolvableType;

import java.lang.reflect.Method;

/**
 * Data binding using new binders in springboot
 *
 * @author <a href="mailto:liaochunyhm@live.com">liaochuntao</a>
 */
public class NacosBootConfigurationPropertiesBinder extends NacosConfigurationPropertiesBinder {

    private final Logger logger = LoggerFactory.getLogger(NacosBootConfigurationPropertiesBinder.class);

    private ConfigurableApplicationContext context;
    private ConfigurationBeanFactoryMetadata beanFactoryMetadata;

    public NacosBootConfigurationPropertiesBinder(ConfigurableApplicationContext applicationContext) {
        super(applicationContext);
        this.context = applicationContext;
        this.beanFactoryMetadata = applicationContext.getBean(
                ConfigurationBeanFactoryMetadata.BEAN_NAME,
                ConfigurationBeanFactoryMetadata.class);
    }

    @Override
    protected void doBind(Object bean, String beanName, String dataId, String groupId,
                          NacosConfigurationProperties properties, String content, ConfigService configService) {

        String name = "tmp_" + beanName;

        NacosPropertySource propertySource = new NacosPropertySource(name, content, properties.yaml());
        context.getEnvironment().getPropertySources().addLast(propertySource);
        Binder binder = Binder.get(context.getEnvironment());
        ResolvableType type = getBeanType(bean, beanName);
        Bindable<?> target = Bindable.of(type).withExistingValue(bean);
        binder.bind("", target);
        publishBoundEvent(bean, beanName, dataId, groupId, properties, content, configService);
        publishMetadataEvent(bean, beanName, dataId, groupId, properties);
        context.getEnvironment().getPropertySources().remove(name);
    }

    private ResolvableType getBeanType(Object bean, String beanName) {
        Method factoryMethod = this.beanFactoryMetadata.findFactoryMethod(beanName);
        if (factoryMethod != null) {
            return ResolvableType.forMethodReturnType(factoryMethod);
        }
        return ResolvableType.forClass(bean.getClass());
    }
}
