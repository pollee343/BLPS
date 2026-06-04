package app.delegates;

import app.model.entities.Application;
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

    @Override
    public void execute(DelegateExecution execution) {
        Object rawApplication = execution.getVariable("application");
        if (!(rawApplication instanceof Application application)) {
            execution.setVariable("errorMessage", "application is required");
            throw new BpmnError("JIRA_EXPORT_FAILED", "application is required");
        }

        try {
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
}
