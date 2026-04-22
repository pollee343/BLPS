package app.dto;

import app.model.enams.PromisedPaymentStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class PromisedPaymentDataResponse {
    private BigDecimal amount;
    private BigDecimal amountToRepay;
    private PromisedPaymentStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime dueDate;
    private LocalDateTime repaidAt;
}
