package app.services;

import app.dto.ExpensesResponse;
import app.model.entities.MoneyOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import app.repositories.MoneyOperationRepository;
import app.repositories.UserDataRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExpensesService {

    @Autowired
    private MoneyOperationRepository moneyOperationRepository;
    @Autowired
    private UserDataRepository userDataRepository;

    public List<ExpensesResponse> getExpensesForPeriod(String accountNumber, LocalDate from, LocalDate to) {
        Long userDataId = userDataRepository.findByAccountNumber(accountNumber).get().getId();
        LocalDateTime fromDate = from.atStartOfDay();
        LocalDateTime toDate = from.atStartOfDay();
        return moneyOperationRepository.findByUserDataIdAndOperationTimeBetween(userDataId, fromDate, toDate)
                .stream()
                .map(this::buildExpensesResponse)
                .collect(Collectors.toList());
    }

    private ExpensesResponse buildExpensesResponse(MoneyOperation moneyOperation) {
        return new ExpensesResponse()
                .setAccountNumber(moneyOperation.getUserData().getAccountNumber())
                .setMoneyOperationTime(moneyOperation.getOperationTime())
                .setMoneyOperationType(moneyOperation.getType())
                .setMoneyOperationAmount(moneyOperation.getAmount());
    }
}

