package app.jca;

import jakarta.resource.cci.*;
import jakarta.resource.ResourceException;
import jakarta.resource.spi.ConnectionManager;

import javax.naming.NamingException;
import javax.naming.Reference;

public class JiraConnectionFactory implements ConnectionFactory {

    private final ConnectionManager connectionManager;
    private final JiraManagedConnectionFactory mcf;

    public JiraConnectionFactory(
            JiraManagedConnectionFactory mcf,
            ConnectionManager connectionManager
    ) {
        this.mcf = mcf;
        this.connectionManager = connectionManager;
    }

    public JiraConnectionFactory() {
        this.mcf = null;
        this.connectionManager = null;
    }

    @Override
    public Connection getConnection() {
        if (mcf == null) {
            throw new IllegalStateException("ManagedConnectionFactory is not set");
        }

        try {
            if (connectionManager != null) {
                return (Connection) connectionManager.allocateConnection(mcf, null);
            }

            JiraManagedConnection managedConnection =
                    (JiraManagedConnection) mcf.createManagedConnection(null, null);
            return (Connection) managedConnection.getConnection(null, null);
        } catch (ResourceException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Connection getConnection(ConnectionSpec connectionSpec) throws ResourceException {
        return getConnection();
    }

    @Override
    public RecordFactory getRecordFactory() throws ResourceException {
        return null;
    }

    @Override
    public ResourceAdapterMetaData getMetaData() throws ResourceException {
        return null;
    }

    @Override
    public void setReference(Reference reference) {

    }

    @Override
    public Reference getReference() throws NamingException {
        return null;
    }
}
