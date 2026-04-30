package app.repositories;

import app.model.entities.UserData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserDataRepository extends JpaRepository<UserData, Long> {
//мб убрать
    Optional<UserData> findByPhoneNumber(String phoneNumber);

    Optional<UserData> findByAccountNumber(String accountNumber);
}
