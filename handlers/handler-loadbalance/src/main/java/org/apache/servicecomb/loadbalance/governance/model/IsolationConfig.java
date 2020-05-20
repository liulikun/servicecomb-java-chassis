package org.apache.servicecomb.loadbalance.governance.model;

public class IsolationConfig {
    private boolean enabled;
    private int singleTestTime;
    private int errorThresholdPercentage;
    private int enableRequestThreshold;
    private int continuousFailureThreshold;
    private int minIsolationTime;

    public IsolationConfig() {
    }

    public IsolationConfig withEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public IsolationConfig withSingleTestTime(int singleTestTime) {
        this.singleTestTime = singleTestTime;
        return this;
    }

    public IsolationConfig withMinIsolationTime(int minIsolationTime) {
        this.minIsolationTime = minIsolationTime;
        return this;
    }

    public IsolationConfig withErrorThresholdPercentage(int errorThresholdPercentage) {
        this.errorThresholdPercentage = errorThresholdPercentage;
        return this;
    }

    public IsolationConfig withEnableRequestThreshold(int enableRequestThreshold) {
        this.enableRequestThreshold = enableRequestThreshold;
        return this;
    }

    public IsolationConfig withContinuousFailureThreshold(int continuousFailureThreshold) {
        this.continuousFailureThreshold = continuousFailureThreshold;
        return this;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public int getErrorThresholdPercentage() {
        return errorThresholdPercentage;
    }

    public int getEnableRequestThreshold() {
        return enableRequestThreshold;
    }

    public int getContinuousFailureThreshold() {
        return continuousFailureThreshold;
    }

    public int getSingleTestTime() {
        return singleTestTime;
    }

    public int getMinIsolationTime() {
        return minIsolationTime;
    }
}
