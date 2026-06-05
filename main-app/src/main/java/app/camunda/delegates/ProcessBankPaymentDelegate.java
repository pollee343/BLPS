package app.camunda.delegates;

import app.model.enams.BankOperationStatus;
import app.services.interfases.BankServiceInterface;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component("processBankPaymentDelegate")
@RequiredArgsConstructor
public class ProcessBankPaymentDelegate extends BalanceTopUpProcessVariables implements JavaDelegate {

    private final BankServiceInterface bankService;

    @Override
    @Transactional
    public void execute(DelegateExecution execution) {
        BankOperationStatus status = bankService.processPayment(
                getString(execution, "cardNumber"),
                getString(execution, "cvc"),
                getBigDecimal(execution, "amount")
        );
        execution.setVariable("bankPaymentStatus", status.name());
    }
}
