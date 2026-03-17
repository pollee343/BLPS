package services;

import lombok.RequiredArgsConstructor;
import model.entities.Bank;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repositories.BankRepository;


import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class BankService {

    private final BankRepository bankRepository;

    @Transactional
    public boolean processPayment(String cardNumber, String cvc, BigDecimal amount) {
        Bank bankAccount = bankRepository.findByCardNumber(cardNumber)
                .orElseThrow(() -> new RuntimeException("Карта не найдена"));

        if (!bankAccount.getCvc().equals(cvc)) {
            throw new RuntimeException("Неверный CVC");
        }

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Некорректная сумма");
        }

        if (bankAccount.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Недостаточно средств");
        }

        bankAccount.setBalance(bankAccount.getBalance().subtract(amount));
        bankRepository.save(bankAccount);

        return true;
    }
}