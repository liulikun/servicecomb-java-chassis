package org.apache.servicecomb.loadbalance.governance.model;

public class IsolationConfigWithSender {
    private ConfigSender configSender;
    private IsolationConfig isolationConfig;

    public IsolationConfigWithSender(ConfigSender configSender, IsolationConfig isolationConfig) {
        this.configSender = configSender;
        this.isolationConfig = isolationConfig;
    }

    public ConfigSender getConfigSender() {
        return configSender;
    }

    public IsolationConfig getIsolationConfig() {
        return isolationConfig;
    }
}
