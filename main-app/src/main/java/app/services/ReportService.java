package app.services;

import app.PDFUtils.ReportBuilder;
import app.dao.ApplicationDAOService;
import app.dao.MoneyOperationDAOService;
import app.dao.ServiceUsageDAOService;
import app.dao.UserDataDAOService;
import app.model.enams.*;
import app.model.entities.Application;
import app.model.entities.MoneyOperation;
import app.model.entities.ServiceUsage;
import app.model.entities.UserData;
import app.model.utils.OperationInformation;
import app.services.interfases.ApplicationServiceInterface;
import app.services.interfases.ReportServiceInterface;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDate;
import java.util.*;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService implements ReportServiceInterface {

    private static final Logger log = LoggerFactory.getLogger(ReportService.class);

    private final JavaMailSender emailSender;
    private final ReportBuilder reportBuilder;

    private final UserDataDAOService userDataDAOService;
    private final MoneyOperationDAOService moneyOperationDAOService;
    private final ServiceUsageDAOService serviceUsageDAOService;
    private final ApplicationDAOService applicationDAOService;

    private final ApplicationServiceInterface applicationService;

    @Override
    public byte[] getBill(String accountNumber, LocalDate date, String email) throws IOException {

        if (date.getDayOfMonth() != 1){
            throw new IllegalArgumentException("Счет можно получить только за весь месяц");
        }
        if (date.isAfter(LocalDate.now().minusMonths(1)) ||
                (date.getMonth() == LocalDate.now().minusMonths(1).getMonth()
                        && LocalDate.now().getDayOfMonth() < 5)){
            throw new IllegalArgumentException("Счет можно получить после 5 числа месяца, следующего за расчетным периодом");
        }

        LocalDate to = date.plusMonths(1).minusDays(1);

        LocalDateTime fromTime = date.atStartOfDay();
        LocalDateTime toTime = date.plusMonths(1).minusDays(1).atTime(23, 59);

        UserData userData = userDataDAOService
                .findByAccountNumber(accountNumber)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь с заданным лицевым счетом не найден"));

        List<MoneyOperation> moneyOperations =
                moneyOperationDAOService.findByUserDataIdAndOperationTimeBetween(userData.getId(), fromTime, toTime);

        BigDecimal total = BigDecimal.ZERO;
        BigDecimal incs = BigDecimal.ZERO;
        List<MoneyOperation> incomes = new ArrayList<>();
        for (MoneyOperation moneyOperation : moneyOperations) {
            if (moneyOperation.getType() == OperationType.EXPENSE){
                total = total.add(moneyOperation.getAmount());
            } else {
                incs = incs.add(moneyOperation.getAmount());
                incomes.add(moneyOperation);
            }
        }
        BigDecimal remains = total.subtract(incs).compareTo(BigDecimal.ZERO) < 0
                ? BigDecimal.ZERO : total.subtract(incs);

        List<MoneyOperation> moneyOpsBig =
                moneyOperationDAOService.findByUserDataIdAndOperationTimeBefore(userData.getId(), fromTime);
        BigDecimal rem_on_from = BigDecimal.ZERO;
        for (MoneyOperation moneyOperation : moneyOpsBig) {
            if (moneyOperation.getType() == OperationType.INCOME){
                rem_on_from = rem_on_from.add(moneyOperation.getAmount());
            } else {
                rem_on_from = rem_on_from.subtract(moneyOperation.getAmount());
            }
        }

        return reportBuilder.getBillReport(accountNumber, date, to, userData, total, remains, rem_on_from, email, incomes);
    }

    @Override
    public byte[] getInformationAboutExpenses(String accountNumber, LocalDate from, LocalDate to) throws IOException {

        if (to.isAfter(ChronoLocalDate.from(LocalDate.now().atStartOfDay()))){
            throw new IllegalArgumentException("Детализированная информация доступна только за время до текущего момента");
        }

        UserData userData = userDataDAOService
                .findByAccountNumber(accountNumber)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь с заданным лицевым счетом не найден"));

        LocalDateTime fromTime = from.atStartOfDay();
        LocalDateTime toTime = to.atTime(23, 59);

        List<MoneyOperation> moneyOperations =
                moneyOperationDAOService.findByUserDataIdAndOperationTimeBetween(userData.getId(), fromTime, toTime);

        Map<String, BigDecimal> forCategory = new HashMap<>();
        for (MoneyOperation moneyOperation : moneyOperations) {
            if (moneyOperation.getType() == OperationType.EXPENSE){
                if (!forCategory.containsKey(moneyOperation.getName())){
                    forCategory.put(moneyOperation.getName(), moneyOperation.getAmount());
                } else {
                    forCategory.put(moneyOperation.getName(), forCategory.get(moneyOperation.getName()).add(moneyOperation.getAmount()));
                }
            }
        }

        List<ServiceUsage> serviceUsages =
                serviceUsageDAOService.findByUserDataIdAndOperationTimeBetween(userData.getId(), fromTime, toTime);
        serviceUsages.sort(Comparator.comparing(ServiceUsage::getOperationTime));

        List<OperationInformation> operations = new ArrayList<>();
        for (ServiceUsage serviceUsage : serviceUsages) {
            operations.add(getOperation(serviceUsage));
        }

        return reportBuilder.getInformationAboutExpensesReport(accountNumber, from, to,
                userData, forCategory, operations);
    }

    private OperationInformation getOperation(ServiceUsage serviceUsage) {
        OperationInformation operation = new OperationInformation();
        operation.setTime(serviceUsage.getOperationTime());

        if (serviceUsage.getOperationType().equals(UsageType.CALL)) {
            if (serviceUsage.getName().isEmpty()) {
                if (serviceUsage.getDirection().equals(UsageDirection.OUTGOING)) {
                    serviceUsage.setName("Исходящий звонок");
                } else {
                    serviceUsage.setName("Входящий звонок");
                }
            }
            operation.setName(serviceUsage.getName());
            Integer amount = serviceUsage.getUnitsUsed();
            String units = "секунд";
            if (amount / 60 > 0) {
                amount = amount / 60;
                units = "минут";
            }
            if (amount / 60 > 0) {
                amount = amount / 60;
                units = "часов";
            }
            operation.setDescription(amount + " " + units);

        } else if (serviceUsage.getOperationType().equals(UsageType.SMS)) {
            if (serviceUsage.getName().isEmpty()) {
                if (serviceUsage.getDirection().equals(UsageDirection.OUTGOING)) {
                    serviceUsage.setName("Отправлено сообщение");
                } else {
                    serviceUsage.setName("Получено сообщение");
                }
            }
            operation.setName(serviceUsage.getName());
            if (serviceUsage.getDirection().equals(UsageDirection.OUTGOING)) {
                operation.setDescription(serviceUsage.getUnitsUsed() + " слов");
            } else {
                operation.setDescription("1 шт.");
            }
        } else if (serviceUsage.getOperationType().equals(UsageType.INTERNET)) {
            Integer amount = serviceUsage.getUnitsUsed();
            String units = "байт";
            if (amount / 1024 > 0) {
                amount /= 1024;
                units = "Кб";
            }
            if (amount / 1024 > 0) {
                amount /= 1024;
                units = "Mб";
            }
            if (amount / 1024 > 0) {
                amount /= 1024;
                units = "Гб";
            }
            if (amount / 1024 > 0) {
                amount /= 1024;
                units = "Tб";
            }
            operation.setName("Мобильный интернет");
            operation.setDescription("Мобильный интернет: трафик " + amount + units);
        }
        log.info(operation.toString());
        return operation;
    }

    @Override
    public void sendEmail(String email, String title, String text, byte[] content) throws MessagingException {
        final MimeMessage mimeMessage = this.emailSender.createMimeMessage();
        final MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);

        messageHelper.setFrom("noreply@baeldung.com");
        messageHelper.setTo(email);
        messageHelper.setSubject(title);
        messageHelper.setText(text);
        messageHelper.addAttachment("report.pdf", new ByteArrayResource(content));
        emailSender.send(mimeMessage);
        log.info("Send message success on email: {}", email);
    }

    @Override
    public void sendReportOnEmail(String accountNumber, ApplicationType applicationType, MultipartFile file) throws IOException, MessagingException {
        UserData userData = userDataDAOService.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        Application application = applicationDAOService.findWaitingApplications(userData, applicationType, ApplicationStatus.WAITING_EMPLOYEE)
                .orElseThrow(() -> new EntityNotFoundException("Заявка не найдена"));

        if (applicationType.equals(ApplicationType.PROMISED_PAYMENT_REJECTION)){
            sendEmail(application.getEmail(),
                    "Ответ по заявке на получение информации об отказе в получении обещанного платежа",
                    "",
                    file.getBytes());
        } else {
            sendEmail(application.getEmail(),
                    "Ответ по заявке на получение юридически достоверного отчета",
                    "",
                    file.getBytes());
        }
        applicationService.makeApplicationProcessed(userData, applicationType);
    }
}
