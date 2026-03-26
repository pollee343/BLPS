package app.services;

import app.model.enams.PromisedPaymentStatus;
import app.model.enams.OperationType;
import app.model.enams.UsageDirection;
import app.model.enams.UsageType;
import app.model.entities.MoneyOperation;
import app.model.entities.PromisedPayment;
import app.model.entities.ServiceUsage;
import app.model.entities.UserData;
import app.repositories.MoneyOperationRepository;
import app.repositories.PromisedPaymentRepository;
import app.repositories.ServiceUsageRepository;
import app.repositories.UserDataRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PromisedPaymentService {

    private final UserDataRepository userDataRepository;
    private final PromisedPaymentRepository promisedPaymentRepository;
    private final MoneyOperationRepository moneyOperationRepository;
    private final ServiceUsageRepository serviceUsageRepository;

    public PromisedPaymentService(UserDataRepository userDataRepository,
                                  PromisedPaymentRepository promisedPaymentRepository,
                                  MoneyOperationRepository moneyOperationRepository,
                                  ServiceUsageRepository serviceUsageRepository) {
        this.userDataRepository = userDataRepository;
        this.promisedPaymentRepository = promisedPaymentRepository;
        this.moneyOperationRepository = moneyOperationRepository;
        this.serviceUsageRepository = serviceUsageRepository;
    }

    @Transactional
    public void takePromisedPayment(Long userDataId, BigDecimal amount) {
        validateRequestedAmount(amount);

        UserData userData = userDataRepository.findById(userDataId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        if (promisedPaymentRepository.existsByUserDataIdAndStatus(userDataId, PromisedPaymentStatus.OVERDUE)) {
            throw new RuntimeException("Нельзя взять новый обещанный платеж, пока не погашена просроченная задолженность");
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime monthStart = now.toLocalDate().withDayOfMonth(1).atStartOfDay();
        LocalDateTime nextMonthStart = monthStart.plusMonths(1);
        BigDecimal monthlyTaken = promisedPaymentRepository.sumAmountTakenForPeriod(userDataId, monthStart, nextMonthStart);

        if (monthlyTaken.add(amount).compareTo(BigDecimal.valueOf(3000)) > 0) {
            BigDecimal available = BigDecimal.valueOf(3000).subtract(monthlyTaken).max(BigDecimal.ZERO);
            throw new RuntimeException("Превышен месячный лимит обещанных платежей. Доступно: " + available + " руб.");
        }

        PromisedPayment promisedPayment = new PromisedPayment();
        promisedPayment.setUserData(userData);
        promisedPayment.setAmount(amount);
        promisedPayment.setAmountToRepay(calculateAmountToRepay(amount));
        promisedPayment.setStatus(PromisedPaymentStatus.ACTIVE);
        promisedPayment.setCreatedAt(now);
        promisedPayment.setDueDate(now.plusDays(3));

        userData.setBalance(userData.getBalance().add(amount));

        promisedPaymentRepository.save(promisedPayment);

        MoneyOperation op = new MoneyOperation();
        op.setOperationTime(now);
        op.setType(OperationType.INCOME);
        op.setAmount(amount);
        op.setUserData(userData);
        op.setName("Подключение обещанного платежа");

        moneyOperationRepository.save(op);
    }

    @Transactional
    public void processPromisedPayment(Long userDataId) {
        UserData userData = userDataRepository.findById(userDataId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        LocalDateTime now = LocalDateTime.now();
        boolean hadOverdueBefore = promisedPaymentRepository.existsByUserDataIdAndStatus(userDataId, PromisedPaymentStatus.OVERDUE);
        List<PromisedPayment> paymentsToProcess =
                promisedPaymentRepository.findByUserDataIdAndStatusInAndDueDateLessThanEqualOrderByDueDateAscCreatedAtAsc(
                        userDataId,
                        List.of(PromisedPaymentStatus.ACTIVE, PromisedPaymentStatus.OVERDUE),
                        now
                );

        boolean hasOverdue = false;

        for (PromisedPayment payment : paymentsToProcess) {
            if (userData.getBalance().compareTo(payment.getAmountToRepay()) >= 0) {
                repayPromisedPayment(userData, payment, now);
                continue;
            }

            if (payment.getStatus() == PromisedPaymentStatus.ACTIVE) {
                payment.setStatus(PromisedPaymentStatus.OVERDUE);
                promisedPaymentRepository.save(payment);
                sendSms(userData,
                        "Номер заблокирован из-за просроченного обещанного платежа на "
                                + payment.getAmountToRepay() + " руб.");
            }

            hasOverdue = true;
        }

        if (!hasOverdue) {
            hasOverdue = promisedPaymentRepository.existsByUserDataIdAndStatus(userDataId, PromisedPaymentStatus.OVERDUE);
        }

        userData.setIsBlocked(hasOverdue);

        if (hadOverdueBefore && !hasOverdue) {
            sendSms(userData, "Все просроченные обещанные платежи погашены. Номер разблокирован.");
        }
    }

    private void repayPromisedPayment(UserData userData, PromisedPayment payment, LocalDateTime now) {
        userData.setBalance(userData.getBalance().subtract(payment.getAmountToRepay()));

        payment.setStatus(PromisedPaymentStatus.PAID);
        payment.setRepaidAt(now);
        promisedPaymentRepository.save(payment);

        MoneyOperation op = new MoneyOperation();
        op.setOperationTime(now);
        op.setType(OperationType.EXPENSE);
        op.setAmount(payment.getAmountToRepay());
        op.setUserData(userData);
        op.setName("Списание обещанного платежа");

        moneyOperationRepository.save(op);
    }

    private void validateRequestedAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Сумма обещанного платежа должна быть положительной");
        }
        if (amount.compareTo(BigDecimal.valueOf(1500)) > 0) {
            throw new RuntimeException("За один раз можно взять не более 1500 руб.");
        }
    }

    private BigDecimal calculateAmountToRepay(BigDecimal amount) {
        return amount.multiply(BigDecimal.valueOf(1.2)).setScale(2, RoundingMode.HALF_UP);
    }

    private void sendSms(UserData userData, String text) {

        ServiceUsage sms = new ServiceUsage();
        sms.setOperationType(UsageType.SMS);
        sms.setDirection(UsageDirection.INCOMING);
        sms.setName(text);
        sms.setUnitsUsed(0);
        sms.setOperationTime(LocalDateTime.now());
        sms.setUserData(userData);

        serviceUsageRepository.save(sms);
    }
}
