package app.controllers;

import app.model.enams.ApplicationType;
import app.services.interfases.ReportServiceInterface;
import jakarta.mail.MessagingException;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.history.HistoricVariableInstance;
import org.camunda.bpm.engine.runtime.ProcessInstanceWithVariables;
import org.camunda.bpm.engine.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/report")
@RequiredArgsConstructor
public class ReportController {

    private static final Logger log = LoggerFactory.getLogger(ReportController.class);
        private final ReportServiceInterface reportService;
    private final RuntimeService runtimeService;
    private final TaskService taskService;
    private final HistoryService historyService;

    @PreAuthorize("(hasRole('USER') || hasRole('ADMIN')) " +
            "&& @securityService.canAccessAccountNumber(authentication, #accountNumber)")
    @GetMapping("/getInformationAboutExpenses")
    public ResponseEntity<byte[]> getInformationAboutExpenses(@RequestParam("accountNumber") String accountNumber,
                                                              @RequestParam(name = "from", required = false) LocalDate from,
                                                              @RequestParam(name = "to", required = false) LocalDate to,
                                                              Authentication authentication) {
        Pair<LocalDate, LocalDate> date = checkDates(from, to);
        log.info("InformationAboutExpenses request received. Params: accountNumber={}, from={}, to={}",
                accountNumber, date.getFirst(), date.getSecond());

        ProcessInstanceWithVariables result = runtimeService
                .createProcessInstanceByKey("Process_057ekc3")
                .setVariables(buildCommonVariables(accountNumber, authentication))
                .setVariable("from", date.getFirst())
                .setVariable("to", date.getSecond())
                .executeWithVariablesInReturn();

        completeCurrentUserTasks(result.getId());

        byte[] data = (byte[]) runtimeService.getVariable(result.getId(), "reportPdf");
        if (data == null) {
            data = (byte[]) readHistoricVariable(result.getId(), "reportPdf");
        }
        if (data == null) {
            throw new IllegalStateException("reportPdf was not produced by process " + result.getId());
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=report.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(data.length)
                .body(data);
    }

    @PreAuthorize("(hasRole('USER') || hasRole('ADMIN')) " +
            "&& @securityService.canAccessAccountNumber(authentication, #accountNumber)")
    @GetMapping("/getInformationAboutExpensesOnEmail")
    public ResponseEntity<String> getInformationAboutExpensesOnEmail(@RequestParam("accountNumber") String accountNumber,
                                                                               @RequestParam(name = "from", required = false) LocalDate from,
                                                                               @RequestParam(name = "to", required = false) LocalDate to,
                                                                               @RequestParam(name = "email") @Email String email) throws MessagingException, IOException {

        Pair<LocalDate, LocalDate> date = checkDates(from, to);
        byte[] data = reportService.getInformationAboutExpenses(accountNumber, date.getFirst(), date.getSecond());

        reportService.sendEmail(email, "Детализация расходов от МТС",
                "Сообщение является автоматическим, отвечать на него не нужно", data);
        return ResponseEntity.ok()
                .body("Отчет успешно сформирован и отправлен на почту " + email);

    }

    @PreAuthorize("(hasRole('USER') || hasRole('ADMIN')) " +
            "&& @securityService.canAccessAccountNumber(authentication, #accountNumber)")
    @GetMapping("/getBill")
    public ResponseEntity<String> getBill(@RequestParam("accountNumber") String accountNumber,
                                          @RequestParam(name = "date") LocalDate date,
                                          @RequestParam(name = "email") @Email String email,
                                          Authentication authentication) {
        log.info("Bill request received. Params: accountNumber={}, date={}, email={}",
                accountNumber, date, email);

        ProcessInstanceWithVariables result = runtimeService
                .createProcessInstanceByKey("Process_0xitu1x")
                .setVariables(buildCommonVariables(accountNumber, authentication))
                .setVariable("date", date)
                .setVariable("email", email)
                .executeWithVariablesInReturn();

        completeCurrentUserTasks(result.getId());

        String resultMessage = (String) readHistoricVariable(result.getId(), "resultMessage");
        if (resultMessage == null || resultMessage.isBlank()) {
            resultMessage = "Счет успешно сформирован и отправлен на почту " + email;
        }

        return ResponseEntity.ok()
                .body(resultMessage);
    }

    @PreAuthorize("(hasRole('MODERATOR') || hasRole('ADMIN')) &&" +
            "@securityService.emailSendRightsCheck(authentication, #applicationType)")
    @PostMapping(path = "/sendReportOnEmail", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> sendReportOnEmail(@RequestParam String accountNumber,
                                                    @RequestParam ApplicationType applicationType,
                                                    @RequestParam MultipartFile file) throws MessagingException, IOException {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Файл не загружен");
        }

        String contentType = file.getContentType();
        String originalFilename = file.getOriginalFilename();

        boolean pdfByMime = MediaType.APPLICATION_PDF_VALUE.equalsIgnoreCase(contentType);
        boolean pdfByName = originalFilename != null
                && originalFilename.toLowerCase().endsWith(".pdf");

        if (!pdfByMime && !pdfByName) {
            return ResponseEntity.badRequest().body("Разрешён только PDF");
        }

        reportService.sendReportOnEmail(accountNumber, applicationType, file);
        return ResponseEntity.ok("Отчет успешно отправлен на почту");
    }

    private Pair<LocalDate, LocalDate> checkDates(LocalDate from, LocalDate to) {
        if (to == null) {
            to = LocalDate.now();
        }
        if (from == null) {
            from = to.withDayOfMonth(1);
        }
        if (from.isAfter(to)) {
            throw new IllegalArgumentException("Дата начала периода должна быть раньше даты конца");
        }
        return Pair.of(from, to);
    }

    private Map<String, Object> buildCommonVariables(String accountNumber, Authentication authentication) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("accountNumber", accountNumber);
        variables.put("userId", extractUserId(authentication));
        variables.put("authorities", authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList());
        return variables;
    }

    private void completeCurrentUserTasks(String processInstanceId) {
        List<Task> tasks = taskService.createTaskQuery()
                .processInstanceId(processInstanceId)
                .orderByTaskCreateTime()
                .asc()
                .list();

        for (Task task : tasks) {
            log.info("Completing task {} ({}) for processInstanceId={}",
                    task.getId(), task.getName(), processInstanceId);
            taskService.complete(task.getId());
        }
    }

    private Object readHistoricVariable(String processInstanceId, String variableName) {
        HistoricVariableInstance variableInstance = historyService.createHistoricVariableInstanceQuery()
                .processInstanceId(processInstanceId)
                .variableName(variableName)
                .singleResult();
        return variableInstance != null ? variableInstance.getValue() : null;
    }

    private Long extractUserId(Authentication authentication) {
        if (!(authentication instanceof JwtAuthenticationToken token)) {
            throw new IllegalArgumentException("Unsupported authentication type");
        }
        Jwt jwt = token.getToken();
        Long userId = jwt.getClaim("user_id");
        if (userId == null) {
            throw new IllegalArgumentException("user_id claim is missing");
        }
        return userId;
    }
}
