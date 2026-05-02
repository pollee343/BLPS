package app.jca;

import app.jca.interfaces.ConnectionInterface;
import jakarta.resource.cci.ConnectionFactory;
import jakarta.resource.spi.*;
import jakarta.resource.ResourceException;
import org.springframework.beans.factory.annotation.Value;

import javax.security.auth.Subject;
import java.io.Serializable;
import java.io.PrintWriter;
import java.util.Objects;
import java.util.Set;

@ConnectionDefinition(
        connectionFactory = ConnectionFactory.class,
        connectionFactoryImpl = JiraConnectionFactory.class,
        connection = ConnectionInterface.class,
        connectionImpl = JiraConnection.class
)
public class JiraManagedConnectionFactory implements ManagedConnectionFactory, Serializable {

    private static final long serialVersionUID = 1L;

    public JiraManagedConnectionFactory() {
    }

    @Value("${jira.baseUrl}")
    private String baseUrl;
    @Value("${jira.apiToken}")
    private String apiToken;
    @Value("${jira.userEmail}")
    private String userEmail; // todo мб нужно заменить на email

    public String getBaseUrl() { return baseUrl; }
    public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }

    public String getApiToken() { return apiToken; }
    public void setApiToken(String apiToken) { this.apiToken = apiToken; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    @Override
    public Object createConnectionFactory(ConnectionManager connectionManager) {
        return new JiraConnectionFactory(this, connectionManager);
    }

    @Override
    public Object createConnectionFactory() {
        return new JiraConnectionFactory(this, new StandaloneConnectionManager());
    }

    private final class StandaloneConnectionManager implements ConnectionManager, Serializable {
        private static final long serialVersionUID = 1L;

        @Override
        public Object allocateConnection(ManagedConnectionFactory managedConnectionFactory, ConnectionRequestInfo connectionRequestInfo)
                throws ResourceException {
            ManagedConnection managedConnection = managedConnectionFactory.createManagedConnection(null, connectionRequestInfo);
            return managedConnection.getConnection(null, connectionRequestInfo);
        }
    }

    @Override
    public ManagedConnection createManagedConnection(Subject subject, ConnectionRequestInfo cxRequestInfo) {
        return new JiraManagedConnection(this);
    }

    @Override
    public ManagedConnection matchManagedConnections(Set connectionSet, Subject subject, ConnectionRequestInfo cxRequestInfo) {
        return connectionSet.isEmpty()
                ? null
                : (ManagedConnection) connectionSet.iterator().next();
    }

    @Override
    public void setLogWriter(PrintWriter out) {}

    @Override
    public PrintWriter getLogWriter() { return null; }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        JiraManagedConnectionFactory other = (JiraManagedConnectionFactory) obj;

        if (!Objects.equals(baseUrl, other.baseUrl)) return false;
        if (!Objects.equals(apiToken, other.apiToken)) return false;
        return Objects.equals(userEmail, other.userEmail);
    }

    @Override
    public int hashCode() {
        int result = baseUrl != null ? baseUrl.hashCode() : 0;
        result = 31 * result + (apiToken != null ? apiToken.hashCode() : 0);
        result = 31 * result + (userEmail != null ? userEmail.hashCode() : 0);
        return result;
    }
}
