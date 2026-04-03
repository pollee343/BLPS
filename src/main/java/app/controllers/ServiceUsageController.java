package app.controllers;

import app.dto.MessageOnlyResponse;
import app.model.entities.ServiceUsage;
import app.repositories.ServiceUsageRepository;
import app.services.ServiceUsageService;
import app.services.interfases.ServiceUsageServiceInterface;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/serviceUsage")
@RequiredArgsConstructor
@Log4j2
public class ServiceUsageController {

    private final ServiceUsageServiceInterface serviceUsageService;

    @PostMapping("/createServiceUsage")
    public ResponseEntity<MessageOnlyResponse> createServiceUsage(@Valid @RequestBody ServiceUsage serviceUsage) {
        serviceUsageService.createServiceUsage(serviceUsage);
        return ResponseEntity.status(HttpStatus.CREATED).body(new MessageOnlyResponse("Использование сервисов МТС успешно зарегистрировано"));
    }
}
