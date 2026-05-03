package app.services;

import app.dao.ApplicationDAOService;
import app.model.entities.Application;
import app.model.enams.ApplicationStatus;
import jakarta.resource.ResourceException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ApplicationProcessingService {

    private final ApplicationDAOService applicationDAOService;
    private final JiraAccessService jiraAccessService;

    //берёт заявку из БД по applicationId: CREATED -> WAITING_EMPLOYEE
    @Transactional
    public void process(Long applicationId) {
        Application application = applicationDAOService.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("Application not found: " + applicationId));

        if (application.getApplicationStatus() != ApplicationStatus.CREATED) {
            return;
        }

        try {
            jiraAccessService.createTask(application);

            application.setApplicationStatus(ApplicationStatus.WAITING_EMPLOYEE);

            // Later this is where the worker will create a task in the external EIS through JCA.
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
