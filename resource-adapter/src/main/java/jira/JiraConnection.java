package jira;

import jakarta.resource.ResourceException;
import jakarta.resource.cci.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;

public class JiraConnection implements Connection {

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

    public String createTask(String title, String description) throws ResourceException {
        try {
            String auth = mcf.getUserEmail() + ":" + mcf.getApiToken();
            String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());

            String jsonBody = String.format("""
                {
                  "fields": {
                    "project": { "key": "SCRUM" },
                    "summary": "%s",
                    "description": "%s",
                    "issuetype": { "name": "Task" }
                  }
                }
                """, title, description);

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(mcf.getBaseUrl() + "/rest/api/2/issue"))
                    .header("Authorization", "Basic " + encodedAuth)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 400) {
                throw new ResourceException("Jira error: " + response.body());
            }

            // Извлекаем ключ
            String responseBody = response.body();
            String key = responseBody.split("\"key\":\"")[1].split("\"")[0];

            System.out.println("Jira issue created: " + key);
            return key;

        } catch (Exception e) {
            throw new ResourceException("Failed to create Jira issue", e);
        }
    }

    @Override
    public Interaction createInteraction() throws ResourceException {
        return null;
    }

    @Override
    public LocalTransaction getLocalTransaction() throws ResourceException {
        return null;
    }

    @Override
    public ConnectionMetaData getMetaData() throws ResourceException {
        return null;
    }

    @Override
    public ResultSetInfo getResultSetInfo() throws ResourceException {
        return null;
    }

    public void close() {
        closed = true;
    }
}
