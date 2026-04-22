package app.controllers;

import app.dto.ApplicationRequest;
import app.dto.ApplicationResponse;
import app.services.interfases.ApplicationServiceInterface;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/application")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationServiceInterface applicationService;

    @PostMapping("/promisedPaymentRejection")
    public ResponseEntity<String> promisedPaymentRejection(@Valid @RequestBody ApplicationRequest applicationRequest,
                                                           @AuthenticationPrincipal Jwt jwt) {
        applicationService.promisedPaymentRejection(applicationRequest.getAccountNumber(), applicationRequest.getEmail());
        return ResponseEntity.ok("Заявка на получение информации об отказе в получении обещанного платежа успешно получена");
    }

    @PreAuthorize("hasRole('MODERATOR')")
    @GetMapping("/getAllPromisedPaymentRejectionApps")
    public List<ApplicationResponse> getAllPromisedPaymentRejectionApps() {
        return applicationService.getAllPromisedPaymentRejectionApps();
    }

    @PostMapping("/legallyReliableReport")
    public ResponseEntity<String> legallyReliableReport(@Valid @RequestBody ApplicationRequest applicationRequest){
        applicationService.legallyReliableReport(applicationRequest.getAccountNumber(), applicationRequest.getEmail());
        return ResponseEntity.ok("Заявка на получение юридически достоверного отчета успешно получена");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/getAllLegallyReliableRetortApps")
    public List<ApplicationResponse> getAllLegallyReliableRetortApps(){
        return applicationService.getAllLegallyReliableRetortApps();
    }
}
