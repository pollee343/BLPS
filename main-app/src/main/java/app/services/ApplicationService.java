package app.services;

import app.dao.ApplicationDAOService;
import app.dao.UserDataDAOService;
import app.dto.responses.ApplicationResponse;
import app.model.enams.ApplicationStatus;
import app.model.enams.ApplicationType;
import app.model.entities.Application;
import app.model.entities.UserData;
import app.services.interfases.ApplicationServiceInterface;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApplicationService implements ApplicationServiceInterface {

    private final ApplicationDAOService applicationDAOService;
    private final UserDataDAOService userDataDAOService;

    @Override
    public void promisedPaymentRejection(String accountNumber, String email) {
        UserData userData = userDataDAOService.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        if (applicationDAOService.findWaitingApplications(userData, ApplicationType.PROMISED_PAYMENT_REJECTION, ApplicationStatus.CREATED)
                .isPresent()) {
            throw new IllegalArgumentException("Заявка на получение информации об отказе в получении обещанного платежа уже создана");
        }
        createApplication(email, ApplicationType.PROMISED_PAYMENT_REJECTION, userData);
    }

    @Override
    public void legallyReliableReport(String accountNumber, String email) {
        UserData userData = userDataDAOService.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        if (applicationDAOService.findWaitingApplications(userData, ApplicationType.LEGALLY_RELIABLE_REPORT, ApplicationStatus.CREATED)
                .isPresent()) {
            throw new IllegalArgumentException("Заявка на получение юридически достоверного отчета уже создана");
        }
        createApplication(email, ApplicationType.LEGALLY_RELIABLE_REPORT, userData);
    }

    @Override
    public List<ApplicationResponse> getAllPromisedPaymentRejectionApps() {
        return applicationDAOService.findAllWaitingApplicationsByApplicationType(ApplicationType.PROMISED_PAYMENT_REJECTION, ApplicationStatus.CREATED)
                .stream()
                .map(this::buildApplicationResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ApplicationResponse> getAllLegallyReliableRetortApps() {
        return applicationDAOService.findAllWaitingApplicationsByApplicationType(ApplicationType.LEGALLY_RELIABLE_REPORT, ApplicationStatus.CREATED)
                .stream()
                .map(this::buildApplicationResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void makeApplicationProcessed(UserData userData, ApplicationType applicationType) {
        Application application = applicationDAOService.findWaitingApplications(userData, applicationType, ApplicationStatus.CREATED)
                .orElseThrow(() -> new EntityNotFoundException("Не найдены необработанные заявки"));
        application.setApplicationStatus(ApplicationStatus.PROCESSED);
        applicationDAOService.createApplication(application);
    }

    private void createApplication(String email, ApplicationType applicationType, UserData userData) {
        Application application = new Application();
        application.setApplicationType(applicationType);
        application.setEmail(email);
        application.setUserData(userData);
        applicationDAOService.createApplication(application);
    }

    private ApplicationResponse buildApplicationResponse(Application application) {
        return new ApplicationResponse(application.getUserData().getAccountNumber(), application.getEmail());
    }
}
