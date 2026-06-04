package app.delegates;

import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.variable.value.FileValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.io.IOException;
import java.util.Locale;

@Component("validateReportFileDelegate")
public class ValidateReportFileDelegate implements JavaDelegate {

    private static final Logger log = LoggerFactory.getLogger(ValidateReportFileDelegate.class);

    @Override
    public void execute(DelegateExecution execution) {
        byte[] reportPdf = readReportPdf(execution);
        String fileName = (String) execution.getVariable("reportFileName");
        String contentType = (String) execution.getVariable("reportContentType");

        boolean valid = reportPdf != null
                && reportPdf.length > 0
                && isPdf(fileName, contentType, reportPdf);

        if (reportPdf != null) {
            execution.setVariable("reportPdf", reportPdf);
        }
        execution.setVariable("fileValid", valid);
        if (!valid) {
            execution.setVariable("errorMessage", "Разрешён только PDF");
        }

        log.info("ValidateReportFileDelegate: processInstanceId={}, fileName={}, contentType={}, valid={}",
                execution.getProcessInstanceId(), fileName, contentType, valid);
    }

    private byte[] readReportPdf(DelegateExecution execution) {
        Object rawReportPdf = execution.getVariable("reportPdf");
        if (rawReportPdf instanceof FileValue fileValue) {
            return readFileValue(execution, fileValue);
        }
        if (rawReportPdf instanceof byte[] bytes) {
            return bytes;
        }
        if (rawReportPdf instanceof String base64String && !base64String.isBlank()) {
            return decodeBase64(base64String);
        }

        String reportPdfBase64 = (String) execution.getVariable("reportPdfBase64");
        if (reportPdfBase64 != null && !reportPdfBase64.isBlank()) {
            return decodeBase64(reportPdfBase64);
        }

        return null;
    }

    private byte[] readFileValue(DelegateExecution execution, FileValue fileValue) {
        try {
            if (fileValue.getFilename() != null && execution.getVariable("reportFileName") == null) {
                execution.setVariable("reportFileName", fileValue.getFilename());
            }
            if (fileValue.getMimeType() != null && execution.getVariable("reportContentType") == null) {
                execution.setVariable("reportContentType", fileValue.getMimeType());
            }
            return fileValue.getValue().readAllBytes();
        } catch (IOException exception) {
            execution.setVariable("errorMessage", "Не удалось прочитать файл");
            return null;
        }
    }

    private byte[] decodeBase64(String value) {
        try {
            return Base64.getDecoder().decode(value);
        } catch (IllegalArgumentException exception) {
            return null;
        }
    }

    private boolean isPdf(String fileName, String contentType, byte[] reportPdf) {
        boolean byMime = contentType != null && "application/pdf".equalsIgnoreCase(contentType);
        boolean byName = fileName != null && fileName.toLowerCase(Locale.ROOT).endsWith(".pdf");
        boolean byHeader = reportPdf.length >= 4
                && reportPdf[0] == '%'
                && reportPdf[1] == 'P'
                && reportPdf[2] == 'D'
                && reportPdf[3] == 'F';
        return byMime || byName || byHeader;
    }
}
