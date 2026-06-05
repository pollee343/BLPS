package app.camunda.delegates;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component("checkPromisedPaymentAccessDelegate")
public class CheckPromisedPaymentAccessDelegate extends PromisedPaymentProcessVariables implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) {
        requireUserDataId(execution);
        execution.setVariable("accessAllowed", true);
    }
}
