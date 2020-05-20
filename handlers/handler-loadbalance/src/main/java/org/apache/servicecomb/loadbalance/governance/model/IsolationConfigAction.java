package org.apache.servicecomb.loadbalance.governance.model;

public class IsolationConfigAction {
    private IsolationConfig isolationConfig;

    public IsolationConfigAction() {
    }

    public IsolationConfigAction(IsolationConfig isolationConfig) {
        this.isolationConfig = isolationConfig;
    }

    public IsolationConfig getIsolationConfig() {
        return isolationConfig;
    }

    public void setIsolationConfig(IsolationConfig isolationConfig) {
        this.isolationConfig = isolationConfig;
    }
}
