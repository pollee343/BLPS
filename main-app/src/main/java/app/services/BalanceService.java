package app.services;

import app.dao.MoneyOperationDAOService;
import app.dao.UserDataDAOService;
import app.dto.requests.PaymentRequest;
import app.dto.responses.BalanceResponse;
import app.services.interfases.BalanceServiceInterface;
import app.services.interfases.BankServiceInterface;
import app.services.interfases.PromisedPaymentServiceInterface;
import app.model.enams.BankOperationStatus;
import app.model.enams.OperationType;
import app.model.entities.MoneyOperation;
import app.model.entities.UserData;

import app.services.interfases.PromisedPaymentServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor

public class BalanceService implements BalanceServiceInterface {

    private final UserDataDAOService userDataDAOService;
    private final MoneyOperationDAOService moneyOperationDAOService;

    private final BankServiceInterface bankService;
    private final PromisedPaymentServiceInterface promisedPaymentService;
    private final TransactionTemplate transactionTemplate;

    @Override
    public BankOperationStatus topUp(PaymentRequest request) {
        return transactionTemplate.execute(status -> topUpInTransaction(request));
    }

    private BankOperationStatus topUpInTransaction(PaymentRequest request) {
        BankOperationStatus bankStatus = bankService.processPayment(
                request.getCardNumber(),
                request.getCvc(),
                request.getAmount()
        );

        if (bankStatus != BankOperationStatus.SUCCESS) {
            return bankStatus;
        }

        UserData userData = userDataDAOService.findById(request.getUserDataId())
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        userData.setBalance(userData.getBalance().add(request.getAmount()));

        MoneyOperation op = new MoneyOperation();
        op.setOperationTime(LocalDateTime.now());
        op.setType(OperationType.INCOME);
        op.setAmount(request.getAmount());
        op.setUserData(userData);
        op.setName("Регистрация платежа: Банковская карта");

        moneyOperationDAOService.save(op);

        promisedPaymentService.processPromisedPayment(request.getUserDataId());

        return BankOperationStatus.SUCCESS;
    }

    @Override
    public void spend(Long userDataId, BigDecimal amount, String name) {
        transactionTemplate.executeWithoutResult(status -> spendInTransaction(userDataId, amount, name));
    }

    private void spendInTransaction(Long userDataId, BigDecimal amount, String name) {
        UserData userData = userDataDAOService.findById(userDataId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

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

        moneyOperationDAOService.save(op);
    }

    @Override
    public BalanceResponse getBalance(Long userDataId) {
        UserData userData = userDataDAOService.findById(userDataId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        return new BalanceResponse(
                userData.getId(),
                userData.getAccountNumber(),
                userData.getBalance()
        );
    }

}
