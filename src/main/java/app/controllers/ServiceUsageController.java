package app.controllers;

import app.dto.MessageOnlyResponse;
import app.model.entities.ServiceUsage;
import app.repositories.ServiceUsageRepository;
import app.services.ServiceUsageService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/serviceUsage")
public class ServiceUsageController {

    @Autowired
    private ServiceUsageService serviceUsageService;

    @PostMapping("/createServiceUsage")
    public ResponseEntity<MessageOnlyResponse> createServiceUsage(@Valid @RequestBody ServiceUsage serviceUsage) {
        serviceUsageService.createServiceUsage(serviceUsage);
        return ResponseEntity.status(HttpStatus.CREATED).body(new MessageOnlyResponse("Использование сервисов МТС успешно зарегистрировано"));
    }
}
