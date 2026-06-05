package app.controllers;

import app.dto.responses.BalanceResponse;
import app.dto.requests.PaymentRequest;
import app.dto.requests.SpendRequest;
import app.model.enams.BankOperationStatus;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.history.HistoricVariableInstance;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import app.services.BalanceService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/balance")
@RequiredArgsConstructor
public class BalanceController {

    private final BalanceService balanceService;
    private final RuntimeService runtimeService;
    private final TaskService taskService;
    private final HistoryService historyService;

    @PostMapping("/top-up")
    public ResponseEntity<?> topUp(@RequestBody PaymentRequest request) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("userDataId", request.getUserDataId());
        variables.put("cardNumber", request.getCardNumber());
        variables.put("cvc", request.getCvc());
        variables.put("amount", request.getAmount());

        ProcessInstance processInstance = runtimeService
                .createProcessInstanceByKey("balance_top_up_process")
                .setVariables(variables)
                .execute();

        Task fillDataTask = taskService.createTaskQuery()
                .processInstanceId(processInstance.getId())
                .taskDefinitionKey("fill_balance_top_up_data")
                .singleResult();
        if (fillDataTask != null) {
            taskService.complete(fillDataTask.getId(), variables);
        }

        Boolean topUpDataValid = (Boolean) readProcessVariable(processInstance.getId(), "topUpDataValid");
        if (Boolean.FALSE.equals(topUpDataValid)) {
            return ResponseEntity.badRequest().body("Некорректные данные платежа");
        }

        BankOperationStatus status = BankOperationStatus.valueOf((String) readProcessVariable(processInstance.getId(), "bankPaymentStatus"));
        return switch (status) {
            case SUCCESS -> ResponseEntity.ok("Баланс успешно пополнен");
            case DECLINED -> ResponseEntity.badRequest().body("Банк отклонил операцию (проверьте сумму и данные)");
            case ERROR -> ResponseEntity.status(502).body("Техническая ошибка банка");
        };
    }

    @GetMapping("/{userDataId}")
    public ResponseEntity<?> getBalance(@PathVariable Long userDataId) {
        BalanceResponse response = balanceService.getBalance(userDataId);
        return ResponseEntity.ok(response);
    }

    //для заполнения бд операциями
    @PostMapping("/spend")
    public ResponseEntity<?> spend(@RequestBody SpendRequest request) {
        balanceService.spend(
                request.getUserDataId(),
                request.getAmount(),
                request.getName()
        );
        return ResponseEntity.ok("Баланс успешно уменьшен");
    }

    private Object readProcessVariable(String processInstanceId, String variableName) {
        ProcessInstance activeInstance = runtimeService.createProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .singleResult();
        if (activeInstance != null) {
            return runtimeService.getVariable(processInstanceId, variableName);
        }

        HistoricVariableInstance variableInstance = historyService.createHistoricVariableInstanceQuery()
                .processInstanceId(processInstanceId)
                .variableName(variableName)
                .singleResult();
        return variableInstance != null ? variableInstance.getValue() : null;
    }

}
