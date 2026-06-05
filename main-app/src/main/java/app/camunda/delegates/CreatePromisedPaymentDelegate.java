package app.camunda.delegates;

import app.dao.UserDataDAOService;
import app.model.enams.PromisedPaymentStatus;
import app.model.entities.PromisedPayment;
import app.model.entities.UserData;
import app.repositories.PromisedPaymentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component("createPromisedPaymentDelegate")
@RequiredArgsConstructor
public class CreatePromisedPaymentDelegate extends PromisedPaymentProcessVariables implements JavaDelegate {

    private final UserDataDAOService userDataDAOService;
    private final PromisedPaymentRepository promisedPaymentRepository;

    @Override
    @Transactional
    public void execute(DelegateExecution execution) {
        UserData userData = userDataDAOService.findById(requireUserDataId(execution))
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));
        LocalDateTime now = LocalDateTime.now();

        PromisedPayment promisedPayment = new PromisedPayment();
        promisedPayment.setUserData(userData);
        promisedPayment.setAmount(requireAmount(execution));
        promisedPayment.setAmountToRepay(getBigDecimal(execution, "amountToRepay"));
        promisedPayment.setStatus(PromisedPaymentStatus.ACTIVE);
        promisedPayment.setCreatedAt(now);
        promisedPayment.setDueDate(now.plusDays(3));

        PromisedPayment saved = promisedPaymentRepository.save(promisedPayment);
        execution.setVariable("promisedPaymentId", saved.getId());
        execution.setVariable("dueDate", saved.getDueDate().toString());
    }
}
