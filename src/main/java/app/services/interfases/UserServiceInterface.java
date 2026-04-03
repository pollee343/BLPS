package app.services.interfases;

import app.model.entities.User;

import java.util.List;

public interface UserServiceInterface {
    void createUser(User newUser);
    List<User> getAllUsers();
}
