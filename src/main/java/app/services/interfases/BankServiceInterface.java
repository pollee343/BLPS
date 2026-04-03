package app.services.interfases;

import app.model.enams.BankOperationStatus;

import java.math.BigDecimal;

public interface BankServiceInterface {
    BankOperationStatus processPayment(String cardNumber, String cvc, BigDecimal amount);
}
