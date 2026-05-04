package app.dao;

import app.model.enams.ApplicationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReportSendingBodyRequest {
    private String accountNumber;
    private ApplicationType applicationType;
}
