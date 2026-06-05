package app.services.interfases;

import app.dto.responses.PromisedPaymentDataResponse;

import java.util.List;

public interface PromisedPaymentServiceInterface {
    List<PromisedPaymentDataResponse> getPromisedPaymentRejectData(String accountNumber);
}
