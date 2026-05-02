package app.jca;

import jakarta.resource.spi.ActivationSpec;
import jakarta.resource.spi.BootstrapContext;
import jakarta.resource.spi.Connector;
import jakarta.resource.spi.ResourceAdapter;
import jakarta.resource.spi.endpoint.MessageEndpointFactory;

import javax.transaction.xa.XAResource;
import java.io.Serializable;

@Connector(
        displayName = "Jira Resource Adapter",
        vendorName = "BLPS",
        eisType = "Jira",
        version = "1.0"
)
public class JiraResourceAdapterImpl implements ResourceAdapter, Serializable {

    private static final long serialVersionUID = 1L;

    public JiraResourceAdapterImpl() {
    }

    public void start(BootstrapContext ctx) {}

    public void stop() {}

    public void endpointActivation(MessageEndpointFactory endpointFactory, ActivationSpec spec) {}

    public void endpointDeactivation(MessageEndpointFactory endpointFactory, ActivationSpec spec) {}

    public XAResource[] getXAResources(ActivationSpec[] specs) {
        return new XAResource[0];
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        return (obj instanceof JiraResourceAdapterImpl);
    }

    @Override
    public int hashCode() {
        return JiraResourceAdapterImpl.class.hashCode();
    }
}
