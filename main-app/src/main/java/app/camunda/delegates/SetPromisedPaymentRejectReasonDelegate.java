package app.camunda.delegates;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component("setPromisedPaymentRejectReasonDelegate")
public class SetPromisedPaymentRejectReasonDelegate extends PromisedPaymentProcessVariables implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) {
        String reason;

        if (Boolean.FALSE.equals(execution.getVariable("accessAllowed"))) {
            reason = "Доступ запрещен для userDataId="
                    + getLong(execution, "userDataId") + ".";
        } else if (Boolean.FALSE.equals(execution.getVariable("amountValid"))) {
            reason = "Некорректная сумма обещанного платежа: сумма должна быть больше 0 и не больше 1500 руб.";
        } else if (Boolean.TRUE.equals(execution.getVariable("hasOverduePayment"))) {
            reason = "Обещанный платеж отклонен: есть непогашенный просроченный обещанный платеж.";
        } else if (Boolean.TRUE.equals(execution.getVariable("monthlyLimitExceeded"))) {
            BigDecimal monthlyTaken = getBigDecimal(execution, "monthlyTaken");
            BigDecimal available = BigDecimal.valueOf(3000)
                    .subtract(monthlyTaken == null ? BigDecimal.ZERO : monthlyTaken)
                    .max(BigDecimal.ZERO);
            reason = "Превышен месячный лимит обещанных платежей. Доступно: " + available + " руб.";
        } else {
            reason = "Обещанный платеж отклонен.";
        }

        execution.setVariable("errorMessage", reason);
    }
}
