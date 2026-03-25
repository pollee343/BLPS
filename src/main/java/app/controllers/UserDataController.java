package app.controllers;

import app.dto.IncreaseRemainingResponse;
import app.dto.MessageOnlyResponse;
import app.model.entities.User;
import app.model.entities.UserData;
import app.repositories.UserDataRepository;
import app.services.UserDataService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/api/userData")
public class UserDataController {

    @Autowired
    private UserDataService userDataService;

    @PostMapping("/createUserData")
    public ResponseEntity<MessageOnlyResponse> createUserData(@Valid @RequestBody UserData userData) {
        userDataService.createUserData(userData);
        return ResponseEntity.status(HttpStatus.CREATED).body(new MessageOnlyResponse("Данные пользователя добавлены"));
    }

    @PostMapping("/increaseRemaining")
    public ResponseEntity<MessageOnlyResponse> increaseRemaining(@Valid @RequestBody IncreaseRemainingResponse increaseRemainingResponse) {
        userDataService.increaseRemaining(increaseRemainingResponse);
        return ResponseEntity.ok(new MessageOnlyResponse("Данные пользователя добавлены"));
    }

    @GetMapping(name = "/getAll", produces = APPLICATION_JSON_VALUE)
    public List<UserData> getAll() {
        return userDataService.getAllUserData();
    }
}
