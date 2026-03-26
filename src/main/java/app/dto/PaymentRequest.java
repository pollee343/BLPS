package app.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentRequest {
    private String cardNumber;
    private String cvc;
    private BigDecimal amount;
    private Long userDataId;}
