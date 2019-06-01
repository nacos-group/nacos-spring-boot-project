package com.alibaba.boot.nacos.config.autoconfigure;

import com.alibaba.boot.nacos.config.binder.NacosBootConfigurationPropertiesBinder;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;

import static com.alibaba.nacos.spring.util.NacosBeanUtils.registerInfrastructureBeanIfAbsent;

/**
 * @author <a href="mailto:liaochunyhm@live.com">liaochuntao</a>
 */
public class NacosConfigBootBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar, EnvironmentAware,
        BeanFactoryAware {

    private Environment environment;

    private BeanFactory beanFactory;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        registerInfrastructureBeanIfAbsent(registry, NacosBootConfigurationPropertiesBinder.BEAN_NAME, NacosBootConfigurationPropertiesBinder.class);
    }
}
