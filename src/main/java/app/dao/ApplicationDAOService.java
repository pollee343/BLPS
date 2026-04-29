package app.dao;

import app.model.enams.ApplicationStatus;
import app.model.enams.ApplicationType;
import app.model.entities.Application;
import app.model.entities.UserData;
import app.repositories.ApplicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ApplicationDAOService {
    private final ApplicationRepository applicationRepository;

    public void createApplication(Application application) {
        applicationRepository.save(application);
    }

    public Optional<Application> findWaitingApplications(UserData userData, ApplicationType applicationType, ApplicationStatus applicationStatus) {
        return applicationRepository.findByUserDataAndApplicationTypeAndApplicationStatus(userData, applicationType, applicationStatus);
    }

    public List<Application> findAllWaitingApplicationsByApplicationType(ApplicationType applicationType, ApplicationStatus applicationStatus) {
        return applicationRepository.findByApplicationTypeAndApplicationStatus(applicationType, applicationStatus);
    }
}
