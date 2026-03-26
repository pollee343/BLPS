package app.model.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
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
    private BigDecimal balance = BigDecimal.ZERO;

    @Column(name = "remaining_seconds", nullable = false)
    private Integer remainingSeconds = 0;

    @Column(name = "remaining_bytes", nullable = false)
    private Long remainingBytes = 0L;

    @Column(name = "remaining_sms", nullable = false)
    private Integer remainingSms = 0;

    @Column(name = "is_blocked", nullable = false)
    private Boolean isBlocked = false;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "userData")
    private List<MoneyOperation> moneyOperations = new ArrayList<>();

    @OneToMany(mappedBy = "userData")
    private List<ServiceUsage> serviceUsages = new ArrayList<>();

    @OneToMany(mappedBy = "userData")
    private List<PromisedPayment> promisedPayments = new ArrayList<>();
}
