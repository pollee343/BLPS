package app.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BalanceRequest {
    private Long userDataId;
    private BigDecimal amount;
}