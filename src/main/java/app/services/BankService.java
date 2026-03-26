package app.services;

import lombok.RequiredArgsConstructor;
import app.model.enams.BankOperationStatus;
import app.model.entities.Bank;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import app.repositories.BankRepository;

import java.math.BigDecimal;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class BankService {

    private final BankRepository bankRepository;

    private final Random random = new Random();

    @Transactional
    public BankOperationStatus processPayment(String cardNumber, String cvc, BigDecimal amount) {

        // имитация тех ошибки банка
        if (random.nextInt(10) == 0) {
            return BankOperationStatus.ERROR;
        }

        Bank bankAccount = bankRepository.findByCardNumber(cardNumber).orElse(null);

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
        bankRepository.save(bankAccount);

        return BankOperationStatus.SUCCESS;
    }
}