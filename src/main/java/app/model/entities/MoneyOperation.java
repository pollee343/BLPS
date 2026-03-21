package app.model.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import app.model.enams.OperationType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "money_operations")
public class MoneyOperation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "operation_time", columnDefinition = "TIMESTAMP")
    private LocalDateTime operationTime;

    @Column(name = "name", length = 300, nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "op_type")
    private OperationType type;

    @Column(name = "amount", precision = 19, scale = 2, nullable = false)
    private BigDecimal amount;

    @ManyToOne
    @JoinColumn(name = "user_data_id")
    private UserData userData;

}