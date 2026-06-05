package app.services;

import app.dao.PromisedPaymentDAOService;
import app.dao.UserDataDAOService;
import app.dto.responses.PromisedPaymentDataResponse;
import app.model.entities.PromisedPayment;
import app.model.entities.UserData;
import app.services.interfases.PromisedPaymentServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PromisedPaymentService implements PromisedPaymentServiceInterface {

    private final UserDataDAOService userDataDAOService;
    private final PromisedPaymentDAOService promisedPaymentDAOService;

    @Override
    public List<PromisedPaymentDataResponse> getPromisedPaymentRejectData(String accountNumber) {
        UserData userData = userDataDAOService.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        return promisedPaymentDAOService.getByUserData(userData)
                .stream()
                .map(this::buildPromisedPaymentDataResponse)
                .collect(Collectors.toList());
    }

    private PromisedPaymentDataResponse buildPromisedPaymentDataResponse(PromisedPayment promisedPayment) {
        return new PromisedPaymentDataResponse()
                .setAmount(promisedPayment.getAmount())
                .setStatus(promisedPayment.getStatus())
                .setAmountToRepay(promisedPayment.getAmountToRepay())
                .setDueDate(promisedPayment.getDueDate())
                .setCreatedAt(promisedPayment.getCreatedAt())
                .setRepaidAt(promisedPayment.getRepaidAt());
    }
}
