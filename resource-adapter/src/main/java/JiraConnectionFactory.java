import interfaces.JiraConnectionFactoryInterface;
import jakarta.resource.ResourceException;
import jakarta.resource.spi.ConnectionManager;

public class JiraConnectionFactory implements JiraConnectionFactoryInterface {

    private final ConnectionManager connectionManager;
    private final JiraManagedConnectionFactory mcf;

    public JiraConnectionFactory(
            JiraManagedConnectionFactory mcf,
            ConnectionManager connectionManager
    ) {
        this.mcf = mcf;
        this.connectionManager = connectionManager;
    }

    @Override
    public JiraConnection getConnection() {
        try {
            return (JiraConnection) connectionManager.allocateConnection(mcf, null);
        } catch (ResourceException e) {
            throw new RuntimeException(e);
        }
    }
}
