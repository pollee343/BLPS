package app.services;

import app.services.interfases.BalanceServiceInterface;
import app.services.interfases.BankServiceInterface;
import jakarta.transaction.Transactional;
import app.dto.BalanceResponse;
import app.dto.PaymentRequest;
import app.model.enams.BankOperationStatus;
import app.model.enams.OperationType;
import app.model.entities.MoneyOperation;
import app.model.entities.UserData;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import app.repositories.MoneyOperationRepository;
import app.repositories.UserDataRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor

public class BalanceService implements BalanceServiceInterface {

    private final UserDataRepository userDataRepository;
    private final MoneyOperationRepository moneyOperationRepository;
    private final BankServiceInterface bankService;

    @Override
    @Transactional
    public BankOperationStatus topUp(PaymentRequest request) {
        BankOperationStatus status = bankService.processPayment(
                request.getCardNumber(),
                request.getCvc(),
                request.getAmount()
        );

        if (status != BankOperationStatus.SUCCESS) {
            return status;
        }

        UserData userData = userDataRepository.findById(request.getUserDataId())
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        userData.setBalance(userData.getBalance().add(request.getAmount()));

        MoneyOperation op = new MoneyOperation();
        op.setOperationTime(LocalDateTime.now());
        op.setType(OperationType.INCOME);
        op.setAmount(request.getAmount());
        op.setUserData(userData);
        op.setName("Регистрация платежа: Банковская карта");

        moneyOperationRepository.save(op);

        return BankOperationStatus.SUCCESS;
    }

    @Override
    @Transactional
    public void spend(Long userDataId, BigDecimal amount, String name) {
        UserData userData = userDataRepository.findById(userDataId).orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        if (userData.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Недостаточно средств");
        }
        userData.setBalance(userData.getBalance().subtract(amount));

        MoneyOperation op = new MoneyOperation();
        op.setOperationTime(LocalDateTime.now());
        op.setType(OperationType.EXPENSE);
        op.setAmount(amount);
        op.setUserData(userData);
        op.setName(name);

        moneyOperationRepository.save(op);
    }

    @Override
    public BalanceResponse getBalance(Long userDataId) {
        UserData userData = userDataRepository.findById(userDataId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        return new BalanceResponse(
                userData.getId(),
                userData.getAccountNumber(),
                userData.getBalance()
        );
    }

}
