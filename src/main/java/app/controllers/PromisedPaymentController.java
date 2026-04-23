package app.controllers;

import app.dto.PromisedPaymentRequest;
import app.services.interfases.PromisedPaymentServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/promised-payment")
@RequiredArgsConstructor
public class PromisedPaymentController {

    private final PromisedPaymentServiceInterface promisedPaymentService;

    @PostMapping("/take")
    public ResponseEntity<?> takePromisedPayment(@RequestBody PromisedPaymentRequest request) {
        promisedPaymentService.takePromisedPayment(
                request.getUserDataId(),
                request.getAmount()
        );
        return ResponseEntity.ok("Обещанный платеж успешно подключен");
    }

}
