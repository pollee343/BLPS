package app.services.interfases;

import app.dto.ExpensesResponse;
import app.model.enams.OperationType;

import java.time.LocalDate;
import java.util.List;

public interface ExpensesServiceInterface {
    List<ExpensesResponse> getExpensesForPeriod(String accountNumber, LocalDate from, LocalDate to);
    List<ExpensesResponse> getExpensesForPeriodAndOperationType(String accountNumber,
                                                                       LocalDate from,
                                                                       LocalDate to,
                                                                       OperationType operationType);
    List<ExpensesResponse> getForPeriodAndOperationName(String accountNumber,
                                                               LocalDate from,
                                                               LocalDate to,
                                                               String operationName);
}
