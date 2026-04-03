package app.services;

import app.dto.IncreaseRemainingResponse;
import app.model.entities.User;
import app.model.entities.UserData;
import app.repositories.UserDataRepository;
import app.repositories.UserRepository;
import app.services.interfases.UserDataServiceInterface;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserDataService implements UserDataServiceInterface {

    private final UserDataRepository userDataRepository;
    private final UserRepository userRepository;

    @Override
    public void createUserData(UserData userData) {
        User user = userRepository.findById(userData.getUser().getId())
                .orElseThrow(() ->new IllegalArgumentException("Пользователя с заданным id не существует"));
        userDataRepository.save(userData);
    }

    @Override
    public void increaseRemaining(IncreaseRemainingResponse increaseRemainingResponse) {
        UserData userData = userDataRepository
                .findByAccountNumber(increaseRemainingResponse.getAccountNumber())
                .orElseThrow(() -> new EntityNotFoundException("Пользователь с заданным лицевым счетом не найден"));

        userData.setRemainingSeconds(userData.getRemainingSeconds() + increaseRemainingResponse.getRemainingSeconds());
        userData.setRemainingSms(userData.getRemainingSms() + increaseRemainingResponse.getRemainingSms());
        userData.setRemainingBytes(userData.getRemainingBytes() + increaseRemainingResponse.getRemainingBytes());
        userDataRepository.save(userData);
    }

    @Override
    public List<UserData> getAllUserData() {
        return userDataRepository.findAll();
    }
}
