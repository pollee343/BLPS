package app.camunda.delegates;

import app.model.enams.BankOperationStatus;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component("setBalanceTopUpRejectReasonDelegate")
public class SetBalanceTopUpRejectReasonDelegate extends BalanceTopUpProcessVariables implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) {
        String reason;

        if (Boolean.FALSE.equals(execution.getVariable("topUpDataValid"))) {
            reason = "Некорректные данные пополнения: проверьте userDataId, сумму, номер карты и CVC.";
        } else {
            String statusValue = getString(execution, "bankPaymentStatus");
            BankOperationStatus status = statusValue == null ? null : BankOperationStatus.valueOf(statusValue);
            if (status == BankOperationStatus.DECLINED) {
                reason = "Банк отклонил операцию: карта не найдена, CVC неверный, сумма некорректна или на карте недостаточно средств.";
            } else if (status == BankOperationStatus.ERROR) {
                reason = "Техническая ошибка банка при обработке платежа.";
            } else {
                reason = "Пополнение баланса отклонено.";
            }
        }

        execution.setVariable("errorMessage", reason);
    }
}
