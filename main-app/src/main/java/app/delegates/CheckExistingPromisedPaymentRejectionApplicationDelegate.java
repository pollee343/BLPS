package app.delegates;

import app.services.interfases.ApplicationServiceInterface;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("checkExistingPromisedPaymentRejectionApplicationDelegate")
@RequiredArgsConstructor
public class CheckExistingPromisedPaymentRejectionApplicationDelegate implements JavaDelegate {

    private static final Logger log = LoggerFactory.getLogger(CheckExistingPromisedPaymentRejectionApplicationDelegate.class);

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
            exists = applicationService.hasCreatedPromisedPaymentRejectionApplication(accountNumber);
        } catch (RuntimeException exception) {
            execution.setVariable("errorMessage", exception.getMessage());
            throw new BpmnError("BAD_REQUEST", exception.getMessage());
        }

        execution.setVariable("applicationExists", exists);
        if (exists) {
            execution.setVariable("errorMessage",
                    "Заявка на получение информации об отказе в получении обещанного платежа уже создана");
        }

        log.info("CheckExistingPromisedPaymentRejectionApplicationDelegate: processInstanceId={}, accountNumber={}, exists={}",
                execution.getProcessInstanceId(), accountNumber, exists);
    }
}
