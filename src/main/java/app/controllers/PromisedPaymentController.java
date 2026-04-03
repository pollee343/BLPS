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
        promisedPaymentService.takePromisedPayment(
                request.getUserDataId(),
                request.getAmount()
        );
        return ResponseEntity.ok("Обещанный платеж успешно подключен");
    }

}
