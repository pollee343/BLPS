package app.services;

import app.dao.ApplicationDAOService;
import app.model.enams.ApplicationStatus;
import app.model.entities.Application;
import jakarta.resource.ResourceException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ApplicationProcessingService {

    private final ApplicationDAOService applicationDAOService;
    private final JiraAccessService jiraAccessService;

    public void process(Long applicationId) {
        Application application = getApplicationById(applicationId);
        if (application.getApplicationStatus() != ApplicationStatus.CREATED) {
            return;
        }

        try {
            jiraAccessService.createTask(application);
            updateStatus(application, ApplicationStatus.WAITING_EMPLOYEE);
        } catch (ResourceException e) {
            throw new RuntimeException(e);
        } catch (RuntimeException exception) {
            updateStatus(application, ApplicationStatus.FAILED);
            throw exception;
        }
    }

    public Application getApplicationById(Long applicationId) {
        return applicationDAOService.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("Application not found: " + applicationId));
    }

    public void updateStatus(Application application, ApplicationStatus applicationStatus) {
        application.setApplicationStatus(applicationStatus);
        applicationDAOService.createApplication(application);
    }
}
