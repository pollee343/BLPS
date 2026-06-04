package app.controllers;

import app.dto.responses.ExpensesResponse;
import app.model.enams.OperationType;
import app.services.interfases.ExpensesServiceInterface;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.ProcessInstanceWithVariables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.util.Pair;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/api/expenses")
@RequiredArgsConstructor
public class ExpensesController {

    private static final Logger log = LoggerFactory.getLogger(ExpensesController.class);

    private final ExpensesServiceInterface expensesService;
    private final RuntimeService runtimeService;

        @PreAuthorize("hasRole('USER') || hasRole('ADMIN')")
    @GetMapping(value = "/forPeriod", produces = APPLICATION_JSON_VALUE)
    public List<ExpensesResponse> getExpensesForPeriod(@RequestParam("accountNumber") String accountNumber,
                                                       @RequestParam(name = "from", required = false) LocalDate from,
                                                       @RequestParam(name = "to", required = false) LocalDate to,
                                                       Authentication authentication) {

        Pair<LocalDate, LocalDate> data = checkDates(from, to);
        log.info("ExpensesForPeriod request received. Params: accountNumber={}, from={}, to={}", accountNumber, data.getFirst(), data.getSecond());

        Long userId = extractUserId(authentication);
        List<String> authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        Map<String, Object> variables = new HashMap<>();
        variables.put("accountNumber", accountNumber);
        variables.put("from", data.getFirst());
        variables.put("to", data.getSecond());
        variables.put("userId", userId);
        variables.put("authorities", authorities);
        variables.put("operationType", null);
        variables.put("operationName", null);

        ProcessInstanceWithVariables result = runtimeService
                .createProcessInstanceByKey("Process_1")
                .setVariables(variables)
                .executeWithVariablesInReturn();

        @SuppressWarnings("unchecked")
        List<ExpensesResponse> expenses = (List<ExpensesResponse>) result.getVariables().get("expenses");
        return expenses != null ? expenses : List.of();
    }

    @PreAuthorize("(hasRole('USER') || hasRole('ADMIN')) " +
            "&& @securityService.canAccessAccountNumber(authentication, #accountNumber)")
    @GetMapping(value = "/forPeriodAndOperationType", produces = APPLICATION_JSON_VALUE)
    public List<ExpensesResponse> getExpensesForPeriodAndOperation(@RequestParam("accountNumber") String accountNumber,
                                                       @RequestParam(name = "from", required = false) LocalDate from,
                                                       @RequestParam(name = "to", required = false) LocalDate to,
                                                       @RequestParam("operationType") OperationType operationType){

        Pair<LocalDate, LocalDate> data = checkDates(from, to);
        log.info("ExpensesForPeriodAndOperationName request received. Params: accountNumber={}, from={}, to={}, operationType={}", accountNumber, data.getFirst(), data.getSecond(), operationType);
        return expensesService.getExpensesForPeriodAndOperationType(accountNumber, data.getFirst(), data.getSecond(), operationType);
    }

    @PreAuthorize("(hasRole('USER') || hasRole('ADMIN')) " +
            "&& @securityService.canAccessAccountNumber(authentication, #accountNumber)")
    @GetMapping(value = "/forPeriodAndOperationName", produces = APPLICATION_JSON_VALUE)
    public List<ExpensesResponse> getForPeriodAndOperationName(@RequestParam("accountNumber") String accountNumber,
                                                                   @RequestParam(name = "from", required = false) LocalDate from,
                                                                   @RequestParam(name = "to", required = false) LocalDate to,
                                                                   @RequestParam("operationName") String operationName){
        Pair<LocalDate, LocalDate> data = checkDates(from, to);
        log.info("ExpensesForPeriodAndOperationName request received. Params: accountNumber={}, from={}, to={}, operationName={}", accountNumber, data.getFirst(), data.getSecond(), operationName);
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

    private Long extractUserId(Authentication authentication) {
        if (!(authentication instanceof JwtAuthenticationToken token)) {
            throw new IllegalArgumentException("Unsupported authentication type");
        }
        Jwt jwt = token.getToken();
        Long userId = jwt.getClaim("user_id");
        if (userId == null) {
            throw new IllegalArgumentException("user_id claim is missing");
        }
        return userId;
    }

}
