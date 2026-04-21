package app.services.interfases;

import app.model.enams.ApplicationType;
import jakarta.mail.MessagingException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;

public interface ReportServiceInterface {
    byte[] getBill(String accountNumber, LocalDate date, String email) throws IOException;
    byte[] getInformationAboutExpenses(String accountNumber, LocalDate from, LocalDate to)  throws IOException;
    void sendEmail(String email, String title, String text, byte[] content) throws MessagingException;
    void sendReportOnEmail(String accountNumber, ApplicationType applicationType, MultipartFile file) throws IOException, MessagingException;
}
