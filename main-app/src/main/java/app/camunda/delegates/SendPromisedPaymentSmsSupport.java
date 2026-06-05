package app.camunda.delegates;

import app.dao.ServiceUsageDAOService;
import app.dao.UserDataDAOService;
import app.model.enams.UsageDirection;
import app.model.enams.UsageType;
import app.model.entities.ServiceUsage;
import app.model.entities.UserData;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;

import java.time.LocalDateTime;

@RequiredArgsConstructor
abstract class SendPromisedPaymentSmsSupport extends PromisedPaymentProcessVariables {

    private final UserDataDAOService userDataDAOService;
    private final ServiceUsageDAOService serviceUsageDAOService;

    protected void sendSms(DelegateExecution execution, String text) {
        UserData userData = userDataDAOService.findById(requireUserDataId(execution))
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));

        ServiceUsage sms = new ServiceUsage();
        sms.setOperationType(UsageType.SMS);
        sms.setDirection(UsageDirection.INCOMING);
        sms.setName(text);
        sms.setUnitsUsed(0);
        sms.setOperationTime(LocalDateTime.now());
        sms.setUserData(userData);

        serviceUsageDAOService.save(sms);
    }
}
