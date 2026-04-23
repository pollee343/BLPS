package app.services.interfases;

import app.dto.responses.BalanceResponse;
import app.dto.requests.PaymentRequest;
import app.model.enams.BankOperationStatus;

import java.math.BigDecimal;

public interface BalanceServiceInterface {
    BankOperationStatus topUp(PaymentRequest request);
    void spend(Long userDataId, BigDecimal amount, String name);
    BalanceResponse getBalance(Long userDataId);
}
