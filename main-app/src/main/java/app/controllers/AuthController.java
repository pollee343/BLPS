package app.controllers;

import app.dto.requests.LoginRequest;
import app.dto.requests.RegistrationRequest;
import app.services.interfases.AuthenticationServiceInterface;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationServiceInterface authenticationService;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(authenticationService.login(loginRequest.getUsername(), loginRequest.getPassword()));
    }

    @PostMapping("/registration")
    public ResponseEntity<String> registration(@Valid @RequestBody RegistrationRequest registrationRequest) {
        authenticationService.registration(registrationRequest);
        return ResponseEntity.ok("Успешная регистрация");
    }
}
