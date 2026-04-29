package app.controllers;

import app.dto.requests.ApplicationRequest;
import app.dto.responses.ApplicationResponse;
import app.services.interfases.ApplicationServiceInterface;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/application")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationServiceInterface applicationService;

    @PreAuthorize("hasRole('USER') " +
            "&& @securityService.canAccessAccountNumber(authentication, #applicationRequest.getAccountNumber)")
    @PostMapping("/promisedPaymentRejection")
    public ResponseEntity<String> promisedPaymentRejection(@Valid @RequestBody ApplicationRequest applicationRequest) {
        applicationService.promisedPaymentRejection(applicationRequest.getAccountNumber(), applicationRequest.getEmail());
        return ResponseEntity.ok("Заявка на получение информации об отказе в получении обещанного платежа успешно получена");
    }

    @PreAuthorize("hasRole('MODERATOR')")
    @GetMapping("/getAllPromisedPaymentRejectionApps")
    public List<ApplicationResponse> getAllPromisedPaymentRejectionApps() {
        return applicationService.getAllPromisedPaymentRejectionApps();
    }

    @PreAuthorize("(hasRole('USER') || hasRole('ADMIN')) " +
            "&& @securityService.canAccessAccountNumber(authentication, #applicationRequest.getAccountNumber)")
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
