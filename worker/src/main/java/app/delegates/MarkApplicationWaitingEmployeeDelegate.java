package app.delegates;

import app.model.enams.ApplicationStatus;
import app.model.entities.Application;
import app.services.ApplicationProcessingService;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("markApplicationWaitingEmployeeDelegate")
@RequiredArgsConstructor
public class MarkApplicationWaitingEmployeeDelegate implements JavaDelegate {

    private static final Logger log = LoggerFactory.getLogger(MarkApplicationWaitingEmployeeDelegate.class);

    private final ApplicationProcessingService applicationProcessingService;

    @Override
    public void execute(DelegateExecution execution) {
        Object rawApplication = execution.getVariable("application");
        if (!(rawApplication instanceof Application application)) {
            execution.setVariable("errorMessage", "application is required");
            throw new BpmnError("BAD_REQUEST", "application is required");
        }

        try {
            applicationProcessingService.updateStatus(application, ApplicationStatus.WAITING_EMPLOYEE);
            execution.setVariable("application", application);
            log.info("MarkApplicationWaitingEmployeeDelegate: processInstanceId={}, applicationId={}",
                    execution.getProcessInstanceId(), application.getId());
        } catch (RuntimeException exception) {
            execution.setVariable("errorMessage", exception.getMessage());
            throw new BpmnError("BAD_REQUEST", exception.getMessage());
        }
    }
}
