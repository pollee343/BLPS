package app.services;

import app.dto.ExpensesResponse;
import app.model.enams.OperationType;
import app.model.entities.MoneyOperation;
import app.model.entities.UserData;
import app.services.interfases.ExpensesServiceInterface;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import app.repositories.MoneyOperationRepository;
import app.repositories.UserDataRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class ExpensesService implements ExpensesServiceInterface {

    private final MoneyOperationRepository moneyOperationRepository;
    private final UserDataRepository userDataRepository;

    @Override
    public List<ExpensesResponse> getExpensesForPeriod(String accountNumber,
                                                       LocalDate from,
                                                       LocalDate to) {
        UserData userData = userDataRepository
                .findByAccountNumber(accountNumber)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь с заданным лицевым счетом не найден"));
        Long userDataId = userData.getId();
        LocalDateTime fromDate = from.atStartOfDay();
        LocalDateTime toDate = to.atTime(23, 59, 59);
        return moneyOperationRepository.findByUserDataIdAndOperationTimeBetween(userDataId, fromDate, toDate)
                .stream()
                .map(this::buildExpensesResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ExpensesResponse> getExpensesForPeriodAndOperationType(String accountNumber,
                                                                       LocalDate from,
                                                                       LocalDate to,
                                                                       OperationType operationType) {
        UserData userData = userDataRepository
                .findByAccountNumber(accountNumber)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь с заданным лицевым счетом не найден"));
        Long userDataId = userData.getId();
        LocalDateTime fromDate = from.atStartOfDay();
        LocalDateTime toDate = to.atTime(23, 59, 59);
        return moneyOperationRepository
                .findByUserDataIdAndOperationTimeBetweenAndType(userDataId, fromDate, toDate, operationType)
                .stream()
                .map(this::buildExpensesResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ExpensesResponse> getForPeriodAndOperationName(String accountNumber,
                                                               LocalDate from,
                                                               LocalDate to,
                                                               String operationName) {
        UserData userData = userDataRepository
                .findByAccountNumber(accountNumber)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь с заданным лицевым счетом не найден"));
        Long userDataId = userData.getId();
        LocalDateTime fromDate = from.atStartOfDay();
        LocalDateTime toDate = to.atTime(23, 59, 59);
        List<ExpensesResponse> result = moneyOperationRepository
                .findByUserDataIdAndOperationTimeBetweenAndNameLike(userDataId, fromDate, toDate, "%" + operationName + "%")
                .stream()
                .map(this::buildExpensesResponse)
                .collect(Collectors.toList());
        if (result.isEmpty()) {
            throw new EntityNotFoundException("Операция " + operationName + " не найдена");
        }
        return result;
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

