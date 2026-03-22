package app.controllers;

import app.dto.PromisedPaymentRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import app.services.PromisedPaymentService;

@RestController
@RequestMapping("/api/promised-payment")
@RequiredArgsConstructor
public class PromisedPaymentController {

    private final PromisedPaymentService promisedPaymentService;

    @PostMapping("/take")
    public ResponseEntity<?> takePromisedPayment(@RequestBody PromisedPaymentRequest request) {
        try {
            promisedPaymentService.takePromisedPayment(
                    request.getUserDataId(),
                    request.getAmount()
            );
            return ResponseEntity.ok("Обещанный платеж успешно подключен");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/process/{userDataId}")
    public ResponseEntity<?> processPromisedPayment(@PathVariable Long userDataId) {
        try {
            promisedPaymentService.processPromisedPayment(userDataId);
            return ResponseEntity.ok("Обработка обещанного платежа выполнена");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}