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
package com.alibaba.boot.nacos.discovery.properties;

import com.alibaba.boot.nacos.discovery.NacosDiscoveryConstants;
import com.alibaba.nacos.api.naming.PreservedMetadataKeys;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;

/**
 * {@link ConfigurationProperties} for configuring Nacos Discovery.
 *
 * @author <a href="mailto:fangjian0423@gmail.com">Jim</a>
 * @author <a href="mailto:liaochunyhm@live.com">liaochuntao</a>
 */
@ConfigurationProperties(NacosDiscoveryConstants.PREFIX)
public class NacosDiscoveryProperties {

    private String name = "application";

    private String contextPath;

    /**
     * nacos discovery server address
     */
    private String serverAddr;

    /**
     * the domain name of a service, through which the server address can be dynamically
     * obtained.
     */
    private String endpoint;

    /**
     * namespace, separation registry of different environments.
     */
    private String namespace;

    /**
     * weight for service instance, the larger the value, the larger the weight.
     */
    private float weight = 1;

    /**
     * cluster name for nacos server.
     */
    private String clusterName = "DEFAULT";

    /**
     * extra metadata to register.
     */
    private Map<String, String> metadata = new HashMap<>();

    /**
     * The ip address your want to register for your service instance, needn't to set it
     * if the auto detect ip works well.
     */
    private String ip;

    /**
     * which network interface's ip you want to register
     */
    private String networkInterface = "";

    /**
     * The port your want to register for your service instance, needn't to set it if the
     * auto detect port works well
     */
    private int port = -1;

    /**
     * whether your service is a https service
     */
    private boolean secure = false;

    /**
     * access key for namespace.
     */
    private String accessKey;

    /**
     * secret key for namespace.
     */
    private String secretKey;

    /**
     * auto registry service
     */
    private boolean autoRegistry = true;

    @PostConstruct
    private void init() throws SocketException {

        metadata.put(PreservedMetadataKeys.REGISTER_SOURCE, "SPRING_BOOT");

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getServerAddr() {
        return serverAddr;
    }

    public void setServerAddr(String serverAddr) {
        Assert.notNull(serverAddr, "nacos discovery server-addr must not be null");
        this.serverAddr = serverAddr;
    }

    public String getContextPath() {
        return contextPath;
    }

    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }

    public String getNetworkInterface() {
        return networkInterface;
    }

    public void setNetworkInterface(String networkInterface) {
        this.networkInterface = networkInterface;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public boolean isSecure() {
        return secure;
    }

    public void setSecure(boolean secure) {
        this.secure = secure;
    }

    public boolean isAutoRegistry() {
        return autoRegistry;
    }

    public void setAutoRegistry(boolean autoRegistry) {
        this.autoRegistry = autoRegistry;
    }
}
