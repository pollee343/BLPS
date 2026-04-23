package app.controllers;

import app.dao.ReportSendingBodyRequest;
import app.model.enams.ApplicationType;
import app.services.interfases.ReportServiceInterface;
import jakarta.mail.MessagingException;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/report")
@RequiredArgsConstructor
@Log4j2
public class ReportController {

    private final ReportServiceInterface reportService;

    @PreAuthorize("hasRole('USER') || hasRole('ADMIN')" +
            "&& @securityService.canAccessAccountNumber(authentication, #accountNumber)")
    @GetMapping("/getInformationAboutExpenses")
    public ResponseEntity<byte[]> getInformationAboutExpenses(@RequestParam("accountNumber") String accountNumber,
                                                                               @RequestParam(name = "from", required = false) LocalDate from,
                                                                               @RequestParam(name = "to", required = false) LocalDate to) throws MessagingException, IOException {
        Pair<LocalDate, LocalDate> date = checkDates(from, to);
        byte[] data = reportService.getInformationAboutExpenses(accountNumber, date.getFirst(), date.getSecond());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=report.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(data.length)
                .body(data);
    }

    @PreAuthorize("hasRole('USER') || hasRole('ADMIN')" +
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

    @PreAuthorize("hasRole('USER') || hasRole('ADMIN')" +
            "&& @securityService.canAccessAccountNumber(authentication, #accountNumber)")
    @GetMapping("/getBill")
    public ResponseEntity<String> getBill(@RequestParam("accountNumber") String accountNumber,
                                                           @RequestParam(name = "date") LocalDate date,
                                                           @RequestParam(name = "email") @Email String email) throws MessagingException, IOException {
        byte[] data = reportService.getBill(accountNumber, date, email);
        reportService.sendEmail(email, "Счет от МТС",
                "Сообщение является автоматическим, отвечать на него не нужно", data);
        return ResponseEntity.ok()
                .body("Счет успешно сформирован и отправлен на почту " + email);


//        //todo этот вариант только для тестов, отправляется только на почту
//        return ResponseEntity.ok()
//                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=report.pdf")
//                .contentType(MediaType.APPLICATION_PDF)
//                .contentLength(data.length)
//                .body(data);
    }

    @PreAuthorize("hasRole('MODERATOR') || hasRole('ADMIN')")
    @PostMapping(path = "/sendReportOnEmail", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> sendReportOnEmail(@RequestParam String accountNumber,
                                                    @RequestParam ApplicationType applicationType,
                                                    @RequestPart("file") MultipartFile file) throws MessagingException, IOException {

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
        return ResponseEntity.ok("sendReportOnEmail");
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
}
