package app.delegates.expenses.bill;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("prepareGeneratedBillDelegate")
@RequiredArgsConstructor
public class PrepareGeneratedBillDelegate implements JavaDelegate {

    private static final Logger log = LoggerFactory.getLogger(PrepareGeneratedBillDelegate.class);

    @Override
    public void execute(DelegateExecution execution) {
        log.info("PrepareGeneratedReportDelegate start: processInstanceId={}, activityId={}",
                execution.getProcessInstanceId(), execution.getCurrentActivityId());

        byte[] report = (byte[]) execution.getVariable("reportPdf");
        String fileName = (String) execution.getVariable("reportFileName");

        if (report == null || report.length == 0) {
            execution.setVariable("errorMessage", "Отчет не был сформирован");
            throw new BpmnError("REPORT_GENERATION_FAILED", "Отчет не был сформирован");
        }

        if (fileName == null || fileName.isBlank()) {
            fileName = "report.pdf";
            execution.setVariable("reportFileName", fileName);
        }

        execution.setVariable("reportSummary", "Отчет " + fileName + " сформирован, размер " + report.length + " байт");
        log.info("PrepareGeneratedBillDelegate success: processInstanceId={}, fileName={}, reportBytes={}",
                execution.getProcessInstanceId(), fileName, report.length);
    }
}
