package app.services.interfases;

import app.dto.PromisedPaymentDataResponse;

import java.math.BigDecimal;
import java.util.List;

public interface PromisedPaymentServiceInterface {
    void takePromisedPayment(Long userDataId, BigDecimal amount);
    void processPromisedPayment(Long userDataId);
    List<PromisedPaymentDataResponse> getPromisedPaymentRejectData(String accountNumber);
}
