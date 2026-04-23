package app.dto.responses;

import lombok.Data;
import lombok.experimental.Accessors;
import app.model.enams.OperationType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class ExpensesResponse {
    private String accountNumber;
    private LocalDateTime MoneyOperationTime;
    private String MoneyOperationName;
    private OperationType MoneyOperationType;
    private BigDecimal MoneyOperationAmount;

}
