package app.camunda.delegates;

import app.dao.PromisedPaymentDAOService;
import app.model.enams.PromisedPaymentStatus;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component("checkHadOverdueBeforeProcessingDelegate")
@RequiredArgsConstructor
public class CheckHadOverdueBeforeProcessingDelegate extends PromisedPaymentProcessVariables implements JavaDelegate {

    private final PromisedPaymentDAOService promisedPaymentDAOService;

    @Override
    public void execute(DelegateExecution execution) {
        Long userDataId = getLong(execution, "userDataId");
        boolean hadOverdueBefore = userDataId != null
                && promisedPaymentDAOService.existsByUserDataIdAndStatus(userDataId, PromisedPaymentStatus.OVERDUE);
        execution.setVariable("hadOverdueBefore", hadOverdueBefore);
    }
}
