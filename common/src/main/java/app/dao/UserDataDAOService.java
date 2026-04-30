package app.dao;

import app.model.entities.UserData;
import app.repositories.UserDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserDataDAOService {

    private final UserDataRepository userDataRepository;
    
    public Optional<UserData> findByPhoneNumber(String phoneNumber) {
        return userDataRepository.findByPhoneNumber(phoneNumber);
    }

    public Optional<UserData> findByAccountNumber(String accountNumber) {
        return userDataRepository.findByAccountNumber(accountNumber);
    }

    public void save(UserData userData) {
        userDataRepository.save(userData);
    }

    public Optional<UserData> findById(Long userDataId) {
        return userDataRepository.findById(userDataId);
    }

    public List<UserData> findAll() {
        return userDataRepository.findAll();
    }
}
