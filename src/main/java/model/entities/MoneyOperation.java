package model.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import model.enams.OperationType;
import org.springframework.data.annotation.Id;

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

    @Column(name = "operation_time")
    private LocalDateTime operationTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private OperationType type;

    @Column(name = "amount")
    private BigDecimal amount;

    @ManyToOne
    @JoinColumn(name = "user_data_id")
    private UserData userData;

}