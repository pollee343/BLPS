package app.services.interfases;

import app.dto.requests.RegistrationRequest;

public interface AuthenticationServiceInterface {
    void registration(RegistrationRequest registrationRequest);
    String login(String login, String password);
}
