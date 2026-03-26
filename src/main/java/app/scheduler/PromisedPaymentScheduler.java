package app.scheduler;

import app.model.entities.UserData;
import app.repositories.UserDataRepository;
import app.services.PromisedPaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PromisedPaymentScheduler {

    private final UserDataRepository userDataRepository;
    private final PromisedPaymentService promisedPaymentService;

    // каждый ЧАС проверяет просроченные обещанные платежи
    @Scheduled(fixedRate = 3_600_000)
    public void processOverduePromisedPayments() {
        List<UserData> overdue = userDataRepository
                .findAllPromisedPaymentTimedOut(LocalDateTime.now());

        for (UserData userData : overdue) {
            promisedPaymentService.processPromisedPayment(userData.getId());
        }
    }
}
