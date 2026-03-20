package app.repositories;

import app.model.entities.MoneyOperation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface MoneyOperationRepository extends JpaRepository<MoneyOperation, Long> {

    List<MoneyOperation> findByUserDataId(Long userDataId);

    List<MoneyOperation> findByUserDataIdAndOperationTimeBetween(Long userDataId, LocalDateTime from, LocalDateTime to);
}