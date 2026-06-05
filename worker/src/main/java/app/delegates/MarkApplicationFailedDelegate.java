package app.delegates;

import app.model.enams.ApplicationStatus;
import app.services.ApplicationProcessingService;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("markApplicationFailedDelegate")
@RequiredArgsConstructor
public class MarkApplicationFailedDelegate implements JavaDelegate {

    private static final Logger log = LoggerFactory.getLogger(MarkApplicationFailedDelegate.class);

    private final ApplicationProcessingService applicationProcessingService;

    @Override
    public void execute(DelegateExecution execution) {
        Long applicationId = readLong(execution.getVariable("applicationId"));
        if (applicationId == null) {
            execution.setVariable("errorMessage", "applicationId is required");
            throw new BpmnError("BAD_REQUEST", "applicationId is required");
        }

        try {
            var application = applicationProcessingService.getApplicationById(applicationId);
            applicationProcessingService.updateStatus(application, ApplicationStatus.FAILED);
            log.info("MarkApplicationFailedDelegate: processInstanceId={}, applicationId={}",
                    execution.getProcessInstanceId(), application.getId());
        } catch (RuntimeException exception) {
            execution.setVariable("errorMessage", exception.getMessage());
            throw new BpmnError("BAD_REQUEST", exception.getMessage());
        }
    }

    private Long readLong(Object value) {
        if (value instanceof Number number) {
            return number.longValue();
        }
        if (value instanceof String stringValue && !stringValue.isBlank()) {
            try {
                return Long.parseLong(stringValue);
            } catch (NumberFormatException ignored) {
                return null;
            }
        }
        return null;
    }
}
