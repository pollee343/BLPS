package app.camunda.delegates;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component("setPromisedPaymentSuccessMessageDelegate")
public class SetPromisedPaymentSuccessMessageDelegate extends PromisedPaymentProcessVariables implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) {
        BigDecimal amount = getBigDecimal(execution, "amount");
        String suffix = amount == null ? "." : " на сумму " + amount + " руб.";
        execution.setVariable("successMessage", "Обещанный платеж успешно подключен" + suffix);
    }
}
