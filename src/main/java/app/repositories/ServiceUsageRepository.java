package app.repositories;

import app.model.entities.ServiceUsage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ServiceUsageRepository extends JpaRepository<ServiceUsage, Long> {
    List<ServiceUsage> findByUserDataIdAndOperationTimeBetween(Long userDataId, LocalDateTime from, LocalDateTime to);
}
