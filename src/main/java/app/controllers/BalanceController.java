package app.controllers;


import app.dto.BalanceResponse;
import app.dto.PaymentRequest;
import app.dto.SpendRequest;
import app.model.enams.BankOperationStatus;
import app.services.interfases.BalanceServiceInterface;
import app.services.interfases.PromisedPaymentServiceInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/balance")
@RequiredArgsConstructor
@Log4j2
public class BalanceController {

    private final BalanceServiceInterface balanceService;
    private final PromisedPaymentServiceInterface promisedPaymentService;

    @PostMapping("/top-up")
    public ResponseEntity<?> topUp(@RequestBody PaymentRequest request) {
        try {
            BankOperationStatus status = balanceService.topUp(request);
            if (status == BankOperationStatus.SUCCESS) {
                promisedPaymentService.processPromisedPayment(request.getUserDataId());
            }
            return switch (status) {
                case SUCCESS -> ResponseEntity.ok("Баланс успешно пополнен");
                case DECLINED -> ResponseEntity.badRequest().body("Банк отклонил операцию (проверьте сумму и данные)");
                case ERROR -> ResponseEntity.internalServerError().body("Техническая ошибка банка");
            };
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{userDataId}")
    public ResponseEntity<?> getBalance(@PathVariable Long userDataId) {
        try {
            BalanceResponse response = balanceService.getBalance(userDataId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    //для заполнения бд операциями
    @PostMapping("/spend")
    public ResponseEntity<?> spend(@RequestBody SpendRequest request) {
        try {
            balanceService.spend(
                    request.getUserDataId(),
                    request.getAmount(),
                    request.getName()
            );
            return ResponseEntity.ok("Баланс успешно уменьшен");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
