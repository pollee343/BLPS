package app.camunda.delegates;

import app.dao.UserDataDAOService;
import app.model.entities.UserData;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component("checkBalanceForRepaymentDelegate")
@RequiredArgsConstructor
public class CheckBalanceForRepaymentDelegate extends PromisedPaymentProcessVariables implements JavaDelegate {

    private final UserDataDAOService userDataDAOService;

    @Override
    public void execute(DelegateExecution execution) {
        UserData userData = userDataDAOService.findById(requireUserDataId(execution))
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));
        execution.setVariable("balanceEnough", userData.getBalance().compareTo(getBigDecimal(execution, "amountToRepay")) >= 0);
    }
}
