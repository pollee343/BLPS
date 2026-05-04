package app.services;

import app.dao.ApplicationDAOService;
import app.model.entities.Application;
import app.model.enams.ApplicationStatus;
import jakarta.resource.ResourceException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

@Service
@RequiredArgsConstructor
public class ApplicationProcessingService {

    private final ApplicationDAOService applicationDAOService;
    private final JiraAccessService jiraAccessService;
    private final TransactionTemplate transactionTemplate;

    //берёт заявку из БД по applicationId: CREATED -> WAITING_EMPLOYEE
    public void process(Long applicationId) {
        transactionTemplate.executeWithoutResult(status -> processInTransaction(applicationId));
    }

    private void processInTransaction(Long applicationId) {
        Application application = applicationDAOService.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("Заявка не найденв: " + applicationId));

        if (application.getApplicationStatus() != ApplicationStatus.CREATED) {
            return;
        }

        try {
            jiraAccessService.createTask(application);

            application.setApplicationStatus(ApplicationStatus.WAITING_EMPLOYEE);
            applicationDAOService.createApplication(application);
        } catch (ResourceException e) {
            throw new RuntimeException(e);
        } catch (RuntimeException exception) {
            application.setApplicationStatus(ApplicationStatus.FAILED);
            applicationDAOService.createApplication(application);
            throw exception;
        }
    }
}
