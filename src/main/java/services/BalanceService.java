package services;

import jakarta.transaction.Transactional;
import model.enams.OperationType;
import model.entities.MoneyOperation;
import model.entities.UserData;

import org.springframework.stereotype.Service;
import repositories.MoneyOperationRepository;
import repositories.UserDataRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class BalanceService {

    private final UserDataRepository userDataRepository;
    private final MoneyOperationRepository moneyOperationRepository;


    public BalanceService(UserDataRepository userDataRepository, MoneyOperationRepository moneyOperationRepository) {
        this.userDataRepository = userDataRepository;
        this.moneyOperationRepository = moneyOperationRepository;
    }

    @Transactional
    public void topUp(Long userDataId, BigDecimal amount) {
        UserData userData = userDataRepository.findById(userDataId).orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        userData.setBalance(userData.getBalance().add(amount));

        MoneyOperation op = new MoneyOperation();
        op.setType(OperationType.INCOME);
        op.setAmount(amount);
        op.setUserData(userData);

        moneyOperationRepository.save(op);
    }

    @Transactional
    public void spend(Long userDataId, BigDecimal amount) {
        UserData userData = userDataRepository.findById(userDataId).orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        if (userData.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Недостаточно средств");
        }
        userData.setBalance(userData.getBalance().subtract(amount));

        MoneyOperation op = new MoneyOperation();
        op.setType(OperationType.EXPENSE);
        op.setAmount(amount);
        op.setUserData(userData);

        moneyOperationRepository.save(op);
    }


}