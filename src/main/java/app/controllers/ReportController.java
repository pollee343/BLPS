package app.controllers;

import app.dto.MessageOnlyResponse;
import app.services.ReportService;
import jakarta.mail.MessagingException;
import jakarta.validation.constraints.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/report")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @GetMapping("/getInformationAboutExpenses")
    public ResponseEntity<byte[]> getInformationAboutExpenses(@RequestParam("accountNumber") String accountNumber,
                                                                               @RequestParam(name = "from", required = false) LocalDate from,
                                                                               @RequestParam(name = "to", required = false) LocalDate to) throws MessagingException, IOException {
        if (to == null) {
            to = LocalDate.now();
        }
        if (from == null) {
            from = to.withDayOfMonth(1);
        }

        byte[] data = reportService.getInformationAboutExpenses(accountNumber, from, to);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=report.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(data.length)
                .body(data);
    }

    @GetMapping("/getInformationAboutExpensesOnEmail")
    public ResponseEntity<MessageOnlyResponse> getInformationAboutExpensesOnEmail(@RequestParam("accountNumber") String accountNumber,
                                                                               @RequestParam(name = "from", required = false) LocalDate from,
                                                                               @RequestParam(name = "to", required = false) LocalDate to,
                                                                               @RequestParam(name = "email") @Email String email) throws MessagingException, IOException {
        if (to == null) {
            to = LocalDate.now();
        }
        if (from == null) {
            from = to.withDayOfMonth(1);
        }

        byte[] data = reportService.getInformationAboutExpenses(accountNumber, from, to);

        reportService.sendEmail(email, "Детализация расходов от МТС",
                "Сообщение является автоматическим, отвечать на него не нужно", data);
        return ResponseEntity.ok()
                .body(new MessageOnlyResponse("Отчет успешно сформирован и отправлен на почту " + email));

    }

    @GetMapping("/getBill")
    public ResponseEntity<MessageOnlyResponse> getBill(@RequestParam("accountNumber") String accountNumber,
                                                           @RequestParam(name = "date") LocalDate date,
                                                           @RequestParam(name = "email") @Email String email) throws MessagingException, IOException {
        byte[] data = reportService.getBill(accountNumber, date, email);
        reportService.sendEmail(email, "Счет от МТС",
                "Сообщение является автоматическим, отвечать на него не нужно", data);
        return ResponseEntity.ok()
                .body(new MessageOnlyResponse("Счет успешно сформирован и отправлен на почту " + email));


//        //todo этот вариант только для тестов, отправляется только на почту
//        return ResponseEntity.ok()
//                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=report.pdf")
//                .contentType(MediaType.APPLICATION_PDF)
//                .contentLength(data.length)
//                .body(data);
    }
}
