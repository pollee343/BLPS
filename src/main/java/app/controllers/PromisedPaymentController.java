package app.controllers;

import app.dto.PromisedPaymentRequest;
import app.services.interfases.PromisedPaymentServiceInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/promised-payment")
@RequiredArgsConstructor
@Log4j2
public class PromisedPaymentController {

    private final PromisedPaymentServiceInterface promisedPaymentService;

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

}