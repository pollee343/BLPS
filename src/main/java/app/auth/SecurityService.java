package app.auth;

import app.dao.UserDataDAOService;
import app.model.entities.UserData;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SecurityService {

    private final UserDataDAOService userDataDAOService;

    public boolean canAccessAccountNumber(Authentication authentication, String accountNumber) {
        Jwt jwt = ((JwtAuthenticationToken) authentication).getToken();

        List<String> roles = jwt.getClaim("authorities");
        if (roles.contains("ROLE_ADMIN") || roles.contains("ROLE_MODERATOR")) {
            return true;
        }

        Long userId = jwt.getClaim("user_id");

        UserData userData = userDataDAOService.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        return userId.equals(userData.getUser().getId());
    }

    public boolean canAccessUserData(Authentication authentication, Long userDataId) {
        Jwt jwt = ((JwtAuthenticationToken) authentication).getToken();

        List<String> roles = jwt.getClaim("authorities");
        if (roles.contains("ROLE_ADMIN") || roles.contains("ROLE_MODERATOR")) {
            return true;
        }

        Long userId = jwt.getClaim("user_id");

        UserData userData = userDataDAOService.findById(userDataId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        return userId.equals(userData.getUser().getId());
    }
}
