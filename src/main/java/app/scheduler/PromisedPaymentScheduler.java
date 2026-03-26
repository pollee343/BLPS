package app.scheduler;

import app.model.enams.PromisedPaymentStatus;
import app.repositories.PromisedPaymentRepository;
import app.services.PromisedPaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PromisedPaymentScheduler {

    private final PromisedPaymentRepository promisedPaymentRepository;
    private final PromisedPaymentService promisedPaymentService;

    // каждый ЧАС проверяет просроченные обещанные платежи
    @Scheduled(fixedRate = 3_600_000)
    public void processOverduePromisedPayments() {
        List<Long> userIds = promisedPaymentRepository.findDistinctUserIdsWithDuePayments(
                List.of(PromisedPaymentStatus.ACTIVE, PromisedPaymentStatus.OVERDUE),
                LocalDateTime.now()
        );

        for (Long userId : userIds) {
            promisedPaymentService.processPromisedPayment(userId);
        }
    }
}
