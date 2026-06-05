package app.camunda.delegates;

import app.dao.UserDataDAOService;
import app.model.entities.UserData;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component("increaseBalanceByPromisedPaymentDelegate")
@RequiredArgsConstructor
public class IncreaseBalanceByPromisedPaymentDelegate extends PromisedPaymentProcessVariables implements JavaDelegate {

    private final UserDataDAOService userDataDAOService;

    @Override
    @Transactional
    public void execute(DelegateExecution execution) {
        UserData userData = userDataDAOService.findById(requireUserDataId(execution))
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));
        userData.setBalance(userData.getBalance().add(requireAmount(execution)));
        userDataDAOService.save(userData);
    }
}
