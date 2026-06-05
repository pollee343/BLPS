package app.camunda.delegates;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component("calculatePromisedPaymentRepayAmountDelegate")
public class CalculatePromisedPaymentRepayAmountDelegate extends PromisedPaymentProcessVariables implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) {
        BigDecimal amountToRepay = requireAmount(execution)
                .multiply(BigDecimal.valueOf(1.2))
                .setScale(2, RoundingMode.HALF_UP);
        execution.setVariable("amountToRepay", amountToRepay);
    }
}
