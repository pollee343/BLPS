package app.delegates;

import app.dao.ApplicationDAOService;
import app.dao.UserDataDAOService;
import app.model.enams.ApplicationStatus;
import app.model.enams.ApplicationType;
import app.model.entities.Application;
import app.model.entities.UserData;
import app.services.interfases.ApplicationServiceInterface;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("createPromisedPaymentRejectionApplicationDelegate")
@RequiredArgsConstructor
public class CreatePromisedPaymentRejectionApplicationDelegate implements JavaDelegate {

    private static final Logger log = LoggerFactory.getLogger(CreatePromisedPaymentRejectionApplicationDelegate.class);

    private final ApplicationServiceInterface applicationService;
    private final UserDataDAOService userDataDAOService;
    private final ApplicationDAOService applicationDAOService;

    @Override
    public void execute(DelegateExecution execution) {
        String accountNumber = (String) execution.getVariable("accountNumber");
        String email = (String) execution.getVariable("email");

        if (accountNumber == null || accountNumber.isBlank()) {
            execution.setVariable("errorMessage", "accountNumber is required");
            throw new BpmnError("BAD_REQUEST", "accountNumber is required");
        }
        if (email == null || email.isBlank()) {
            execution.setVariable("errorMessage", "email is required");
            throw new BpmnError("BAD_REQUEST", "email is required");
        }

        applicationService.promisedPaymentRejection(accountNumber, email);

        UserData userData = userDataDAOService.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new BpmnError("BAD_REQUEST", "Пользователь не найден"));

        Application application = applicationDAOService.findWaitingApplications(
                userData,
                ApplicationType.PROMISED_PAYMENT_REJECTION,
                ApplicationStatus.CREATED
        ).orElseThrow(() -> new BpmnError("BAD_REQUEST", "Созданная заявка не найдена"));

        execution.setVariable("applicationId", application.getId());

        log.info("CreatePromisedPaymentRejectionApplicationDelegate: processInstanceId={}, applicationId={}",
                execution.getProcessInstanceId(), application.getId());
    }
}
