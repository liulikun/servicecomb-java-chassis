package org.apache.servicecomb.loadbalance.governance;

import org.apache.servicecomb.loadbalance.Configuration;
import org.apache.servicecomb.loadbalance.governance.model.ConfigSender;
import org.apache.servicecomb.loadbalance.governance.model.IsolationConfig;
import org.apache.servicecomb.loadbalance.governance.model.IsolationConfigAction;
import org.apache.servicecomb.loadbalance.governance.model.IsolationConfigWithSender;
import org.apache.servicecomb.provider.springmvc.reference.RestTemplateBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

import java.util.Set;

public class IsolationConfigReporter implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(IsolationConfigReporter.class);

    private Set<String> targetServices;
    private String fromServiceName;
    private String fromInstanceId;
    private String cseUri;

    public IsolationConfigReporter(Set<String> targetServices, String fromServiceName, String fromInstanceId, String cseUri) {
        this.targetServices = targetServices;
        this.fromServiceName = fromServiceName;
        this.fromInstanceId = fromInstanceId;
        this.cseUri = cseUri;
    }

    @Override
    public void run() {
        try {
            send();
        } catch (Throwable e) {
            //TODO: set a flag if the service is down, then check the service status before send
            LOGGER.warn("Error when sending config to governance service", e);
        }
    }

    private void send() {
        for (String targetServiceName : targetServices) {
            boolean isolationFilterOpen = Configuration.INSTANCE.isIsolationFilterOpen(targetServiceName);
            int singleTestTime = Configuration.INSTANCE.getSingleTestTime(targetServiceName);
            System.out.println("singleTestTime:" + targetServiceName + ":" + singleTestTime);
            int minIsolationTime = Configuration.INSTANCE.getMinIsolationTime(targetServiceName);
            int continuousFailureThreshold = Configuration.INSTANCE.getContinuousFailureThreshold(targetServiceName);
            int enableRequestThreshold = Configuration.INSTANCE.getEnableRequestThreshold(targetServiceName);
            int errorThresholdPercentage = Configuration.INSTANCE.getErrorThresholdPercentage(targetServiceName);

            ConfigSender configSender = new ConfigSender()
                    .withTargetServiceName(targetServiceName)
                    .withFromServiceName(fromServiceName)
                    .withFromInstanceId(fromInstanceId)
                    .withSentAtSecond(System.currentTimeMillis() / 1000);

            IsolationConfig isolationConfig = new IsolationConfig()
                    .withEnabled(isolationFilterOpen)
                    .withContinuousFailureThreshold(continuousFailureThreshold)
                    .withEnableRequestThreshold(enableRequestThreshold)
                    .withErrorThresholdPercentage(errorThresholdPercentage)
                    .withSingleTestTime(singleTestTime)
                    .withMinIsolationTime(minIsolationTime);

            IsolationConfigWithSender isolationConfigFromSender = new IsolationConfigWithSender(configSender, isolationConfig);
            RestTemplate restTemplate = RestTemplateBuilder.create();
            for (int i = 0; i < 1; i++) {
                restTemplate.postForObject(
                        cseUri,
                        isolationConfigFromSender,
                        IsolationConfigAction.class);
            }
        }
    }
}
