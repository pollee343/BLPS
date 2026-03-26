package app.services;

import app.controllers.ExpensesController;
import app.dto.ExpensesResponse;
import app.model.enams.OperationType;
import app.model.entities.MoneyOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import app.repositories.MoneyOperationRepository;
import app.repositories.UserDataRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExpensesService {

    private static Logger logger = LoggerFactory.getLogger(ExpensesService.class);
    @Autowired
    private MoneyOperationRepository moneyOperationRepository;
    @Autowired
    private UserDataRepository userDataRepository;

    public List<ExpensesResponse> getExpensesForPeriod(String accountNumber, LocalDate from, LocalDate to) {
        Long userDataId = userDataRepository.findByAccountNumber(accountNumber).get().getId(); // todo работа с optional
        LocalDateTime fromDate = from.atStartOfDay();
        LocalDateTime toDate = to.atStartOfDay();
        return moneyOperationRepository.findByUserDataIdAndOperationTimeBetween(userDataId, fromDate, toDate)
                .stream()
                .map(this::buildExpensesResponse)
                .collect(Collectors.toList());
    }

    public List<ExpensesResponse> getExpensesForPeriodAndOperationType(String accountNumber,
                                                                       LocalDate from,
                                                                       LocalDate to,
                                                                       OperationType operationType) {
        Long userDataId = userDataRepository.findByAccountNumber(accountNumber).get().getId(); // todo работа с optional
        LocalDateTime fromDate = from.atStartOfDay();
        LocalDateTime toDate = to.atStartOfDay();
        return moneyOperationRepository
                .findByUserDataIdAndOperationTimeBetweenAndType(userDataId, fromDate, toDate, operationType)
                .stream()
                .map(this::buildExpensesResponse)
                .collect(Collectors.toList());
    }

    private ExpensesResponse buildExpensesResponse(MoneyOperation moneyOperation) {
        return new ExpensesResponse()
                .setAccountNumber(moneyOperation.getUserData().getAccountNumber())
                .setMoneyOperationName(moneyOperation.getName())
                .setMoneyOperationTime(moneyOperation.getOperationTime())
                .setMoneyOperationType(moneyOperation.getType())
                .setMoneyOperationAmount(moneyOperation.getAmount());
    }
}

