package app.services;

import app.dao.BankDAOService;
import app.services.interfases.BankServiceInterface;
import lombok.RequiredArgsConstructor;
import app.model.enams.BankOperationStatus;
import app.model.entities.Bank;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class BankService implements BankServiceInterface {

    private final BankDAOService bankDAOService;

    private final Random random = new Random();

    @Override
    @Transactional
    public BankOperationStatus processPayment(String cardNumber, String cvc, BigDecimal amount) {

        // имитация тех ошибки банка
        if (random.nextInt(10) == 0) {
            return BankOperationStatus.ERROR;
        }

        Bank bankAccount = bankDAOService.findByCardNumber(cardNumber).orElse(null);

        if (bankAccount == null) {
            return BankOperationStatus.DECLINED;
        }

        if (!bankAccount.getCvc().equals(cvc)) {
            return BankOperationStatus.DECLINED;
        }

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return BankOperationStatus.DECLINED;
        }

        if (bankAccount.getBalance().compareTo(amount) < 0) {
            return BankOperationStatus.DECLINED;
        }

        bankAccount.setBalance(bankAccount.getBalance().subtract(amount));
        bankDAOService.save(bankAccount);

        return BankOperationStatus.SUCCESS;
    }
}