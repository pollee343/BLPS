package app.controllers;

import app.dto.ExpensesResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import app.services.ExpensesService;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/api/expenses")
@RequiredArgsConstructor
public class ExpensesController {

    private static Logger logger = LoggerFactory.getLogger(ExpensesController.class);
    @Autowired
    private ExpensesService expensesService;

    @GetMapping(value = "/forPeriod", produces = APPLICATION_JSON_VALUE)
    public List<ExpensesResponse> getExpensesForPeriod(@RequestParam("accountNumber") String accountNumber,
                                                       @RequestParam(name = "from", required = false) LocalDate from,
                                                       @RequestParam(name = "to", required = false) LocalDate to){
        if (from == null && to == null) {
            to = LocalDate.now();
            from = to.withDayOfMonth(1);
        }
        logger.info("ExpensesForPeriod request received. Params: accountNumber={}, from={}, to={}", accountNumber, from, to);
        return expensesService.getExpensesForPeriod(accountNumber, from, to);
    }

}
