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

package org.apache.servicecomb.serviceregistry.config;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.servicecomb.deployment.Deployment;
import org.apache.servicecomb.deployment.DeploymentProvider;
import org.apache.servicecomb.foundation.common.net.IpPort;
import org.apache.servicecomb.foundation.common.net.NetUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.netflix.config.DynamicBooleanProperty;
import com.netflix.config.DynamicIntProperty;
import com.netflix.config.DynamicPropertyFactory;
import com.netflix.config.DynamicStringProperty;

import io.vertx.core.http.HttpVersion;

/**
 * Created by   on 2016/12/23.
 */
public final class ServiceRegistryConfig {
  private static final Logger LOGGER = LoggerFactory.getLogger(ServiceRegistryConfig.class);

  public static final ServiceRegistryConfig INSTANCE = new ServiceRegistryConfig();

  private static final int DEFAULT_TIMEOUT_IN_MS = 30000;

  private static final int DEFAULT_TIMEOUT_IN_SECONDS = 30;

  private static final int DEFAULT_REQUEST_TIMEOUT_IN_MS = 30000;

  private static final int DEFAULT_REQUEST_HEARTBEAT_TIMEOUT_IN_MS = 3000;

  private static final int DEFAULT_CHECK_INTERVAL_IN_S = 30;

  private static final int DEFAULT_CHECK_TIMES = 3;

  public static final String AUTH_ENABLED = "servicecomb.auth.enabled";

  public static final String TENANT_ACCESS_KEY = "servicecomb.auth.accessKey";

  public static final String TENANT_SECRET_KEY = "servicecomb.auth.secretKey";

  public static final String REGISTRY_API_VERSION = "servicecomb.service.registry.api.version";

  public static final String TENANT_NAME = "servicecomb.config.client.tenantName";

  public static final String DOMAIN_NAME = "servicecomb.config.client.domainName";

  public static final String NO_TENANT = "default";

  public static final String NO_DOMAIN = "default";

  public static final String MICROSERVICE_VERSION_FACTORY = "servicecomb.microserviceVersionFactory";

  private boolean ssl = true;

  public static final String PROXY_PRE_NAME = "servicecomb.proxy.";

  public static final String PROXY_ENABLE = PROXY_PRE_NAME + "enable";

  public static final String PROXY_HOST = PROXY_PRE_NAME + "host";

  public static final String PROXY_PORT = PROXY_PRE_NAME + "port";

  public static final String PROXY_USERNAME = PROXY_PRE_NAME + "username";

  public static final String PROXY_PASSWD = PROXY_PRE_NAME + "passwd";

  private ServiceRegistryConfig() {

  }

  public String getMicroserviceVersionFactory() {
    return DynamicPropertyFactory.getInstance().getStringProperty(MICROSERVICE_VERSION_FACTORY, null).get();
  }

  /**
   * Currently we don't provide configuration about {@link org.apache.servicecomb.serviceregistry.consumer.StaticMicroserviceVersionFactory}
   */
  public String getStaticMicroserviceVersionFactory() {
    return "org.apache.servicecomb.core.definition.StaticMicroserviceVersionMetaFactory";
  }

  public HttpVersion getHttpVersion() {
    DynamicStringProperty property =
        DynamicPropertyFactory.getInstance()
            .getStringProperty("servicecomb.service.registry.client.httpVersion", "HTTP_1_1");
    return HttpVersion.valueOf(property.get());
  }

  public int getInstances() {
    String instances = "servicecomb.service.registry.client.instances ";
    DynamicIntProperty property =
            DynamicPropertyFactory.getInstance()
                    .getIntProperty(instances, 1);
    int deployInstances = property.get();
    if (deployInstances <= 0) {
      int nAvailableProcessors = Runtime.getRuntime().availableProcessors();
      LOGGER.warn("The property `{}` must be positive integer, fallback to use number of available processors: {}",
              instances,
              nAvailableProcessors);
      return nAvailableProcessors;
    }
    return deployInstances;
  }


  public boolean isSsl() {
    getIpPort();
    return this.ssl;
  }

  public ArrayList<IpPort> getIpPort() {
    List<String> uriList = Deployment.getSystemBootStrapInfo(DeploymentProvider.SYSTEM_KEY_SERVICE_CENTER).getAccessURL();
    ArrayList<IpPort> ipPortList = new ArrayList<>();
    uriList.forEach(anUriList -> {
      try {
        URI uri = new URI(anUriList.trim());
        this.ssl = "https".equals(uri.getScheme());
        ipPortList.add(NetUtils.parseIpPort(uri));
      } catch (Exception e) {
        LOGGER.error("servicecomb.service.registry.address invalid : {}", anUriList, e);
      }
    });
    return ipPortList;
  }

  public String getTransport() {
    return "rest";
  }

  public int getConnectionTimeout() {
    DynamicIntProperty property =
        DynamicPropertyFactory.getInstance()
            .getIntProperty("servicecomb.service.registry.client.timeout.connection", DEFAULT_TIMEOUT_IN_MS);
    int timeout = property.get();
    return timeout < 0 ? DEFAULT_TIMEOUT_IN_MS : timeout;
  }

  public int getIdleConnectionTimeout() {
    // connection pool idle timeout based on client heart beat interval. Heart beat default value is 30.
    DynamicIntProperty property =
        DynamicPropertyFactory.getInstance()
            .getIntProperty("servicecomb.service.registry.client.timeout.idle", DEFAULT_TIMEOUT_IN_SECONDS * 2);
    int timeout = property.get();
    return timeout < 1 ? DEFAULT_TIMEOUT_IN_SECONDS * 2 : timeout;
  }

  public int getIdleWatchTimeout() {
    // watch idle timeout based on SC PING/PONG interval. SC default value is 30.
    DynamicIntProperty property =
        DynamicPropertyFactory.getInstance()
            .getIntProperty("servicecomb.service.registry.client.timeout.watch", DEFAULT_TIMEOUT_IN_SECONDS * 2);
    int timeout = property.get();
    return timeout < 1 ? DEFAULT_TIMEOUT_IN_SECONDS * 2 : timeout;
  }

  public int getRequestTimeout() {
    DynamicIntProperty property =
        DynamicPropertyFactory.getInstance()
            .getIntProperty("servicecomb.service.registry.client.timeout.request", DEFAULT_REQUEST_TIMEOUT_IN_MS);
    int timeout = property.get();
    return timeout < 1 ? DEFAULT_REQUEST_TIMEOUT_IN_MS : timeout;
  }

  //Set the timeout of the heartbeat request
  public int getHeartBeatRequestTimeout() {
    DynamicIntProperty property =
        DynamicPropertyFactory.getInstance()
            .getIntProperty("servicecomb.service.registry.client.timeout.heartbeat",
                DEFAULT_REQUEST_HEARTBEAT_TIMEOUT_IN_MS);
    int timeout = property.get();
    return timeout < 1 ? DEFAULT_REQUEST_HEARTBEAT_TIMEOUT_IN_MS : timeout;
  }

  public int getHeartbeatInterval() {
    DynamicIntProperty property =
        DynamicPropertyFactory.getInstance()
            .getIntProperty("servicecomb.service.registry.instance.healthCheck.interval",
                DEFAULT_CHECK_INTERVAL_IN_S);
    int interval = property.get();
    return interval < 0 ? DEFAULT_CHECK_INTERVAL_IN_S : interval;
  }

  public int getInstancePullInterval() {
    DynamicIntProperty property =
        DynamicPropertyFactory.getInstance()
            .getIntProperty("servicecomb.service.registry.instance.pull.interval",
                DEFAULT_CHECK_INTERVAL_IN_S);
    int interval = property.get();
    return interval < 0 ? DEFAULT_CHECK_INTERVAL_IN_S : interval;
  }

  public boolean isRegistryAutoDiscovery() {
    DynamicBooleanProperty property =
        DynamicPropertyFactory.getInstance()
            .getBooleanProperty("servicecomb.service.registry.autodiscovery",
                false);
    return property.get();
  }

  public int getResendHeartBeatTimes() {
    DynamicIntProperty property =
        DynamicPropertyFactory.getInstance()
            .getIntProperty("servicecomb.service.registry.instance.healthCheck.times",
                DEFAULT_CHECK_TIMES);
    int times = property.get();
    return times < 0 ? DEFAULT_CHECK_TIMES : times;
  }

  public boolean isEmptyInstanceProtectionEnabled() {
    DynamicBooleanProperty property =
        DynamicPropertyFactory.getInstance()
            .getBooleanProperty("servicecomb.service.registry.instance.empty.protection",
                true);
    return property.get();
  }

  public boolean isPreferIpAddress() {
    DynamicBooleanProperty property =
        DynamicPropertyFactory.getInstance()
            .getBooleanProperty("servicecomb.service.registry.instance.preferIpAddress",
                false);
    return property.get();
  }

  public boolean isWatch() {
    DynamicBooleanProperty property =
        DynamicPropertyFactory.getInstance()
            .getBooleanProperty("servicecomb.service.registry.instance.watch",
                true);
    return property.get();
  }

  public boolean isClientAuthEnabled() {
    String isAuthEnabled = getProperty("false", AUTH_ENABLED);
    return Boolean.parseBoolean(isAuthEnabled);
  }

  public String getRegistryApiVersion() {
    return getProperty("v4", REGISTRY_API_VERSION);
  }

  public String getTenantName() {
    return getProperty(NO_TENANT, TENANT_NAME);
  }

  public String getDomainName() {
    return getProperty(NO_DOMAIN, DOMAIN_NAME);
  }

  public String getAccessKey() {
    String tenantName = getProperty(null, TENANT_ACCESS_KEY);
    return tenantName;
  }

  public String getSecretKey() {
    String tenantName = getProperty(null, TENANT_SECRET_KEY);
    return tenantName;
  }

  public Boolean isProxyEnable() {
    String enable = getProperty("false", PROXY_ENABLE);
    return Boolean.parseBoolean(enable);
  }

  public String getProxyHost() {
    String host = getProperty("127.0.0.1", PROXY_HOST);
    return host;
  }

  public int getProxyPort() {
    String port = getProperty("8080", PROXY_PORT);
    return Integer.parseInt(port);
  }

  public String getProxyUsername() {
    String username = getProperty(null, PROXY_USERNAME);
    return username;
  }

  public String getProxyPasswd() {
    String passwd = getProperty(null, PROXY_PASSWD);
    return passwd;
  }

  private String getProperty(String defaultValue, String... keys) {
    String property = null;
    for (String key : keys) {
      property = DynamicPropertyFactory.getInstance().getStringProperty(key, null).get();
      if (property != null) {
        break;
      }
    }

    if (property != null) {
      return property;
    } else {
      return defaultValue;
    }
  }
}
