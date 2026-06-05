package app.camunda.delegates;

import app.dao.UserDataDAOService;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component("validateBalanceTopUpDataDelegate")
@RequiredArgsConstructor
public class ValidateBalanceTopUpDataDelegate extends BalanceTopUpProcessVariables implements JavaDelegate {

    private final UserDataDAOService userDataDAOService;

    @Override
    public void execute(DelegateExecution execution) {
        Long userDataId = getLong(execution, "userDataId");
        BigDecimal amount = getBigDecimal(execution, "amount");
        String cardNumber = getString(execution, "cardNumber");
        String cvc = getString(execution, "cvc");

        boolean valid = userDataId != null
                && userDataDAOService.findById(userDataId).isPresent()
                && amount != null
                && amount.compareTo(BigDecimal.ZERO) > 0
                && cardNumber != null
                && !cardNumber.isBlank()
                && cvc != null
                && !cvc.isBlank();

        execution.setVariable("topUpDataValid", valid);
    }
}
