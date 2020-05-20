package org.apache.servicecomb.loadbalance.governance;

import org.apache.servicecomb.core.BootListener;
import org.apache.servicecomb.serviceregistry.DiscoveryManager;
import org.apache.servicecomb.serviceregistry.RegistryUtils;
import org.apache.servicecomb.serviceregistry.api.registry.MicroserviceInstance;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class GovernanceServiceScheduler implements BootListener {
    public static final String GOVERNANCE_SERVICE_NAME = "servicecomb-governance-service";
    @Override
    public void onAfterRegistry(BootEvent event) {
        List<MicroserviceInstance> governanceService = RegistryUtils.findServiceInstance(RegistryUtils.getAppId(), GOVERNANCE_SERVICE_NAME, null);
        if (governanceService != null && !governanceService.isEmpty()) {
            Set<String> serviceNames = DiscoveryManager.INSTANCE.getAppManager().getApps().keySet();
            String fromSourceName = RegistryUtils.getMicroservice().getServiceName();
            String fromInstanceId = RegistryUtils.getMicroserviceInstance().getInstanceId();
            String cseUri = String.format("cse://%s/api/v1/config/isolation", GOVERNANCE_SERVICE_NAME);
            IsolationConfigReporter reporter = new IsolationConfigReporter(serviceNames, fromSourceName, fromInstanceId, cseUri);
            ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
            executorService.scheduleWithFixedDelay(reporter, 10, 10, TimeUnit.SECONDS);
        }
    }
}
