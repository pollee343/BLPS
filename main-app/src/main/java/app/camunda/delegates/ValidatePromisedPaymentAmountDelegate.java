package app.camunda.delegates;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component("validatePromisedPaymentAmountDelegate")
public class ValidatePromisedPaymentAmountDelegate extends PromisedPaymentProcessVariables implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) {
        BigDecimal amount = getBigDecimal(execution, "amount");
        boolean amountValid = amount != null
                && amount.compareTo(BigDecimal.ZERO) > 0
                && amount.compareTo(BigDecimal.valueOf(1500)) <= 0;
        execution.setVariable("amountValid", amountValid);
    }
}
