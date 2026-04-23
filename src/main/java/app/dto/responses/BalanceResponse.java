package app.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class BalanceResponse {
    private Long userDataId;
    private String accountNumber;
    private BigDecimal balance;
}
