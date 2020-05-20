package org.apache.servicecomb.loadbalance.governance.model;

public class ConfigSender {
    private String targetServiceName;
    private String fromServiceName;
    private String fromInstanceId;
    private long sentAtSecond;

    public ConfigSender() {}

    public ConfigSender withTargetServiceName(String targetServiceName) {
        this.targetServiceName = targetServiceName;
        return this;
    }

    public ConfigSender withFromServiceName(String fromServiceName) {
        this.fromServiceName = fromServiceName;
        return this;
    }

    public ConfigSender withFromInstanceId(String fromInstanceId) {
        this.fromInstanceId = fromInstanceId;
        return this;
    }

    public ConfigSender withSentAtSecond(long sentAtSecond) {
        this.sentAtSecond = sentAtSecond;
        return this;
    }

    public String getTargetServiceName() {
        return targetServiceName;
    }

    public String getFromServiceName() {
        return fromServiceName;
    }

    public String getFromInstanceId() {
        return fromInstanceId;
    }

    public long getSentAtSecond() {
        return sentAtSecond;
    }
}
