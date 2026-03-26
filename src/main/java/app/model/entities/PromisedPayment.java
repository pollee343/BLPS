package app.model.entities;

import app.model.enams.PromisedPaymentStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "promised_payments")
public class PromisedPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "amount", precision = 19, scale = 2, nullable = false)
    private BigDecimal amount;

    @Column(name = "amount_to_repay", precision = 19, scale = 2, nullable = false)
    private BigDecimal amountToRepay;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PromisedPaymentStatus status;

    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMP")
    private LocalDateTime createdAt;

    @Column(name = "due_date", nullable = false, columnDefinition = "TIMESTAMP")
    private LocalDateTime dueDate;

    @Column(name = "repaid_at", columnDefinition = "TIMESTAMP")
    private LocalDateTime repaidAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_data_id", nullable = false)
    private UserData userData;
}
