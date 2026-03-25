package app.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class IncreaseRemainingResponse {
    private String accountNumber;
    @Positive
    @NotNull
    private Integer remainingSeconds;
    @Positive
    @NotNull
    private Integer remainingSms;
    @Positive
    @NotNull
    private Integer remainingBytes;

}
