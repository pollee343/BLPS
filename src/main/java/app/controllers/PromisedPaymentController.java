package app.controllers;

import app.dto.requests.PromisedPaymentRequest;
import app.services.interfases.PromisedPaymentServiceInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/promised-payment")
@RequiredArgsConstructor
@Log4j2
public class PromisedPaymentController {

    private final PromisedPaymentServiceInterface promisedPaymentService;

    @PreAuthorize("hasRole('USER')" +
            "&& @securityService.canAccessUserData(authentication, #request.getUserDataId())")
    @PostMapping("/take")
    public ResponseEntity<?> takePromisedPayment(@RequestBody PromisedPaymentRequest request) {
        promisedPaymentService.takePromisedPayment(
                request.getUserDataId(),
                request.getAmount()
        );
        return ResponseEntity.ok("Обещанный платеж успешно подключен");
    }

}
