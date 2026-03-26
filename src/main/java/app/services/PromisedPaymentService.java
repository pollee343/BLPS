package app.services;

import jakarta.transaction.Transactional;
import app.model.enams.OperationType;
import app.model.enams.UsageDirection;
import app.model.enams.UsageType;
import app.model.entities.MoneyOperation;
import app.model.entities.ServiceUsage;
import app.model.entities.UserData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import app.repositories.MoneyOperationRepository;
import app.repositories.ServiceUsageRepository;
import app.repositories.UserDataRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
//todo проверить что все проверки из бпмн есть
//todo проверить что все вызовы от клиента из бпмн есть
//todo написать запросы

@Service
public class PromisedPaymentService {

    private final UserDataRepository userDataRepository;
    private final MoneyOperationRepository moneyOperationRepository;
    private final ServiceUsageRepository serviceUsageRepository;
    private final BalanceService balanceService;

    public PromisedPaymentService(UserDataRepository userDataRepository,
                                  MoneyOperationRepository moneyOperationRepository,
                                  ServiceUsageRepository serviceUsageRepository,
                                  BalanceService balanceService) {
        this.userDataRepository = userDataRepository;
        this.moneyOperationRepository = moneyOperationRepository;
        this.serviceUsageRepository = serviceUsageRepository;
        this.balanceService = balanceService;
    }


    @Transactional
    public void takePromisedPayment(Long userDataId, BigDecimal amount) {

        UserData userData = userDataRepository.findById(userDataId).orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        if (userData.getHasPromisedPayment()) {
            throw new RuntimeException("Обещанный платеж уже активирован");
        }

        userData.setBalance(userData.getBalance().add(amount));


        userData.setHasPromisedPayment(true);
        userData.setPromisedPaymentAmount(amount);
        //пока правильные - 3 дня
        userData.setPromisedPaymentDueDate(LocalDateTime.now().plusDays(3));

        MoneyOperation op = new MoneyOperation();
        op.setOperationTime(LocalDateTime.now());
        op.setType(OperationType.INCOME);
        op.setAmount(amount);
        op.setUserData(userData);
        op.setName("Подключение обещанного платежа");

        moneyOperationRepository.save(op);
        
    }


    @Transactional
    public void processPromisedPayment(Long userDataId) {

        UserData userData = userDataRepository.findById(userDataId).orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        if (!userData.getHasPromisedPayment() || userData.getPromisedPaymentAmount().compareTo(BigDecimal.ZERO) == 0
                || userData.getPromisedPaymentDueDate().isAfter(LocalDateTime.now())) {
            return;
        }

        BigDecimal amount = userData.getPromisedPaymentAmount();
        BigDecimal amountWithPenalty = amount.multiply(BigDecimal.valueOf(1.2));

        if (userData.getBalance().compareTo(amountWithPenalty) >= 0) {
            userData.setHasPromisedPayment(false);
            userData.setIsBlocked(false);
            balanceService.spend(userDataId, amountWithPenalty, "Списание обещанного платежа");
            sendSms(userData, "Обещанный платеж " + amount + " руб. + комиссия 20%. Итого списано: " + amountWithPenalty + " руб. Номер разблокирован.");

        } else {
            userData.setIsBlocked(true);
            sendSms(userData, "Ваш номер заблокирован. Задолженность по обещанному платежу: " + amountWithPenalty + " руб. " +
                    "Пополните баланс для разблокировки. Текущий баланс: " + userData.getBalance());
        }
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
