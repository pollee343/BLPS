package app.dto.requests;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApplicationRequest {
    private String accountNumber;
    @Email
    private String email;
}
