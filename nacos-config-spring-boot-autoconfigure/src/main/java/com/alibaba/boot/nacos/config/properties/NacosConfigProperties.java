/*
 * Copyright (C) 2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.alibaba.boot.nacos.config.properties;

import com.alibaba.boot.nacos.config.NacosConfigConstants;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.nacos.api.common.Constants;
import com.alibaba.nacos.spring.context.constants.NacosConstants;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * {@link ConfigurationProperties} for configuring Nacos Config.
 *
 * @author <a href="mailto:fangjian0423@gmail.com">Jim</a>
 * @author <a href="mailto:liaochunyhm@live.com">liaochuntao</a>
 */
@ConfigurationProperties(NacosConfigConstants.PREFIX)
public class NacosConfigProperties {

	private String serverAddr = "127.0.0.1:8848";

    private String contextPath = "";

	private String encode = "";

	private String endpoint = "";

	private String namespace = "";

	private String accessKey = "";

	private String secretKey = "";

	private boolean autoRefresh = false;

	private boolean yaml = false;

	private String clusterName = "";

	private String dataId = "";

	private String group = Constants.DEFAULT_GROUP;

	private String ramRoleName = "";

	private String maxRetry = "";

	private String configLongPollTimeout = "";

	private String configRetryTime = "";

	@JSONField(serialize = false)
	private List<Config> extConfig = new ArrayList<>();

    public String getServerAddr() {
        return serverAddr;
    }

    public void setServerAddr(String serverAddr) {
        Assert.notNull(serverAddr, "nacos config server-addr must not be null");
        this.serverAddr = serverAddr;
    }

    public String getContextPath() {
        return contextPath;
    }

    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }

    public String getEncode() {
        return encode;
    }

    public void setEncode(String encode) {
        this.encode = encode;
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

    public boolean isAutoRefresh() {
        return autoRefresh;
    }

    public void setAutoRefresh(boolean autoRefresh) {
        this.autoRefresh = autoRefresh;
    }

    public boolean isYaml() {
        return yaml;
    }

    public void setYaml(boolean yaml) {
        this.yaml = yaml;
    }

    public String getDataId() {
        return dataId;
    }

    public void setDataId(String dataId) {
        this.dataId = dataId;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public String getRamRoleName() {
        return ramRoleName;
    }

    public void setRamRoleName(String ramRoleName) {
        this.ramRoleName = ramRoleName;
    }

    public String getMaxRetry() {
        return maxRetry;
    }

    public void setMaxRetry(String maxRetry) {
        this.maxRetry = maxRetry;
    }

    public String getConfigLongPollTimeout() {
        return configLongPollTimeout;
    }

    public void setConfigLongPollTimeout(String configLongPollTimeout) {
        this.configLongPollTimeout = configLongPollTimeout;
    }

    public String getConfigRetryTime() {
        return configRetryTime;
    }

    public void setConfigRetryTime(String configRetryTime) {
        this.configRetryTime = configRetryTime;
    }

    public List<Config> getExtConfig() {
        return extConfig;
    }

    public void setExtConfig(List<Config> extConfig) {
        this.extConfig = extConfig;
    }

    public static class Config {

        private String serverAddr;

        private String endpoint = "";

        private String namespace = "";

        private String clusterName = "";

        private String accessKey = "";

        private String secretKey = "";

        private String dataId = "";

        private String group = Constants.DEFAULT_GROUP;

        private String ramRoleName = "";

        private String maxRetry = "";

        private String configLongPollTimeout = "";

        private String configRetryTime = "";

        private boolean autoRefresh = false;

        private boolean yaml = false;

        public String getServerAddr() {
            return serverAddr;
        }

        public void setServerAddr(String serverAddr) {
            this.serverAddr = serverAddr;
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

        public String getDataId() {
            return dataId;
        }

        public void setDataId(String dataId) {
            this.dataId = dataId;
        }

        public String getGroup() {
            return group;
        }

        public void setGroup(String group) {
            this.group = group;
        }

        public String getMaxRetry() {
            return maxRetry;
        }

        public void setMaxRetry(String maxRetry) {
            this.maxRetry = maxRetry;
        }

        public String getConfigLongPollTimeout() {
            return configLongPollTimeout;
        }

        public void setConfigLongPollTimeout(String configLongPollTimeout) {
            this.configLongPollTimeout = configLongPollTimeout;
        }

        public String getConfigRetryTime() {
            return configRetryTime;
        }

        public void setConfigRetryTime(String configRetryTime) {
            this.configRetryTime = configRetryTime;
        }

        public boolean isAutoRefresh() {
            return autoRefresh;
        }

        public void setAutoRefresh(boolean autoRefresh) {
            this.autoRefresh = autoRefresh;
        }

        public boolean isYaml() {
            return yaml;
        }

        public void setYaml(boolean yaml) {
            yaml = yaml;
        }

        public String getClusterName() {
            return clusterName;
        }

        public void setClusterName(String clusterName) {
            this.clusterName = clusterName;
        }

        public String getRamRoleName() {
            return ramRoleName;
        }

        public void setRamRoleName(String ramRoleName) {
            this.ramRoleName = ramRoleName;
        }

        @Override
        public String toString() {
            return "Config{" +
                    "serverAddr='" + serverAddr + '\'' +
                    ", endpoint='" + endpoint + '\'' +
                    ", namespace='" + namespace + '\'' +
                    ", clusterName='" + clusterName + '\'' +
                    ", accessKey='" + accessKey + '\'' +
                    ", secretKey='" + secretKey + '\'' +
                    ", dataId='" + dataId + '\'' +
                    ", group='" + group + '\'' +
                    ", ramRoleName='" + ramRoleName + '\'' +
                    ", maxRetry='" + maxRetry + '\'' +
                    ", configLongPollTimeout='" + configLongPollTimeout + '\'' +
                    ", configRetryTime='" + configRetryTime + '\'' +
                    ", autoRefresh=" + autoRefresh +
                    ", yaml=" + yaml +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "NacosConfigProperties{" +
                "serverAddr='" + serverAddr + '\'' +
                ", contextPath='" + contextPath + '\'' +
                ", encode='" + encode + '\'' +
                ", endpoint='" + endpoint + '\'' +
                ", namespace='" + namespace + '\'' +
                ", accessKey='" + accessKey + '\'' +
                ", secretKey='" + secretKey + '\'' +
                ", autoRefresh=" + autoRefresh +
                ", yaml=" + yaml +
                ", clusterName='" + clusterName + '\'' +
                ", dataId='" + dataId + '\'' +
                ", group='" + group + '\'' +
                ", ramRoleName='" + ramRoleName + '\'' +
                ", maxRetry='" + maxRetry + '\'' +
                ", configLongPollTimeout='" + configLongPollTimeout + '\'' +
                ", configRetryTime='" + configRetryTime + '\'' +
                ", extConfig=" + extConfig +
                '}';
    }
}
