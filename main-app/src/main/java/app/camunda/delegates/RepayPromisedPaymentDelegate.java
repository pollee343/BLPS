package app.camunda.delegates;

import app.dao.MoneyOperationDAOService;
import app.dao.PromisedPaymentDAOService;
import app.dao.UserDataDAOService;
import app.model.enams.OperationType;
import app.model.enams.PromisedPaymentStatus;
import app.model.entities.MoneyOperation;
import app.model.entities.PromisedPayment;
import app.model.entities.UserData;
import app.repositories.PromisedPaymentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component("repayPromisedPaymentDelegate")
@RequiredArgsConstructor
public class RepayPromisedPaymentDelegate extends PromisedPaymentProcessVariables implements JavaDelegate {

    private final UserDataDAOService userDataDAOService;
    private final PromisedPaymentRepository promisedPaymentRepository;
    private final PromisedPaymentDAOService promisedPaymentDAOService;
    private final MoneyOperationDAOService moneyOperationDAOService;

    @Override
    @Transactional
    public void execute(DelegateExecution execution) {
        UserData userData = userDataDAOService.findById(requireUserDataId(execution))
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));
        PromisedPayment payment = promisedPaymentRepository.findById(getLong(execution, "promisedPaymentId"))
                .orElseThrow(() -> new EntityNotFoundException("Обещанный платеж не найден"));

        BigDecimal amountToRepay = payment.getAmountToRepay();
        LocalDateTime now = LocalDateTime.now();

        userData.setBalance(userData.getBalance().subtract(amountToRepay));
        userDataDAOService.save(userData);

        payment.setStatus(PromisedPaymentStatus.PAID);
        payment.setRepaidAt(now);
        promisedPaymentRepository.save(payment);

        MoneyOperation op = new MoneyOperation();
        op.setOperationTime(now);
        op.setType(OperationType.EXPENSE);
        op.setAmount(amountToRepay);
        op.setUserData(userData);
        op.setName("Списание обещанного платежа");
        moneyOperationDAOService.save(op);

        boolean hasOverdueAfterProcessing = promisedPaymentDAOService.existsByUserDataIdAndStatus(
                userData.getId(),
                PromisedPaymentStatus.OVERDUE
        );
        userData.setIsBlocked(hasOverdueAfterProcessing);
        userDataDAOService.save(userData);
        execution.setVariable("hasOverdueAfterProcessing", hasOverdueAfterProcessing);
    }
}
