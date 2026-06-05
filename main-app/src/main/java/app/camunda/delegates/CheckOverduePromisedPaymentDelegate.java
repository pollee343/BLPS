package app.camunda.delegates;

import app.dao.PromisedPaymentDAOService;
import app.model.enams.PromisedPaymentStatus;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component("checkOverduePromisedPaymentDelegate")
@RequiredArgsConstructor
public class CheckOverduePromisedPaymentDelegate extends PromisedPaymentProcessVariables implements JavaDelegate {

    private final PromisedPaymentDAOService promisedPaymentDAOService;

    @Override
    public void execute(DelegateExecution execution) {
        boolean hasOverduePayment = promisedPaymentDAOService.existsByUserDataIdAndStatus(
                requireUserDataId(execution),
                PromisedPaymentStatus.OVERDUE
        );
        execution.setVariable("hasOverduePayment", hasOverduePayment);
    }
}
