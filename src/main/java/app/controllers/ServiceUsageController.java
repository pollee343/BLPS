package app.controllers;

import app.model.entities.ServiceUsage;
import app.services.interfases.ServiceUsageServiceInterface;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/serviceUsage")
@RequiredArgsConstructor
public class ServiceUsageController {

    private final ServiceUsageServiceInterface serviceUsageService;

    @PostMapping("/createServiceUsage")
    public ResponseEntity<String> createServiceUsage(@Valid @RequestBody ServiceUsage serviceUsage) {
        serviceUsageService.createServiceUsage(serviceUsage);
        return ResponseEntity.status(HttpStatus.CREATED).body("Использование сервисов МТС успешно зарегистрировано");
    }
}
