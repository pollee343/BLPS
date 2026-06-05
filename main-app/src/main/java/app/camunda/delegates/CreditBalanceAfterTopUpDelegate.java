package app.camunda.delegates;

import app.dao.UserDataDAOService;
import app.model.entities.UserData;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component("creditBalanceAfterTopUpDelegate")
@RequiredArgsConstructor
public class CreditBalanceAfterTopUpDelegate extends BalanceTopUpProcessVariables implements JavaDelegate {

    private final UserDataDAOService userDataDAOService;

    @Override
    @Transactional
    public void execute(DelegateExecution execution) {
        Long userDataId = getLong(execution, "userDataId");
        UserData userData = userDataDAOService.findById(userDataId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));
        userData.setBalance(userData.getBalance().add(getBigDecimal(execution, "amount")));
        userDataDAOService.save(userData);
    }
}
