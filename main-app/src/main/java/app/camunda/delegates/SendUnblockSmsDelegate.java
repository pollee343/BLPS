package app.camunda.delegates;

import app.dao.ServiceUsageDAOService;
import app.dao.UserDataDAOService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component("sendUnblockSmsDelegate")
public class SendUnblockSmsDelegate extends SendPromisedPaymentSmsSupport implements JavaDelegate {

    public SendUnblockSmsDelegate(UserDataDAOService userDataDAOService, ServiceUsageDAOService serviceUsageDAOService) {
        super(userDataDAOService, serviceUsageDAOService);
    }

    @Override
    public void execute(DelegateExecution execution) {
        sendSms(execution, "Все просроченные обещанные платежи погашены. Номер разблокирован.");
    }
}
