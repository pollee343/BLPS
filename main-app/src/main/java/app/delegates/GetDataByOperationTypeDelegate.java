package app.delegates;

import app.dto.responses.ExpensesResponse;
import app.model.enams.OperationType;
import app.services.interfases.ExpensesServiceInterface;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component("getDataByOperationTypeDelegate")
@RequiredArgsConstructor
public class GetDataByOperationTypeDelegate implements JavaDelegate {

    private static final Logger log = LoggerFactory.getLogger(GetDataByOperationTypeDelegate.class);

    private final ExpensesServiceInterface expensesService;
    private final ObjectMapper objectMapper;

    @Override
    public void execute(DelegateExecution execution) {
        log.info("GetDataByOperationTypeDelegate start: processInstanceId={}, activityId={}",
                execution.getProcessInstanceId(),
                execution.getCurrentActivityId());

        String accountNumber = (String) execution.getVariable("accountNumber");
        LocalDate from = CamundaDateValueReader.readLocalDate(
                execution.getVariable("from"), "BAD_REQUEST", "Некорректный формат даты начала");
        LocalDate to = CamundaDateValueReader.readLocalDate(
                execution.getVariable("to"), "BAD_REQUEST", "Некорректный формат даты окончания");
        OperationType operationType = readOperationType(execution.getVariable("operationType"));

        log.info("GetDataByOperationTypeDelegate input: processInstanceId={}, accountNumber={}, from={}, to={}, operationType={}",
                execution.getProcessInstanceId(),
                accountNumber,
                from,
                to,
                operationType);

        if (accountNumber == null || accountNumber.isBlank()) {
            execution.setVariable("errorMessage", "accountNumber is required");
            throw new BpmnError("BAD_REQUEST", "accountNumber is required");
        }
        if (operationType == null) {
            execution.setVariable("errorMessage", "operationType is required");
            throw new BpmnError("BAD_REQUEST", "operationType is required");
        }

        List<ExpensesResponse> expenses =
                expensesService.getExpensesForPeriodAndOperationType(accountNumber, from, to, operationType);
        execution.setVariable("expenses", expenses);
        try {
            execution.setVariable("expensesJson", objectMapper.writeValueAsString(expenses));
        } catch (JsonProcessingException e) {
            execution.setVariable("errorMessage", "Не удалось сериализовать результат");
            throw new BpmnError("BAD_REQUEST", "Не удалось сериализовать результат");
        }

        log.info("GetDataByOperationTypeDelegate success: processInstanceId={}, expensesCount={}, expensesJsonLength={}",
                execution.getProcessInstanceId(),
                expenses == null ? null : expenses.size(),
                ((String) execution.getVariable("expensesJson")) == null ? null : ((String) execution.getVariable("expensesJson")).length());
    }

    private OperationType readOperationType(Object value) {
        if (value == null) return null;
        if (value instanceof OperationType ot) return ot;
        if (value instanceof String s) {
            if (s.isBlank() || "ANY".equals(s)) return null;
            return OperationType.valueOf(s);
        }
        throw new BpmnError("BAD_REQUEST", "Некорректный operationType");
    }
}
