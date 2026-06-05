package app.camunda.delegates;

import app.model.enams.PromisedPaymentStatus;
import app.model.entities.PromisedPayment;
import app.repositories.PromisedPaymentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component("markPromisedPaymentOverdueDelegate")
@RequiredArgsConstructor
public class MarkPromisedPaymentOverdueDelegate extends PromisedPaymentProcessVariables implements JavaDelegate {

    private final PromisedPaymentRepository promisedPaymentRepository;

    @Override
    @Transactional
    public void execute(DelegateExecution execution) {
        PromisedPayment payment = promisedPaymentRepository.findById(getLong(execution, "promisedPaymentId"))
                .orElseThrow(() -> new EntityNotFoundException("Обещанный платеж не найден"));
        payment.setStatus(PromisedPaymentStatus.OVERDUE);
        promisedPaymentRepository.save(payment);
    }
}
