import jakarta.resource.spi.ActivationSpec;
import jakarta.resource.spi.BootstrapContext;
import jakarta.resource.spi.ResourceAdapter;
import jakarta.resource.spi.endpoint.MessageEndpointFactory;

import javax.transaction.xa.XAResource;

public class JiraResourceAdapterImpl implements ResourceAdapter {

    public void start(BootstrapContext ctx) {}

    public void stop() {}

    public void endpointActivation(MessageEndpointFactory endpointFactory, ActivationSpec spec) {}

    public void endpointDeactivation(MessageEndpointFactory endpointFactory, ActivationSpec spec) {}

    public XAResource[] getXAResources(ActivationSpec[] specs) {
        return new XAResource[0];
    }
}
