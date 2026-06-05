package app.camunda.delegates;

import org.camunda.bpm.engine.delegate.DelegateExecution;

import java.math.BigDecimal;

abstract class PromisedPaymentProcessVariables {

    protected Long getLong(DelegateExecution execution, String name) {
        Object value = execution.getVariable(name);
        if (value == null) {
            return null;
        }
        if (value instanceof Number number) {
            return number.longValue();
        }
        return Long.valueOf(value.toString());
    }

    protected BigDecimal getBigDecimal(DelegateExecution execution, String name) {
        Object value = execution.getVariable(name);
        if (value == null) {
            return null;
        }
        if (value instanceof BigDecimal decimal) {
            return decimal;
        }
        if (value instanceof Number number) {
            return BigDecimal.valueOf(number.doubleValue());
        }
        return new BigDecimal(value.toString());
    }

    protected Long requireUserDataId(DelegateExecution execution) {
        Long userDataId = getLong(execution, "userDataId");
        if (userDataId == null) {
            throw new IllegalArgumentException("userDataId is required");
        }
        return userDataId;
    }

    protected BigDecimal requireAmount(DelegateExecution execution) {
        BigDecimal amount = getBigDecimal(execution, "amount");
        if (amount == null) {
            throw new IllegalArgumentException("amount is required");
        }
        return amount;
    }
}
