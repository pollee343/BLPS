package app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class BalanceResponse {
    private Long userDataId;
    private String accountNumber;
    private BigDecimal balance;
}
