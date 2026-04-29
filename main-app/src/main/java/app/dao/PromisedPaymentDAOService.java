package app.dao;

import app.model.enams.PromisedPaymentStatus;
import app.model.entities.PromisedPayment;
import app.model.entities.UserData;
import app.repositories.PromisedPaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PromisedPaymentDAOService {

    private final PromisedPaymentRepository paymentRepository;

    public BigDecimal sumAmountTakenForPeriod(Long userDataId,
                                              LocalDateTime periodStart,
                                              LocalDateTime periodEnd) {
        return paymentRepository.sumAmountTakenForPeriod(userDataId, periodStart, periodEnd);
    }

    public boolean existsByUserDataIdAndStatus(Long userDataId, PromisedPaymentStatus status) {
        return paymentRepository.existsByUserDataIdAndStatus(userDataId, status);
    }

    public List<PromisedPayment> findByUserDataIdAndStatusInAndDueDateLessThanEqualOrderByDueDateAscCreatedAtAsc(
            Long userDataId,
            Collection<PromisedPaymentStatus> statuses,
            LocalDateTime now
    ) {
        return paymentRepository.findByUserDataIdAndStatusInAndDueDateLessThanEqualOrderByDueDateAscCreatedAtAsc(userDataId, statuses, now);
    }

    public List<Long> findDistinctUserIdsWithDuePayments(Collection<PromisedPaymentStatus> statuses,
                                                         LocalDateTime now) {
        return paymentRepository.findDistinctUserIdsWithDuePayments(statuses, now);
    }

    public void save(PromisedPayment payment) {
        paymentRepository.save(payment);
    }

    public List<PromisedPayment> getByUserData(UserData userData){
        return paymentRepository.getByUserData(userData);
    }
}
