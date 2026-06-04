package app.services.interfases;

import app.dto.responses.ApplicationResponse;
import app.model.enams.ApplicationType;
import app.model.entities.UserData;

import java.util.List;
import java.util.Optional;

public interface ApplicationServiceInterface {
    void promisedPaymentRejection(String accountNumber, String email);
    void legallyReliableReport(String accountNumber, String email);
    List<ApplicationResponse> getAllPromisedPaymentRejectionApps();
    List<ApplicationResponse> getAllLegallyReliableRetortApps();
    boolean hasCreatedPromisedPaymentRejectionApplication(String accountNumber);
    Optional<String> findWaitingEmployeeApplicationEmail(String accountNumber, ApplicationType applicationType);
    void makeApplicationProcessed(String accountNumber, ApplicationType applicationType);
    void makeApplicationProcessed(UserData userData, ApplicationType applicationType);
}
