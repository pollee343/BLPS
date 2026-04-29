package app.auth;

import app.auth.principals.RolePrincipal;
import app.auth.principals.UserPrincipal;
import app.dao.UserAuthDAOService;
import app.model.auth.Role;
import app.model.auth.UserAuth;
import app.utils.BeanUtil;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;
import java.security.Principal;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DbLoginModule implements LoginModule {

    private Subject subject;
    private CallbackHandler callbackHandler;
    private Map<String, ?> sharedState;
    private Map<String, ?> options;

    private UserAuthDAOService userAuthDAOService;
    private PasswordEncoder passwordEncoder;

    private String username;
    private Set<Principal> principals = new HashSet<>();


    @Override
    public void initialize(Subject subject, CallbackHandler callbackHandler, Map<String, ?> sharedState, Map<String, ?> options) {
        this.subject = subject;
        this.callbackHandler = callbackHandler;
        this.sharedState = sharedState;
        this.options = options;
        this.userAuthDAOService = BeanUtil.getBean(UserAuthDAOService.class);
        this.passwordEncoder = BeanUtil.getBean(PasswordEncoder.class);
    }

    @Override
    public boolean login() throws LoginException {
        NameCallback nameCallback = new NameCallback("username");
        PasswordCallback passwordCallback = new PasswordCallback("password", false);

        try{
            callbackHandler.handle(new Callback[]{nameCallback, passwordCallback});
        } catch (Exception e){
            throw new LoginException("Callback error: " + e.getMessage());
        }

        username = nameCallback.getName();
        String rawPassword = new String(passwordCallback.getPassword());

        UserAuth user = userAuthDAOService.findByUsername(username)
                .orElseThrow(() -> new FailedLoginException("Пользователь не найден"));

        if (!user.isEnabled()) {
            throw new FailedLoginException("Пользователь заблокирован");
        }

        if (!passwordEncoder.matches(rawPassword, user.getPasswordHash())) {
            throw new FailedLoginException("Неверные данные");
        }

        principals.add(new UserPrincipal(user.getUsername(), user.getUserId()));
        for (Role role : user.getRoles()) {
            principals.add(new RolePrincipal(role.getName()));
        }

        return true;
    }

    @Override
    public boolean commit() {
        subject.getPrincipals().addAll(principals);
        return true;
    }

    @Override
    public boolean abort() {
        principals.clear();
        return true;
    }

    @Override
    public boolean logout() {
        subject.getPrincipals().removeAll(principals);
        principals.clear();
        return true;
    }
}
