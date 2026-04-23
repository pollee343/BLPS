package app.controllers;

import app.model.entities.User;
import app.services.interfases.UserServiceInterface;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Log4j2
public class UserController {

    private final UserServiceInterface userService;

    // вместо этого теперь полноценная регистрация
//    @PostMapping("/createUser")
//    public ResponseEntity<String> createUser(@Valid @RequestBody User newUser) {
//        userService.createUser(newUser);
//        return ResponseEntity.status(HttpStatus.CREATED).body("Пользователь успешно создан");
//    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(name = "/getAll", produces = APPLICATION_JSON_VALUE)
    public List<User> getAll() {
        return userService.getAllUsers();
    }
}
