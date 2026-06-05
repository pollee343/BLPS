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

@Component("saveTopUpIncomeOperationDelegate")
@RequiredArgsConstructor
public class SaveTopUpIncomeOperationDelegate extends BalanceTopUpProcessVariables implements JavaDelegate {

    private final UserDataDAOService userDataDAOService;
    private final MoneyOperationDAOService moneyOperationDAOService;

    @Override
    @Transactional
    public void execute(DelegateExecution execution) {
        UserData userData = userDataDAOService.findById(getLong(execution, "userDataId"))
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));

        MoneyOperation op = new MoneyOperation();
        op.setOperationTime(LocalDateTime.now());
        op.setType(OperationType.INCOME);
        op.setAmount(getBigDecimal(execution, "amount"));
        op.setUserData(userData);
        op.setName("Регистрация платежа: Банковская карта");

        moneyOperationDAOService.save(op);
    }
}
