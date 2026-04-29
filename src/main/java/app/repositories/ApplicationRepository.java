package app.repositories;

import app.model.enams.ApplicationStatus;
import app.model.enams.ApplicationType;
import app.model.entities.Application;
import app.model.entities.UserData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long>  {
    Optional<Application> findByUserDataAndApplicationTypeAndApplicationStatus(UserData userData, ApplicationType applicationType, ApplicationStatus status);
    List<Application> findByApplicationTypeAndApplicationStatus(ApplicationType applicationType, ApplicationStatus status);
}
