package app.services;

import app.dao.UserDAOService;
import app.model.entities.User;
import app.services.interfases.UserServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService implements UserServiceInterface {

    private final UserDAOService userDAOService;

    @Override
    public void createUser(User newUser) {
        userDAOService.save(newUser);
    }

    @Override
    public List<User> getAllUsers() {
        return userDAOService.findAll();
    }
}
