package app.delegates;

import app.model.entities.Application;
import app.services.ApplicationProcessingService;
import app.services.JiraAccessService;
import jakarta.resource.ResourceException;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("createJiraIssueDelegate")
@RequiredArgsConstructor
public class CreateJiraIssueDelegate implements JavaDelegate {

    private static final Logger log = LoggerFactory.getLogger(CreateJiraIssueDelegate.class);

    private final JiraAccessService jiraAccessService;
    private final ApplicationProcessingService applicationProcessingService;

    @Override
    public void execute(DelegateExecution execution) {
        Long applicationId = readLong(execution.getVariable("applicationId"));
        if (applicationId == null) {
            execution.setVariable("errorMessage", "applicationId is required");
            throw new BpmnError("JIRA_EXPORT_FAILED", "applicationId is required");
        }

        try {
            Application application = applicationProcessingService.getApplicationById(applicationId);
            String jiraIssueKey = jiraAccessService.createTask(application);
            execution.setVariable("jiraIssueKey", jiraIssueKey);
            execution.setVariable("jiraTaskCreated", true);
            log.info("CreateJiraIssueDelegate: processInstanceId={}, applicationId={}, jiraIssueKey={}",
                    execution.getProcessInstanceId(), application.getId(), jiraIssueKey);
        } catch (ResourceException | RuntimeException exception) {
            execution.setVariable("jiraTaskCreated", false);
            execution.setVariable("errorMessage", exception.getMessage());
            throw new BpmnError("JIRA_EXPORT_FAILED", exception.getMessage());
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
