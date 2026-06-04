package app.delegates;

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

@Component("getExpensesDataForInformationReportDelegate")
@RequiredArgsConstructor
public class GetExpensesDataForInformationReportDelegate implements JavaDelegate {

    private static final Logger log = LoggerFactory.getLogger(GetExpensesDataForInformationReportDelegate.class);

    private final ExpensesServiceInterface expensesService;
    private final ObjectMapper objectMapper;

    @Override
    public void execute(DelegateExecution execution) {
        log.info("GetExpensesDataForInformationReportDelegate start: processInstanceId={}, activityId={}",
                execution.getProcessInstanceId(), execution.getCurrentActivityId());

        String accountNumber = (String) execution.getVariable("accountNumber");
        LocalDate from = normalizeFrom(execution.getVariable("from"), execution.getVariable("to"));
        LocalDate to = normalizeTo(execution.getVariable("to"));

        execution.setVariable("from", from);
        execution.setVariable("to", to);

        if (accountNumber == null || accountNumber.isBlank()) {
            execution.setVariable("errorMessage", "accountNumber is required");
            throw new BpmnError("BAD_REQUEST", "accountNumber is required");
        }

        log.info("GetExpensesDataForInformationReportDelegate input: processInstanceId={}, accountNumber={}, from={}, to={}",
                execution.getProcessInstanceId(), accountNumber, from, to);

        List<ExpensesResponse> expenses = expensesService.getExpensesForPeriod(accountNumber, from, to);
        execution.setVariable("expenses", expenses);

        try {
            execution.setVariable("expensesJson", objectMapper.writeValueAsString(expenses));
        } catch (JsonProcessingException e) {
            execution.setVariable("errorMessage", "Не удалось сериализовать расходы");
            throw new BpmnError("BAD_REQUEST", "Не удалось сериализовать расходы");
        }

        log.info("GetExpensesDataForInformationReportDelegate success: processInstanceId={}, expensesCount={}",
                execution.getProcessInstanceId(), expenses == null ? null : expenses.size());
    }

    private LocalDate normalizeTo(Object rawTo) {
        LocalDate date = CamundaDateValueReader.readLocalDate(rawTo, "BAD_REQUEST", "Некорректный формат даты окончания");
        return date == null ? LocalDate.now() : date;
    }

    private LocalDate normalizeFrom(Object rawFrom, Object rawTo) {
        LocalDate date = CamundaDateValueReader.readLocalDate(rawFrom, "BAD_REQUEST", "Некорректный формат даты начала");
        if (date != null) {
            return date;
        }
        LocalDate to = normalizeTo(rawTo);
        return to.withDayOfMonth(1);
    }
}
