package app.auth.principals;

import lombok.AllArgsConstructor;
import lombok.Data;


import java.security.Principal;

@Data
@AllArgsConstructor
public class UserPrincipal implements Principal {

    private String username;
    private Long userId;

    @Override
    public String getName() {
        return this.username;
    }
}
