package app.services;

import app.dao.RoleDAOService;
import app.dao.UserAuthDAOService;
import app.dao.UserDAOService;
import app.dao.UserDataDAOService;
import app.dto.RegistrationRequest;
import app.model.auth.Role;
import app.model.auth.UserAuth;
import app.model.entities.User;
import app.model.entities.UserData;
import app.services.interfases.AuthenticationServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class AuthenticationService implements AuthenticationServiceInterface {

    private final UserAuthDAOService userAuthDAOService;
    private final UserDataDAOService userDataDAOService;
    private final RoleDAOService roleDAOService;
    private final UserDAOService userDAOService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void registration(RegistrationRequest request) {

        if (userAuthDAOService.findByUsername(request.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Логин уже занят");
        }

        if (userDataDAOService.findByAccountNumber(request.getAccountNumber()).isPresent()) {
            throw new IllegalArgumentException("Номер счёта уже занят");
        }

        if (userDataDAOService.findByPhoneNumber(request.getPhoneNumber()).isPresent()) {
            throw new IllegalArgumentException("Номер телефона уже занят");
        }

        if (userDAOService.findByPassportNumber(request.getPassportNumber()).isPresent()) {
            throw new IllegalArgumentException("Номер паспорта уже занят");
        }

        if (userDAOService.findByPassportSeries(request.getPassportSeries()).isPresent()) {
            throw new IllegalArgumentException("Серия паспорта уже занят");
        }

        Role userRole = roleDAOService.findByName(request.getRoleName())
                .orElseThrow(() -> new IllegalStateException("Роль не найдена"));

        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setBirthDate(request.getBirthDate());
        user.setPassportNumber(request.getPassportNumber());
        user.setPassportSeries(request.getPassportSeries());
        user.setUserData(new ArrayList<>());

        UserData userData = new UserData();
        userData.setAccountNumber(request.getAccountNumber());
        userData.setPhoneNumber(request.getPhoneNumber());

        if (request.getBalance() != null) {
            userData.setBalance(request.getBalance());
        }
        if (request.getRemainingBytes() != null) {
            userData.setRemainingBytes(request.getRemainingBytes());
        }
        if (request.getRemainingSms() != null) {
            userData.setRemainingSms(request.getRemainingSms());
        }
        if (request.getRemainingSeconds() != null) {
            userData.setRemainingSeconds(request.getRemainingSeconds());
        }

        user.getUserData().add(userData);

        user = userDAOService.save(user);

        UserAuth auth = new UserAuth();
        auth.setUser(user);
        auth.setUsername(request.getUsername());
        auth.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        auth.setEnabled(true);

        auth = userAuthDAOService.save(auth);

        auth.getRoles().add(userRole);

        userAuthDAOService.save(auth);
    }
}
