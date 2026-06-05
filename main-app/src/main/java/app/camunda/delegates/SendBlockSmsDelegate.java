package app.camunda.delegates;

import app.dao.ServiceUsageDAOService;
import app.dao.UserDataDAOService;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component("sendBlockSmsDelegate")
public class SendBlockSmsDelegate extends SendPromisedPaymentSmsSupport implements JavaDelegate {

    public SendBlockSmsDelegate(UserDataDAOService userDataDAOService, ServiceUsageDAOService serviceUsageDAOService) {
        super(userDataDAOService, serviceUsageDAOService);
    }

    @Override
    public void execute(DelegateExecution execution) {
        sendSms(execution, "Номер заблокирован из-за просроченного обещанного платежа на "
                + getBigDecimal(execution, "amountToRepay") + " руб.");
    }
}
