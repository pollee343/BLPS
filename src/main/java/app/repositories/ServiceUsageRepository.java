package app.repositories;

import app.model.entities.ServiceUsage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceUsageRepository extends JpaRepository<ServiceUsage, Long> {
}
