package app.controllers;

import app.dto.responses.IncreaseRemainingResponse;
import app.dto.responses.PromisedPaymentDataResponse;
import app.model.entities.UserData;
import app.services.interfases.PromisedPaymentServiceInterface;
import app.services.interfases.UserDataServiceInterface;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/api/userData")
@RequiredArgsConstructor
public class UserDataController {

    private final UserDataServiceInterface userDataService;
    private final PromisedPaymentServiceInterface promisedPaymentService;

    // вместо этого теперь полноценная регистрация
//    @PostMapping("/createUserData")
//    public ResponseEntity<String> createUserData(@Valid @RequestBody UserData userData) {
//        userDataService.createUserData(userData);
//        return ResponseEntity.status(HttpStatus.CREATED).body("Данные пользователя добавлены");
//    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/increaseRemaining")
    public ResponseEntity<String> increaseRemaining(@Valid @RequestBody IncreaseRemainingResponse increaseRemainingResponse) {
        userDataService.increaseRemaining(increaseRemainingResponse);
        return ResponseEntity.ok("Данные пользователя обновлены");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(name = "/getAll", produces = APPLICATION_JSON_VALUE)
    public List<UserData> getAll() {
        return userDataService.getAllUserData();
    }

    @PreAuthorize("hasRole('MODERATOR')")
    @GetMapping("/getUserDataForPromisedPaymentRejection")
    public List<PromisedPaymentDataResponse> getUserDataForPromisedPaymentRejection(@RequestParam String accountNumber) {
        return promisedPaymentService.getPromisedPaymentRejectData(accountNumber);
    }
}
