package app.camunda.delegates;

import app.dao.MoneyOperationDAOService;
import app.dao.UserDataDAOService;
import app.model.enams.OperationType;
import app.model.entities.MoneyOperation;
import app.model.entities.UserData;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component("savePromisedPaymentIncomeOperationDelegate")
@RequiredArgsConstructor
public class SavePromisedPaymentIncomeOperationDelegate extends PromisedPaymentProcessVariables implements JavaDelegate {

    private final UserDataDAOService userDataDAOService;
    private final MoneyOperationDAOService moneyOperationDAOService;

    @Override
    @Transactional
    public void execute(DelegateExecution execution) {
        UserData userData = userDataDAOService.findById(requireUserDataId(execution))
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));

        MoneyOperation op = new MoneyOperation();
        op.setOperationTime(LocalDateTime.now());
        op.setType(OperationType.INCOME);
        op.setAmount(requireAmount(execution));
        op.setUserData(userData);
        op.setName("Подключение обещанного платежа");

        moneyOperationDAOService.save(op);
    }
}
