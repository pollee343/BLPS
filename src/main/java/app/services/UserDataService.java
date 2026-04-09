package app.services;

import app.dao.UserDAOService;
import app.dao.UserDataDAOService;
import app.dto.IncreaseRemainingResponse;
import app.model.entities.User;
import app.model.entities.UserData;
import app.services.interfases.UserDataServiceInterface;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserDataService implements UserDataServiceInterface {

    private final UserDataDAOService userDataDAOService;
    private final UserDAOService userDAOService;

    @Override
    public void createUserData(UserData userData) {
        User user = userDAOService.findById(userData.getUser().getId())
                .orElseThrow(() ->new IllegalArgumentException("Пользователя с заданным id не существует"));
        userDataDAOService.save(userData);
    }

    @Override
    public void increaseRemaining(IncreaseRemainingResponse increaseRemainingResponse) {
        UserData userData = userDataDAOService
                .findByAccountNumber(increaseRemainingResponse.getAccountNumber())
                .orElseThrow(() -> new EntityNotFoundException("Пользователь с заданным лицевым счетом не найден"));

        userData.setRemainingSeconds(userData.getRemainingSeconds() + increaseRemainingResponse.getRemainingSeconds());
        userData.setRemainingSms(userData.getRemainingSms() + increaseRemainingResponse.getRemainingSms());
        userData.setRemainingBytes(userData.getRemainingBytes() + increaseRemainingResponse.getRemainingBytes());
        userDataDAOService.save(userData);
    }

    @Override
    public List<UserData> getAllUserData() {
        return userDataDAOService.findAll();
    }
}
