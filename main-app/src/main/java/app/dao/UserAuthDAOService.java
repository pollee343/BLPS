package app.dao;

import app.model.auth.UserAuth;
import app.repositories.UserAuthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserAuthDAOService {
    private final UserAuthRepository userAuthRepository;

    public Optional<UserAuth> findByUsername(String username) {
        return userAuthRepository.findByUsername(username);
    }

    public UserAuth save(UserAuth userAuth) {
        return userAuthRepository.saveAndFlush(userAuth);
    }
}
