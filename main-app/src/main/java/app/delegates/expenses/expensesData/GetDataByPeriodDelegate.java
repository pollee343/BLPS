package app.delegates.expenses.expensesData;

import app.delegates.expenses.CamundaDateValueReader;
import app.dto.responses.ExpensesResponse;
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

@Component("getDataByPeriodDelegate")
@RequiredArgsConstructor
public class GetDataByPeriodDelegate implements JavaDelegate {

    private static final Logger log = LoggerFactory.getLogger(GetDataByPeriodDelegate.class);

    private final ExpensesServiceInterface expensesService;
    private final ObjectMapper objectMapper;

    @Override
    public void execute(DelegateExecution execution) {
        log.info("GetDataByPeriodDelegate start: processInstanceId={}, activityId={}",
                execution.getProcessInstanceId(),
                execution.getCurrentActivityId());

        String accountNumber = (String) execution.getVariable("accountNumber");
        LocalDate from = CamundaDateValueReader.readLocalDate(
                execution.getVariable("from"), "BAD_REQUEST", "Некорректный формат даты начала");
        LocalDate to = CamundaDateValueReader.readLocalDate(
                execution.getVariable("to"), "BAD_REQUEST", "Некорректный формат даты окончания");

        log.info("GetDataByPeriodDelegate input: processInstanceId={}, accountNumber={}, from={}, to={}",
                execution.getProcessInstanceId(),
                accountNumber,
                from,
                to);

        if (accountNumber == null || accountNumber.isBlank()) {
            execution.setVariable("errorMessage", "accountNumber is required");
            throw new BpmnError("BAD_REQUEST", "accountNumber is required");
        }

        List<ExpensesResponse> expenses = expensesService.getExpensesForPeriod(accountNumber, from, to);
        execution.setVariable("expenses", expenses);
        try {
            execution.setVariable("expensesJson", objectMapper.writeValueAsString(expenses));
        } catch (JsonProcessingException e) {
            execution.setVariable("errorMessage", "Не удалось сериализовать результат");
            throw new BpmnError("BAD_REQUEST", "Не удалось сериализовать результат");
        }

        log.info("GetDataByPeriodDelegate success: processInstanceId={}, expensesCount={}, expensesJsonLength={}",
                execution.getProcessInstanceId(),
                expenses == null ? null : expenses.size(),
                ((String) execution.getVariable("expensesJson")) == null ? null : ((String) execution.getVariable("expensesJson")).length());
    }
}
