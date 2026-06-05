package app.camunda.delegates;

import app.dao.PromisedPaymentDAOService;
import app.dao.UserDataDAOService;
import app.model.enams.PromisedPaymentStatus;
import app.model.entities.UserData;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component("findUserDataForPromisedPaymentProcessingDelegate")
@RequiredArgsConstructor
public class FindUserDataForPromisedPaymentProcessingDelegate extends PromisedPaymentProcessVariables implements JavaDelegate {

    private final UserDataDAOService userDataDAOService;
    private final PromisedPaymentDAOService promisedPaymentDAOService;

    @Override
    public void execute(DelegateExecution execution) {
        Long userDataId = getLong(execution, "userDataId");
        if (userDataId == null) {
            List<Long> dueUserIds = promisedPaymentDAOService.findDistinctUserIdsWithDuePayments(
                    List.of(PromisedPaymentStatus.ACTIVE, PromisedPaymentStatus.OVERDUE),
                    LocalDateTime.now()
            );
            if (dueUserIds.isEmpty()) {
                execution.setVariable("hasPaymentsToProcess", false);
                return;
            }
            userDataId = dueUserIds.get(0);
            execution.setVariable("userDataId", userDataId);
        }

        UserData userData = userDataDAOService.findById(userDataId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));
        execution.setVariable("userBlockedBeforeProcessing", userData.getIsBlocked());
    }
}
