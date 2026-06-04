package app.dto.responses;

import lombok.Data;
import lombok.experimental.Accessors;
import app.model.enams.OperationType;

import java.math.BigDecimal;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class ExpensesResponse implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String accountNumber;
    private LocalDateTime MoneyOperationTime;
    private String MoneyOperationName;
    private OperationType MoneyOperationType;
    private BigDecimal MoneyOperationAmount;

}
