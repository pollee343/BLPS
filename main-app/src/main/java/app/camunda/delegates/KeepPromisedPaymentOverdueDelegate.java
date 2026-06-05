package app.camunda.delegates;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component("keepPromisedPaymentOverdueDelegate")
public class KeepPromisedPaymentOverdueDelegate implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) {
        // Status is already OVERDUE; this task documents the BPMN branch.
    }
}
