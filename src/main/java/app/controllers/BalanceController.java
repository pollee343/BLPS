package app.controllers;


import app.dto.PaymentRequest;
import app.model.enams.BankOperationStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import app.services.BalanceService;
import app.services.PromisedPaymentService;

@RestController
@RequestMapping("/api/balance")
@RequiredArgsConstructor
public class BalanceController {

    private final BalanceService balanceService;
    private final PromisedPaymentService promisedPaymentService;

    @PostMapping("/top-up")
    public ResponseEntity<?> topUp(@RequestBody PaymentRequest request) {
        try {
            BankOperationStatus status = balanceService.topUp(request);
            if (status == BankOperationStatus.SUCCESS) {
                promisedPaymentService.processPromisedPayment(request.getUserDataId());
            }
            return switch (status) {
                case SUCCESS -> ResponseEntity.ok("Баланс успешно пополнен");
                case DECLINED -> ResponseEntity.badRequest().body("Банк отклонил операцию");
                case ERROR -> ResponseEntity.internalServerError().body("Техническая ошибка банка");
            };
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}