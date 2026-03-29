package app.repositories;

import app.model.enams.PromisedPaymentStatus;
import app.model.entities.PromisedPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Repository
public interface PromisedPaymentRepository extends JpaRepository<PromisedPayment, Long> {

    @Query("""
            SELECT COALESCE(SUM(p.amount), 0)
            FROM PromisedPayment p
            WHERE p.userData.id = :userDataId
              AND p.createdAt >= :periodStart
              AND p.createdAt < :periodEnd
            """)
    BigDecimal sumAmountTakenForPeriod(@Param("userDataId") Long userDataId,
                                       @Param("periodStart") LocalDateTime periodStart,
                                       @Param("periodEnd") LocalDateTime periodEnd);

    boolean existsByUserDataIdAndStatus(Long userDataId, PromisedPaymentStatus status);

    List<PromisedPayment> findByUserDataIdAndStatusInAndDueDateLessThanEqualOrderByDueDateAscCreatedAtAsc(
            Long userDataId,
            Collection<PromisedPaymentStatus> statuses,
            LocalDateTime now
    );

    @Query("""
            SELECT DISTINCT p.userData.id
            FROM PromisedPayment p
            WHERE p.status IN :statuses
              AND p.dueDate <= :now
            """)
    List<Long> findDistinctUserIdsWithDuePayments(@Param("statuses") Collection<PromisedPaymentStatus> statuses,
                                                  @Param("now") LocalDateTime now);
}
