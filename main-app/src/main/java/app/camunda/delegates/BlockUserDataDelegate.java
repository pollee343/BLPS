package app.camunda.delegates;

import app.dao.UserDataDAOService;
import app.model.entities.UserData;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component("blockUserDataDelegate")
@RequiredArgsConstructor
public class BlockUserDataDelegate extends PromisedPaymentProcessVariables implements JavaDelegate {

    private final UserDataDAOService userDataDAOService;

    @Override
    @Transactional
    public void execute(DelegateExecution execution) {
        UserData userData = userDataDAOService.findById(requireUserDataId(execution))
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));
        userData.setIsBlocked(true);
        userDataDAOService.save(userData);
    }
}
