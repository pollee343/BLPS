package app.controllers;

import app.dto.requests.PromisedPaymentRequest;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.ProcessInstanceWithVariables;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/promised-payment")
@RequiredArgsConstructor
public class PromisedPaymentController {

    private final RuntimeService runtimeService;

    @PostMapping("/take")
    public ResponseEntity<?> takePromisedPayment(@RequestBody PromisedPaymentRequest request) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("userDataId", request.getUserDataId());
        variables.put("amount", request.getAmount());

        ProcessInstanceWithVariables result = runtimeService
                .createProcessInstanceByKey("promised_payment_process")
                .setVariables(variables)
                .executeWithVariablesInReturn();

        if (Boolean.FALSE.equals(result.getVariables().get("accessAllowed"))) {
            return ResponseEntity.status(403).body("Доступ запрещен");
        }
        if (Boolean.FALSE.equals(result.getVariables().get("amountValid"))) {
            return ResponseEntity.badRequest().body("Сумма обещанного платежа должна быть положительной и не больше 1500 руб.");
        }
        if (Boolean.TRUE.equals(result.getVariables().get("hasOverduePayment"))) {
            return ResponseEntity.badRequest().body("Нельзя взять новый обещанный платеж, пока не погашена просроченная задолженность");
        }
        if (Boolean.TRUE.equals(result.getVariables().get("monthlyLimitExceeded"))) {
            BigDecimal monthlyTaken = (BigDecimal) result.getVariables().get("monthlyTaken");
            BigDecimal available = BigDecimal.valueOf(3000).subtract(monthlyTaken).max(BigDecimal.ZERO);
            return ResponseEntity.badRequest().body("Превышен месячный лимит обещанных платежей. Доступно: " + available + " руб.");
        }

        return ResponseEntity.ok("Обещанный платеж успешно подключен");
    }

}
