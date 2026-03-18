package model.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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

    @Column(name = "account_number", unique = true, length = 255, nullable = false)
    private String accountNumber;

    @Column(name = "phone_number", unique = true, length = 12, nullable = false)
    private String phoneNumber;

    @Column(name = "balance", precision = 19, scale = 2, nullable = false)
    private BigDecimal balance;

    @Column(name = "remaining_minutes", nullable = false)
    private Integer remainingMinutes;

    @Column(name = "remaining_bytes", nullable = false)
    private Long remainingBytes;

    @Column(name = "remaining_sms", nullable = false)
    private Integer remainingSms;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "is_blocked", nullable = false)
    private Boolean isBlocked = false;

    @OneToMany(mappedBy = "user_data")
    private List<MoneyOperation> moneyOperations;

    @OneToMany(mappedBy = "user_data")
    private List<ServiceUsage> serviceUsages;

}