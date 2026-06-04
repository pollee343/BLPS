package app.delegates;

import app.dto.responses.ExpensesResponse;
import app.services.interfases.ExpensesServiceInterface;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component("filterDataByOperationNameDelegate")
@RequiredArgsConstructor
public class FilterDataByOperationNameDelegate implements JavaDelegate {

    private static final Logger log = LoggerFactory.getLogger(FilterDataByOperationNameDelegate.class);

    private final ExpensesServiceInterface expensesService;
    private final ObjectMapper objectMapper;

    @Override
    public void execute(DelegateExecution execution) {
        log.info("FilterDataByOperationNameDelegate start: processInstanceId={}, activityId={}",
                execution.getProcessInstanceId(),
                execution.getCurrentActivityId());

        String accountNumber = (String) execution.getVariable("accountNumber");
        LocalDate from = CamundaDateValueReader.readLocalDate(
                execution.getVariable("from"), "BAD_REQUEST", "Некорректный формат даты начала");
        LocalDate to = CamundaDateValueReader.readLocalDate(
                execution.getVariable("to"), "BAD_REQUEST", "Некорректный формат даты окончания");
        String operationName = (String) execution.getVariable("operationName");

        log.info("FilterDataByOperationNameDelegate input: processInstanceId={}, accountNumber={}, from={}, to={}, operationName={}",
                execution.getProcessInstanceId(),
                accountNumber,
                from,
                to,
                operationName);

        if (accountNumber == null || accountNumber.isBlank()) {
            execution.setVariable("errorMessage", "accountNumber is required");
            throw new BpmnError("BAD_REQUEST", "accountNumber is required");
        }
        if (operationName == null || operationName.isBlank()) {
            execution.setVariable("errorMessage", "operationName is required");
            throw new BpmnError("BAD_REQUEST", "operationName is required");
        }

        List<ExpensesResponse> expenses;
        try {
            expenses = expensesService.getForPeriodAndOperationName(accountNumber, from, to, operationName);
        } catch (EntityNotFoundException e) {
            execution.setVariable("errorMessage", e.getMessage());
            throw new BpmnError("NOT_FOUND", e.getMessage());
        }
        execution.setVariable("expenses", expenses);
        try {
            execution.setVariable("expensesJson", objectMapper.writeValueAsString(expenses));
        } catch (JsonProcessingException e) {
            execution.setVariable("errorMessage", "Не удалось сериализовать результат");
            throw new BpmnError("BAD_REQUEST", "Не удалось сериализовать результат");
        }

        log.info("FilterDataByOperationNameDelegate success: processInstanceId={}, expensesCount={}, expensesJsonLength={}",
                execution.getProcessInstanceId(),
                expenses == null ? null : expenses.size(),
                ((String) execution.getVariable("expensesJson")) == null ? null : ((String) execution.getVariable("expensesJson")).length());
    }
}
