package app.delegates;

import app.model.enams.ApplicationType;
import app.services.interfases.ApplicationServiceInterface;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("checkWaitingEmployeeApplicationDelegate")
@RequiredArgsConstructor
public class CheckWaitingEmployeeApplicationDelegate implements JavaDelegate {

    private static final Logger log = LoggerFactory.getLogger(CheckWaitingEmployeeApplicationDelegate.class);

    private final ApplicationServiceInterface applicationService;

    @Override
    public void execute(DelegateExecution execution) {
        String accountNumber = (String) execution.getVariable("accountNumber");
        ApplicationType applicationType = readApplicationType(execution.getVariable("applicationType"));

        if (accountNumber == null || accountNumber.isBlank()) {
            execution.setVariable("errorMessage", "accountNumber is required");
            throw new BpmnError("BAD_REQUEST", "accountNumber is required");
        }
        if (applicationType == null) {
            execution.setVariable("errorMessage", "applicationType is required");
            throw new BpmnError("BAD_REQUEST", "applicationType is required");
        }

        String applicationEmail;
        try {
            applicationEmail = applicationService.findWaitingEmployeeApplicationEmail(accountNumber, applicationType).orElse(null);
        } catch (RuntimeException exception) {
            execution.setVariable("errorMessage", exception.getMessage());
            throw new BpmnError("BAD_REQUEST", exception.getMessage());
        }

        boolean found = applicationEmail != null;
        execution.setVariable("applicationFound", found);
        if (found) {
            execution.setVariable("applicationEmail", applicationEmail);
        } else {
            execution.setVariable("errorMessage", "Не найдены необработанные заявки");
        }

        log.info("CheckWaitingEmployeeApplicationDelegate: processInstanceId={}, accountNumber={}, applicationType={}, found={}",
                execution.getProcessInstanceId(), accountNumber, applicationType, found);
    }

    private ApplicationType readApplicationType(Object value) {
        if (value instanceof ApplicationType applicationType) {
            return applicationType;
        }
        if (value instanceof String stringValue && !stringValue.isBlank()) {
            return ApplicationType.valueOf(stringValue);
        }
        return null;
    }
}
