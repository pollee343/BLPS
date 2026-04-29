package app.auth.principals;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.security.Principal;

@Data
@AllArgsConstructor
public class RolePrincipal implements Principal {

    private String rolename;

    @Override
    public String getName() {
        return this.rolename;
    }
}
