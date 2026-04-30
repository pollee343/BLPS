import jakarta.resource.spi.ConnectionManager;
import jakarta.resource.spi.ConnectionRequestInfo;
import jakarta.resource.spi.ManagedConnection;
import jakarta.resource.spi.ManagedConnectionFactory;

import javax.security.auth.Subject;
import java.io.PrintWriter;
import java.util.Set;

public class JiraManagedConnectionFactory implements ManagedConnectionFactory {
    private String baseUrl;
    private String apiToken;
    private String projectKey;

    public Object createConnectionFactory(ConnectionManager cxManager) {
        return new JiraConnectionFactory(this, cxManager);
    }

    public Object createConnectionFactory() {
        throw new UnsupportedOperationException();
    }

    public ManagedConnection createManagedConnection(Subject subject, ConnectionRequestInfo cxRequestInfo) {
        return new JiraManagedConnection(this);
    }

    public ManagedConnection matchManagedConnections(
            Set connectionSet,
            Subject subject,
            ConnectionRequestInfo cxRequestInfo
    ) {
        return connectionSet.isEmpty()
                ? null
                : (ManagedConnection) connectionSet.iterator().next();
    }

    public void setLogWriter(PrintWriter out) {}
    public PrintWriter getLogWriter() { return null; }

    public String getBaseUrl() { return baseUrl; }
    public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }

    public String getApiToken() { return apiToken; }
    public void setApiToken(String apiToken) { this.apiToken = apiToken; }

    public String getProjectKey() { return projectKey; }
    public void setProjectKey(String projectKey) { this.projectKey = projectKey; }
}
