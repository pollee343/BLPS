package app.dao;

import app.model.enams.ApplicationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReportSendingBodyRequest {
    private String accountNumber;
    private ApplicationType applicationType;
    private MultipartFile file;
}
