package app.controllers;

import app.dto.ExpensesResponse;
import app.model.enams.OperationType;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import app.services.ExpensesService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/api/expenses")
public class ExpensesController {

    private static Logger logger = LoggerFactory.getLogger(ExpensesController.class);
    @Autowired
    private ExpensesService expensesService;

    @GetMapping(value = "/forPeriod", produces = APPLICATION_JSON_VALUE)
    public List<ExpensesResponse> getExpensesForPeriod(@RequestParam("accountNumber") String accountNumber,
                                                       @RequestParam(name = "from", required = false) LocalDate from,
                                                       @RequestParam(name = "to", required = false) LocalDate to) {

        Pair<LocalDate, LocalDate> data = checkDates(from, to);
        logger.info("ExpensesForPeriod request received. Params: accountNumber={}, from={}, to={}", accountNumber, data.getFirst(), data.getSecond());
        return expensesService.getExpensesForPeriod(accountNumber, data.getFirst(), data.getSecond());
    }

    @GetMapping(value = "/forPeriodAndOperationType", produces = APPLICATION_JSON_VALUE)
    public List<ExpensesResponse> getExpensesForPeriodAndOperation(@RequestParam("accountNumber") String accountNumber,
                                                       @RequestParam(name = "from", required = false) LocalDate from,
                                                       @RequestParam(name = "to", required = false) LocalDate to,
                                                       @RequestParam("operationType") OperationType operationType){

        Pair<LocalDate, LocalDate> data = checkDates(from, to);
        logger.info("ExpensesForPeriodAndOperationName request received. Params: accountNumber={}, from={}, to={}, operationType={}", accountNumber, data.getFirst(), data.getSecond(), operationType);
        return expensesService.getExpensesForPeriodAndOperationType(accountNumber, data.getFirst(), data.getSecond(), operationType);
    }

    @GetMapping(value = "/forPeriodAndOperationName", produces = APPLICATION_JSON_VALUE)
    public List<ExpensesResponse> getForPeriodAndOperationName(@RequestParam("accountNumber") String accountNumber,
                                                                   @RequestParam(name = "from", required = false) LocalDate from,
                                                                   @RequestParam(name = "to", required = false) LocalDate to,
                                                                   @RequestParam("operationName") String operationName){
        Pair<LocalDate, LocalDate> data = checkDates(from, to);
        logger.info("ExpensesForPeriodAndOperationName request received. Params: accountNumber={}, from={}, to={}, operationName={}", accountNumber, data.getFirst(), data.getSecond(), operationName);
        return expensesService.getForPeriodAndOperationName(accountNumber, data.getFirst(), data.getSecond(), operationName);
    }

    private Pair<LocalDate, LocalDate> checkDates(@Nullable LocalDate from, @Nullable LocalDate to) {

        if (to == null) {
            to = LocalDate.now();
        }
        if (from == null) {
            from = to.withDayOfMonth(1);
        }
        if (from.isAfter(to)) {
            throw new IllegalArgumentException("Дата начала периода должна быть раньше даты конца");
        }
        return Pair.of(from, to);
    }

}
