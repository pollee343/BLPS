package app.services.interfases;

import app.dto.ApplicationResponse;

import java.util.List;

public interface ApplicationServiceInterface {
    void promisedPaymentRejection(String accountNumber, String email);
    void legallyReliableReport(String accountNumber, String email);
    List<ApplicationResponse> getAllPromisedPaymentRejectionApps();
    List<ApplicationResponse> getAllLegallyReliableRetortApps();
}
