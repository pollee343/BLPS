package app.services.interfases;

import app.dto.responses.ApplicationResponse;
import app.model.enams.ApplicationType;
import app.model.entities.UserData;

import java.util.List;

public interface ApplicationServiceInterface {
    void promisedPaymentRejection(String accountNumber, String email);
    void legallyReliableReport(String accountNumber, String email);
    List<ApplicationResponse> getAllPromisedPaymentRejectionApps();
    List<ApplicationResponse> getAllLegallyReliableRetortApps();
    void makeApplicationProcessed(UserData userData, ApplicationType applicationType);
}
