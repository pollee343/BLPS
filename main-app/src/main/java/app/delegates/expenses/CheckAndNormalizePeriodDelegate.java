package app.delegates.expenses;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component("checkAndNormalizePeriodDelegate")
@RequiredArgsConstructor
public class CheckAndNormalizePeriodDelegate implements JavaDelegate {

    private static final Logger log = LoggerFactory.getLogger(CheckAndNormalizePeriodDelegate.class);

    @Override
    public void execute(DelegateExecution execution) {
        log.info("CheckAndNormalizePeriodDelegate start: processInstanceId={}, activityId={}",
                execution.getProcessInstanceId(),
                execution.getCurrentActivityId());

        LocalDate to;
        LocalDate from;
        try {
            to = readLocalDate(execution.getVariable("to"));
            from = readLocalDate(execution.getVariable("from"));
        } catch (BpmnError e) {
            execution.setVariable("errorMessage", e.getMessage());
            log.warn("CheckAndNormalizePeriodDelegate error: processInstanceId={}, message={}",
                    execution.getProcessInstanceId(),
                    e.getMessage());
            throw e;
        } catch (RuntimeException e) {
            execution.setVariable("errorMessage", "Некорректный формат даты");
            log.warn("CheckAndNormalizePeriodDelegate runtime error: processInstanceId={}, message={}",
                    execution.getProcessInstanceId(),
                    e.getMessage());
            throw new BpmnError("BAD_REQUEST", "Некорректный формат даты");
        }

        if (to == null) {
            to = LocalDate.now();
        }
        if (from == null) {
            from = to.withDayOfMonth(1);
        }
        if (from.isAfter(to)) {
            execution.setVariable("errorMessage", "Дата начала периода должна быть раньше даты конца");
            log.warn("CheckAndNormalizePeriodDelegate validation error: processInstanceId={}, from={}, to={}",
                    execution.getProcessInstanceId(),
                    from,
                    to);
            throw new BpmnError("BAD_REQUEST", "Дата начала периода должна быть раньше даты конца");
        }

        execution.setVariable("from", from);
        execution.setVariable("to", to);

        log.info("CheckAndNormalizePeriodDelegate success: processInstanceId={}, from={}, to={}",
                execution.getProcessInstanceId(),
                from,
                to);
    }

    private LocalDate readLocalDate(Object value) {
        return CamundaDateValueReader.readLocalDate(value, "BAD_REQUEST", "Некорректный формат даты");
    }
}
