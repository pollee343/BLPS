package app.services.interfases;

import app.dto.responses.BalanceResponse;

import java.math.BigDecimal;

public interface BalanceServiceInterface {
    void spend(Long userDataId, BigDecimal amount, String name);
    BalanceResponse getBalance(Long userDataId);
}
