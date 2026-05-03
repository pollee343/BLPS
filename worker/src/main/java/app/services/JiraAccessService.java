package app.services;

import app.jca.JiraConnectionFactory;
import app.jca.JiraManagedConnectionFactory;
import app.jca.interfaces.ConnectionInterface;
import app.model.enams.ApplicationType;
import app.model.entities.Application;
import jakarta.resource.ResourceException;
import jakarta.resource.cci.Connection;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JiraAccessService {

    private final JiraConnectionFactory connectionFactory;

    public JiraAccessService(
            @Value("${jira.baseUrl}") String baseUrl,
            @Value("${jira.apiToken}") String apiToken,
            @Value("${jira.userEmail}") String userEmail
    ) {
        JiraManagedConnectionFactory mcf = new JiraManagedConnectionFactory();
        mcf.setBaseUrl(baseUrl);
        mcf.setApiToken(apiToken);
        mcf.setUserEmail(userEmail);

        this.connectionFactory = (JiraConnectionFactory) mcf.createConnectionFactory();
    }

    public String createTask(Application application) throws ResourceException {
        Connection connection = null;
        try {

            connection = connectionFactory.getConnection();
            return ((ConnectionInterface) connection)
                    .createTask(createTaskTitle(application), createTaskDescription(application));

        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }

    private String createTaskTitle(Application application){
        if (application.getApplicationType().equals(ApplicationType.PROMISED_PAYMENT_REJECTION)){
            return "Отказ в обещанном платеже №" + application.getId();
        }
        return "Юридически достоверный документ №" + application.getId();
    }

    private String createTaskDescription(Application application){
        String description = "";
        description += "Номер заявки: " + application.getId() + "\\n";
        description += "Номер лицевого счета: " + application.getUserData().getAccountNumber() + "\\n";
        description += "Имейл пользователя: " + application.getEmail() + "\\n";
        return description;
    }

}
