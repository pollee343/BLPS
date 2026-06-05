package app.delegates.expenses.bill;

import app.services.interfases.ReportServiceInterface;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("sendBillToEmailDelegate")
@RequiredArgsConstructor
public class SendBillToEmailDelegate implements JavaDelegate {

    private static final Logger log = LoggerFactory.getLogger(SendBillToEmailDelegate.class);

    private final ReportServiceInterface reportService;

    @Override
    public void execute(DelegateExecution execution) {
        log.info("SendBillToEmailDelegate start: processInstanceId={}, activityId={}",
                execution.getProcessInstanceId(), execution.getCurrentActivityId());

        String email = (String) execution.getVariable("email");
        byte[] report = (byte[]) execution.getVariable("reportPdf");

        if (email == null || email.isBlank()) {
            execution.setVariable("errorMessage", "email is required");
            throw new BpmnError("EMAIL_SEND_FAILED", "email is required");
        }
        if (report == null || report.length == 0) {
            execution.setVariable("errorMessage", "reportPdf is required");
            throw new BpmnError("EMAIL_SEND_FAILED", "reportPdf is required");
        }

        try {
            reportService.sendEmail(email, "Счет от МТС",
                    "Сообщение является автоматическим, отвечать на него не нужно", report);
            execution.setVariable("resultMessage", "Счет успешно сформирован и отправлен на почту " + email);
            log.info("SendBillToEmailDelegate success: processInstanceId={}, email={}",
                    execution.getProcessInstanceId(), email);
        } catch (MessagingException | RuntimeException e) {
            execution.setVariable("errorMessage", e.getMessage());
            log.warn("SendBillToEmailDelegate error: processInstanceId={}, message={}",
                    execution.getProcessInstanceId(), e.getMessage());
            throw new BpmnError("EMAIL_SEND_FAILED", e.getMessage());
        }
    }
}
