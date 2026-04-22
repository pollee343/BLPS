package app.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class RegistrationRequest {

    @NotBlank
    private String username;

    @NotBlank
    @Size(min = 3, max = 18)
    private String password;

    @NotBlank
    private String roleName;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    private LocalDate birthDate;

    @NotBlank
    private String passportSeries;

    @NotBlank
    private String passportNumber;

    @NotBlank
    private String accountNumber;

    @NotBlank
    private String phoneNumber;

    private BigDecimal balance;

    private Integer remainingSeconds;

    private Long remainingBytes;

    private Integer remainingSms;


}
