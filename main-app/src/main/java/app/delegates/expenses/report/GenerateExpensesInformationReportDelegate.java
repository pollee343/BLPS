package app.delegates.expenses.report;

import app.delegates.expenses.CamundaDateValueReader;
import app.services.interfases.ReportServiceInterface;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.variable.value.FileValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
@Component("generateExpensesInformationReportDelegate")
@RequiredArgsConstructor
public class GenerateExpensesInformationReportDelegate implements JavaDelegate {

    private static final Logger log = LoggerFactory.getLogger(GenerateExpensesInformationReportDelegate.class);

    private final ReportServiceInterface reportService;

    @Override
    public void execute(DelegateExecution execution) {
        log.info("GenerateExpensesInformationReportDelegate start: processInstanceId={}, activityId={}",
                execution.getProcessInstanceId(), execution.getCurrentActivityId());

        String accountNumber = (String) execution.getVariable("accountNumber");
        LocalDate from = CamundaDateValueReader.readLocalDate(
                execution.getVariable("from"), "REPORT_GENERATION_FAILED", "Некорректный формат даты начала");
        LocalDate to = CamundaDateValueReader.readLocalDate(
                execution.getVariable("to"), "REPORT_GENERATION_FAILED", "Некорректный формат даты окончания");

        try {
            byte[] report = reportService.getInformationAboutExpenses(accountNumber, from, to);
            FileValue reportFile = reportService.createPdfFileValue("information-about-expenses.pdf", report);
            execution.setVariable("reportPdf", report);
            execution.setVariable("reportFile", reportFile);
            execution.setVariable("reportFileName", "information-about-expenses.pdf");
            execution.setVariable("reportSummary", "PDF сформирован, размер " + report.length + " байт");
            log.info("GenerateExpensesInformationReportDelegate success: processInstanceId={}, reportBytes={}",
                    execution.getProcessInstanceId(), report.length);
        } catch (IOException | RuntimeException e) {
            execution.setVariable("errorMessage", e.getMessage());
            log.warn("GenerateExpensesInformationReportDelegate error: processInstanceId={}, message={}",
                    execution.getProcessInstanceId(), e.getMessage());
            throw new BpmnError("REPORT_GENERATION_FAILED", e.getMessage());
        }
    }
}
