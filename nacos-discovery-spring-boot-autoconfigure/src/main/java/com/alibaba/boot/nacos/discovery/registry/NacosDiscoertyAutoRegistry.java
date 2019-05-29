package com.alibaba.boot.nacos.discovery.registry;

import com.alibaba.boot.nacos.discovery.properties.NacosDiscoveryProperties;
import com.alibaba.nacos.api.annotation.NacosInjected;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.boot.web.server.WebServer;
import org.springframework.context.ApplicationListener;
import org.springframework.util.StringUtils;

/**
 * Auto registry SpringBoot Application to nacos server
 *
 * @author <a href="mailto:liaochunyhm@live.com">liaochuntao</a>
 * @since
 */
public class NacosDiscoertyAutoRegistry implements ApplicationListener<WebServerInitializedEvent> {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public static final String BEAN_NAME = "nacosDiscoertyAutoRegistry";

    @Value("spring.application.name")
    private String applicationName;

    @Autowired
    private NacosDiscoveryProperties discoveryProperties;

    @NacosInjected
    private NamingService namingService;

    @Override
    public void onApplicationEvent(WebServerInitializedEvent event) {
        if (discoveryProperties.isAutoRegistry()) {
            logger.info("[nacos-boot] auto registry application");
            WebServer webServer = event.getWebServer();
            String serviceName = StringUtils.isEmpty(discoveryProperties.getName()) ? applicationName : discoveryProperties.getName();
            Instance instance = buildInstance(webServer);
            instance.setServiceName(serviceName);
            try {
                namingService.registerInstance(serviceName, discoveryProperties.getGroup(), instance);
            } catch (NacosException e) {
                e.printStackTrace();
            }
        }
    }

    private Instance buildInstance(WebServer webServer) {
        NacosDiscoveryProperties.InstanceInfo instanceInfo = discoveryProperties.getInstanceInfo();
        Instance instance = new Instance();
        instance.setIp(instanceInfo.getIp());
        instance.setPort(instanceInfo.getPort() == -1 ? webServer.getPort() : instanceInfo.getPort());
        instance.setMetadata(instanceInfo.getMetadata());
        instance.setWeight(instanceInfo.getWeight());
        instance.setEphemeral(instanceInfo.isEphemeral());
        return instance;
    }
}
