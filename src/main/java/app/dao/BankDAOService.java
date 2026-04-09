package app.dao;

import app.model.entities.Bank;
import app.repositories.BankRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BankDAOService {

    private final BankRepository bankRepository;

    public Optional<Bank> findByCardNumber(String cardNumber) {
        return bankRepository.findByCardNumber(cardNumber);
    }

    public void save(Bank bank) {
        bankRepository.save(bank);
    }

}
