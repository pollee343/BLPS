package app.auth;

import app.dao.UserDataDAOService;
import app.model.enams.ApplicationType;
import app.model.entities.UserData;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SecurityService {

    private final UserDataDAOService userDataDAOService;

    public boolean canAccessAccountNumber(Authentication authentication, String accountNumber) {
        Jwt jwt = ((JwtAuthenticationToken) authentication).getToken();

        if (isAdminOrModerator(authentication)) return true;

        Long userId = jwt.getClaim("user_id");

        UserData userData = userDataDAOService.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        return userId.equals(userData.getUser().getId());
    }

    public boolean canAccessUserData(Authentication authentication, Long userDataId) {

        Jwt jwt = ((JwtAuthenticationToken) authentication).getToken();

        if (isAdminOrModerator(authentication)) return true;

        Long userId = jwt.getClaim("user_id");

        UserData userData = userDataDAOService.findById(userDataId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        return userId.equals(userData.getUser().getId());
    }

    public boolean emailSendRightsCheck(Authentication authentication, ApplicationType applicationType) {

        if (authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(a -> a.equals("ROLE_ADMIN"))
                && applicationType.equals(ApplicationType.PROMISED_PAYMENT_REJECTION)) {
            return false;
        }

        if (authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(a -> a.equals("ROLE_MODERATOR"))
                && applicationType.equals(ApplicationType.LEGALLY_RELIABLE_REPORT)) {
            return false;
        }

        return true;
    }

    private boolean isAdminOrModerator(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(a -> a.equals("ROLE_ADMIN") || a.equals("ROLE_MODERATOR"));
    }
}
