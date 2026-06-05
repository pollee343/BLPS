package app.scheduler;

import app.model.enams.PromisedPaymentStatus;
import app.repositories.PromisedPaymentRepository;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.RuntimeService;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class PromisedPaymentScheduler {

    private final PromisedPaymentRepository promisedPaymentRepository;
    private final RuntimeService runtimeService;

    // каждый ЧАС проверяет просроченные обещанные платежи
    // @Scheduled(fixedRate = 3_600_000)
    public void processOverduePromisedPayments() {
        List<Long> userIds = promisedPaymentRepository.findDistinctUserIdsWithDuePayments(
                List.of(PromisedPaymentStatus.ACTIVE.name(), PromisedPaymentStatus.OVERDUE.name()),
                LocalDateTime.now()
        );

        for (Long userId : userIds) {
            runtimeService.startProcessInstanceByMessage(
                    "PromisedPaymentDueCheck",
                    Map.of("userDataId", userId)
            );
        }
    }
}
