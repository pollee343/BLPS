package app.auth;

import app.auth.principals.RolePrincipal;
import org.springframework.security.authentication.jaas.AuthorityGranter;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.Set;


@Component
public class RoleAuthorityGranter implements AuthorityGranter {

    @Override
    public Set<String> grant(Principal principal) {
        if (principal instanceof RolePrincipal rolePrincipal) {
            return Set.of("ROLE_" + rolePrincipal.getName());
        }
        return Set.of();
    }
}
