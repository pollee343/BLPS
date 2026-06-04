package app.delegates;

import app.services.interfases.ReportServiceInterface;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
@Component("getBillDataDelegate")
@RequiredArgsConstructor
public class GetBillDataDelegate implements JavaDelegate {

    private static final Logger log = LoggerFactory.getLogger(GetBillDataDelegate.class);

    private final ReportServiceInterface reportService;

    @Override
    public void execute(DelegateExecution execution) {
        log.info("GetBillDataDelegate start: processInstanceId={}, activityId={}",
                execution.getProcessInstanceId(), execution.getCurrentActivityId());

        String accountNumber = (String) execution.getVariable("accountNumber");
        LocalDate date = CamundaDateValueReader.readLocalDate(
                execution.getVariable("date"), "REPORT_GENERATION_FAILED", "Некорректный формат даты");
        String email = (String) execution.getVariable("email");

        if (accountNumber == null || accountNumber.isBlank()) {
            execution.setVariable("errorMessage", "accountNumber is required");
            throw new BpmnError("REPORT_GENERATION_FAILED", "accountNumber is required");
        }
        if (date == null) {
            execution.setVariable("errorMessage", "date is required");
            throw new BpmnError("REPORT_GENERATION_FAILED", "date is required");
        }
        if (email == null || email.isBlank()) {
            execution.setVariable("errorMessage", "email is required");
            throw new BpmnError("REPORT_GENERATION_FAILED", "email is required");
        }

        try {
            byte[] report = reportService.getBill(accountNumber, date, email);
            execution.setVariable("reportPdf", report);
            execution.setVariable("reportFileName", "bill.pdf");
            log.info("GetBillDataDelegate success: processInstanceId={}, reportBytes={}",
                    execution.getProcessInstanceId(), report.length);
        } catch (IOException | RuntimeException e) {
            execution.setVariable("errorMessage", e.getMessage());
            log.warn("GetBillDataDelegate error: processInstanceId={}, message={}",
                    execution.getProcessInstanceId(), e.getMessage());
            throw new BpmnError("REPORT_GENERATION_FAILED", e.getMessage());
        }
    }
}
