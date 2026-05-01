package app.services;

import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class ResourceAccessService {

    @Resource(lookup = "java:/eis/JiraCF")
    private jakarta.resource.cci.ConnectionFactory connectionFactory;

    public String createTask() throws jakarta.resource.ResourceException {
        jakarta.resource.cci.Connection connection = null;
        try {
            connection = connectionFactory.getConnection();

            return ((jira.JiraConnection) connection).createTask("t", "d");
            // ...
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }

}
