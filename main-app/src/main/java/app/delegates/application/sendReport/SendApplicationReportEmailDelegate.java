package app.delegates.application.sendReport;

import app.model.enams.ApplicationType;
import app.services.interfases.ReportServiceInterface;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
@Component("sendApplicationReportEmailDelegate")
@RequiredArgsConstructor
public class SendApplicationReportEmailDelegate implements JavaDelegate {

    private static final Logger log = LoggerFactory.getLogger(SendApplicationReportEmailDelegate.class);

    private final ReportServiceInterface reportService;

    @Override
    public void execute(DelegateExecution execution) {
        String email = (String) execution.getVariable("applicationEmail");
        ApplicationType applicationType = readApplicationType(execution.getVariable("applicationType"));
        byte[] reportPdf = (byte[]) execution.getVariable("reportPdf");

        try {
            if (email == null || email.isBlank() || applicationType == null || reportPdf == null || reportPdf.length == 0) {
                execution.setVariable("emailSent", false);
                execution.setVariable("errorMessage", "Недостаточно данных для отправки отчета");
                return;
            }

            reportService.sendApplicationReportEmail(email, applicationType, reportPdf);
            execution.setVariable("emailSent", true);
            log.info("SendApplicationReportEmailDelegate: processInstanceId={}, email={}, applicationType={}",
                    execution.getProcessInstanceId(), email, applicationType);
        } catch (MessagingException | RuntimeException e) {
            execution.setVariable("emailSent", false);
            execution.setVariable("errorMessage", e.getMessage());
        }
    }

    private ApplicationType readApplicationType(Object value) {
        if (value instanceof ApplicationType applicationType) {
            return applicationType;
        }
        if (value instanceof String stringValue && !stringValue.isBlank()) {
            return ApplicationType.valueOf(stringValue);
        }
        return null;
    }

}
