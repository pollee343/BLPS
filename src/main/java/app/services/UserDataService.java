package app.services;

import app.dto.IncreaseRemainingResponse;
import app.model.entities.User;
import app.model.entities.UserData;
import app.repositories.UserDataRepository;
import app.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserDataService {

    @Autowired
    private UserDataRepository userDataRepository;
    @Autowired
    private UserRepository userRepository;

    public void createUserData(UserData userData) {
        User user = userRepository.findById(userData.getUser().getId())
                .orElseThrow(() ->new IllegalArgumentException("Пользователя с заданным id не существует"));
        userDataRepository.save(userData);
    }

    public void increaseRemaining(IncreaseRemainingResponse increaseRemainingResponse) {
        UserData userData = userDataRepository
                .findByAccountNumber(increaseRemainingResponse.getAccountNumber())
                .orElseThrow(() -> new EntityNotFoundException("Пользователь с заданным лицевым счетом не найден"));

        userData.setRemainingSeconds(userData.getRemainingSeconds() + increaseRemainingResponse.getRemainingSeconds());
        userData.setRemainingSms(userData.getRemainingSms() + increaseRemainingResponse.getRemainingSms());
        userData.setRemainingBytes(userData.getRemainingBytes() + increaseRemainingResponse.getRemainingBytes());
        userDataRepository.save(userData);
    }

    public List<UserData> getAllUserData() {
        return userDataRepository.findAll();
    }
}
