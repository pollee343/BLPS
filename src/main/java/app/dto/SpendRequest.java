package app.dto;

import lombok.Data;

import java.math.BigDecimal;
@Data
public class SpendRequest {
    private BigDecimal amount;
    private Long userDataId;
    private String name;
}


