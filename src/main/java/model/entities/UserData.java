package model.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "user_data")
public class UserData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_number", unique = true)
    private String accountNumber;

    @Column(name = "phone_number", unique = true)
    private String phoneNumber;

    @Column(name = "balance")
    private BigDecimal balance;

    @Column(name = "remaining_minutes")
    private Integer remainingMinutes;

    @Column(name = "remaining_bytes")
    private Long remainingBytes;

    @Column(name = "remaining_sms")
    private Integer remainingSms;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "user_data")
    private List<MoneyOperation> moneyOperations;

    @OneToMany(mappedBy = "user_data")
    private List<ServiceUsage> serviceUsages;

}