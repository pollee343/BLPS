package app.dao;

import app.model.entities.User;
import app.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserDAOService {

    private final UserRepository userRepository;

    public Optional<User> findById(Long userId) {
        return userRepository.findById(userId);
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public List<User> findAll(){
        return userRepository.findAll();
    }

    public Optional<User> findByPassportSeries(String passportSeries) {
        return userRepository.findByPassportSeries(passportSeries);
    }

    public Optional<User> findByPassportNumber(String passportNumber) {
        return userRepository.findByPassportNumber(passportNumber);
    }
}
