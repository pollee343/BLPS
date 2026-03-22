package app.services;

import jakarta.transaction.Transactional;
import app.model.enams.OperationType;
import app.model.entities.MoneyOperation;
import app.model.entities.UserData;
import org.springframework.stereotype.Service;
import app.repositories.MoneyOperationRepository;
import app.repositories.UserDataRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Service
public class PromisedPaymentService {

    private final UserDataRepository userDataRepository;
    private final MoneyOperationRepository moneyOperationRepository;

    public PromisedPaymentService(UserDataRepository userDataRepository, MoneyOperationRepository moneyOperationRepository) {
        this.userDataRepository = userDataRepository;
        this.moneyOperationRepository = moneyOperationRepository;
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


        if (userData.getBalance().compareTo(amount) >= 0) {

            userData.setBalance(userData.getBalance().subtract(amount));
            userData.setHasPromisedPayment(false);

            MoneyOperation op = new MoneyOperation();
            op.setType(OperationType.EXPENSE);
            op.setAmount(amount);
            op.setUserData(userData);
            op.setName("Списание обещанного платежа");

            moneyOperationRepository.save(op);

        } else {
            userData.setIsBlocked(true);
        }
    }
}