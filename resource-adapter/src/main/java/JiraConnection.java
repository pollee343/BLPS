import interfaces.JiraConnectionInterface;

public class JiraConnection implements JiraConnectionInterface {

    private final JiraManagedConnection managedConnection;
    private final JiraManagedConnectionFactory mcf;
    private boolean closed = false;

    public JiraConnection(
            JiraManagedConnection managedConnection,
            JiraManagedConnectionFactory mcf
    ) {
        this.managedConnection = managedConnection;
        this.mcf = mcf;
    }

    public String createTask(String title, String description) {
        if (closed) {
            throw new IllegalStateException("Connection is closed");
        }

        // todo Здесь обычный HTTP-запрос в Jira/Redmine/YouTrack
        // Например через java.net.http.HttpClient

        return "Jira-1";
    }

    public void close() {
        closed = true;
    }
}
