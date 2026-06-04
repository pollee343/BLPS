package app.delegates;

import app.model.entities.Application;
import app.services.ApplicationProcessingService;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("loadApplicationForJiraDelegate")
@RequiredArgsConstructor
public class LoadApplicationForJiraDelegate implements JavaDelegate {

    private static final Logger log = LoggerFactory.getLogger(LoadApplicationForJiraDelegate.class);

    private final ApplicationProcessingService applicationProcessingService;

    @Override
    public void execute(DelegateExecution execution) {
        Long applicationId = readLong(execution.getVariable("applicationId"));
        if (applicationId == null) {
            execution.setVariable("errorMessage", "applicationId is required");
            throw new BpmnError("BAD_REQUEST", "applicationId is required");
        }

        try {
            Application application = applicationProcessingService.getApplicationById(applicationId);
            execution.setVariable("application", application);
            log.info("LoadApplicationForJiraDelegate: processInstanceId={}, applicationId={}",
                    execution.getProcessInstanceId(), applicationId);
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
