package app.services.interfases;

import java.math.BigDecimal;

public interface PromisedPaymentServiceInterface {
    void takePromisedPayment(Long userDataId, BigDecimal amount);
    void processPromisedPayment(Long userDataId);
}
