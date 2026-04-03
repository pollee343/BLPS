package app.services.interfases;

import app.dto.IncreaseRemainingResponse;
import app.model.entities.UserData;

import java.util.List;

public interface UserDataServiceInterface {
    void createUserData(UserData userData);
    void increaseRemaining(IncreaseRemainingResponse increaseRemainingResponse);
    List<UserData> getAllUserData();
}
