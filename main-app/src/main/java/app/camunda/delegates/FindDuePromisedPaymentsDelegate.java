package app.camunda.delegates;

import app.dao.PromisedPaymentDAOService;
import app.model.enams.PromisedPaymentStatus;
import app.model.entities.PromisedPayment;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component("findDuePromisedPaymentsDelegate")
@RequiredArgsConstructor
public class FindDuePromisedPaymentsDelegate extends PromisedPaymentProcessVariables implements JavaDelegate {

    private final PromisedPaymentDAOService promisedPaymentDAOService;

    @Override
    public void execute(DelegateExecution execution) {
        Long userDataId = getLong(execution, "userDataId");
        if (userDataId == null) {
            execution.setVariable("hasPaymentsToProcess", false);
            execution.setVariable("hasOverdueAfterProcessing", false);
            return;
        }

        List<Long> processedPaymentIds = getProcessedPaymentIds(execution);
        List<PromisedPayment> payments = promisedPaymentDAOService
                .findByUserDataIdAndStatusInAndDueDateLessThanEqualOrderByDueDateAscCreatedAtAsc(
                        userDataId,
                        List.of(PromisedPaymentStatus.ACTIVE, PromisedPaymentStatus.OVERDUE),
                        LocalDateTime.now()
                );
        PromisedPayment payment = payments.stream()
                .filter(candidate -> !processedPaymentIds.contains(candidate.getId()))
                .findFirst()
                .orElse(null);
        boolean hasPaymentsToProcess = payment != null;
        execution.setVariable("hasPaymentsToProcess", hasPaymentsToProcess);

        if (hasPaymentsToProcess) {
            processedPaymentIds.add(payment.getId());
            execution.setVariable("processedPaymentIds", processedPaymentIds);
            execution.setVariable("promisedPaymentId", payment.getId());
            execution.setVariable("amountToRepay", payment.getAmountToRepay());
            execution.setVariable("paymentWasActive", payment.getStatus() == PromisedPaymentStatus.ACTIVE);
            return;
        }

        boolean hasOverdueAfterProcessing = promisedPaymentDAOService.existsByUserDataIdAndStatus(
                userDataId,
                PromisedPaymentStatus.OVERDUE
        );
        execution.setVariable("hasOverdueAfterProcessing", hasOverdueAfterProcessing);
    }

    private List<Long> getProcessedPaymentIds(DelegateExecution execution) {
        Object value = execution.getVariable("processedPaymentIds");
        if (!(value instanceof List<?> values)) {
            return new ArrayList<>();
        }
        List<Long> ids = new ArrayList<>();
        values.stream()
                .filter(Objects::nonNull)
                .map(item -> item instanceof Number number ? number.longValue() : Long.valueOf(item.toString()))
                .forEach(ids::add);
        return ids;
    }
}
