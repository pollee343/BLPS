package app.camunda.delegates;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("startPromisedPaymentProcessingAfterTopUpDelegate")
@RequiredArgsConstructor
public class StartPromisedPaymentProcessingAfterTopUpDelegate extends BalanceTopUpProcessVariables implements JavaDelegate {

    private final RuntimeService runtimeService;

    @Override
    public void execute(DelegateExecution execution) {
        execution.setVariable("successMessage", "Баланс успешно пополнен.");
        runtimeService.startProcessInstanceByMessage(
                "BalanceTopUpSucceeded",
                Map.of("userDataId", getLong(execution, "userDataId"))
        );
    }
}
