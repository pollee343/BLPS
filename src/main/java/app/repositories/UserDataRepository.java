package app.repositories;

import app.model.entities.UserData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserDataRepository extends JpaRepository<UserData, Long> {
//мб убрать
    Optional<UserData> findByPhoneNumber(String phoneNumber);

    Optional<UserData> findByAccountNumber(String accountNumber);


    //чтобы искал только незаблокировынные
    @Query("SELECT u FROM UserData u WHERE u.hasPromisedPayment = true AND u.isBlocked = false AND u.promisedPaymentDueDate < :now")
    List<UserData> findAllPromisedPaymentTimedOut(@Param("now") LocalDateTime now);
}