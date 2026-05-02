package app.services;

import app.jca.interfaces.ConnectionInterface;
import jakarta.annotation.Resource;
import jakarta.resource.ResourceException;
import jakarta.resource.cci.Connection;
import jakarta.resource.cci.ConnectionFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ResourceAccessService {

    @Resource(lookup = "java:/eis/JiraCF")
    private ConnectionFactory connectionFactory;

    public String createTask() throws ResourceException {
        Connection connection = null;
        try {
            connection = connectionFactory.getConnection();

            return ((ConnectionInterface) connection).createTask("t", "d");
            // ...
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }

}
