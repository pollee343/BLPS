package repositories;

import model.entities.MoneyOperation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MoneyOperationRepository extends JpaRepository<MoneyOperation, Long> {

    List<MoneyOperation> findByUserDataId(Long userDataId);
}