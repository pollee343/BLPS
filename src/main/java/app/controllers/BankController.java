package app.controllers;

import app.dto.PaymentRequest;
import app.model.enams.BankOperationStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import app.services.BankService;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/bank")
@RequiredArgsConstructor
//мне кажется не нужен тк работа с банком идет только через другие процессы т.е. напрямую он никак не должен вызываться

public class BankController {

    private final BankService bankService;

    @PostMapping("/pay")
    public ResponseEntity<?> processPayment(@RequestBody PaymentRequest request) {
        BankOperationStatus status = bankService.processPayment(
                request.getCardNumber(),
                request.getCvc(),
                request.getAmount(),
                request.getUserDataId()
        );

        return switch (status) {
            case SUCCESS -> ResponseEntity.ok("Платеж успешно выполнен");
            case DECLINED -> ResponseEntity.badRequest().body("Банк отклонил операцию");
            case ERROR -> ResponseEntity.internalServerError().body("Техническая ошибка банка");
        };
    }
}