package app.camunda.delegates;

import app.dao.PromisedPaymentDAOService;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component("calculateMonthlyPromisedPaymentsDelegate")
@RequiredArgsConstructor
public class CalculateMonthlyPromisedPaymentsDelegate extends PromisedPaymentProcessVariables implements JavaDelegate {

    private final PromisedPaymentDAOService promisedPaymentDAOService;

    @Override
    public void execute(DelegateExecution execution) {
        BigDecimal amount = requireAmount(execution);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime monthStart = now.toLocalDate().withDayOfMonth(1).atStartOfDay();
        LocalDateTime nextMonthStart = monthStart.plusMonths(1);
        BigDecimal monthlyTaken = promisedPaymentDAOService.sumAmountTakenForPeriod(
                requireUserDataId(execution),
                monthStart,
                nextMonthStart
        );

        execution.setVariable("monthlyTaken", monthlyTaken);
        execution.setVariable("monthlyLimitExceeded", monthlyTaken.add(amount).compareTo(BigDecimal.valueOf(3000)) > 0);
    }
}
