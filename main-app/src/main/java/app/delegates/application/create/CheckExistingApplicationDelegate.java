package app.delegates.application.create;

import app.services.interfases.ApplicationServiceInterface;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("checkExistingApplicationDelegate")
@RequiredArgsConstructor
public class CheckExistingApplicationDelegate implements JavaDelegate {

    private static final Logger log = LoggerFactory.getLogger(CheckExistingApplicationDelegate.class);

    private final ApplicationServiceInterface applicationService;

    @Override
    public void execute(DelegateExecution execution) {
        String accountNumber = (String) execution.getVariable("accountNumber");
        if (accountNumber == null || accountNumber.isBlank()) {
            execution.setVariable("errorMessage", "accountNumber is required");
            throw new BpmnError("BAD_REQUEST", "accountNumber is required");
        }

        boolean exists;
        try {
            exists = applicationService.hasPromisedPaymentRejectionApplication(accountNumber);
        } catch (RuntimeException exception) {
            execution.setVariable("errorMessage", exception.getMessage());
            throw new BpmnError("BAD_REQUEST", exception.getMessage());
        }

        execution.setVariable("applicationExists", exists);
        if (exists) {
            execution.setVariable("errorMessage",
                    "Заявка на получение информации об отказе в получении обещанного платежа уже создана");
        }

        log.info("CheckExistingApplicationDelegate: processInstanceId={}, accountNumber={}, exists={}",
                execution.getProcessInstanceId(), accountNumber, exists);
    }
}
