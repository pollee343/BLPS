package app.model.entities;

import jakarta.persistence.*;
import app.model.enams.UsageDirection;
import app.model.enams.UsageType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "service_usage")
public class ServiceUsage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "operation_type")
    private UsageType operationType;

    @Enumerated(EnumType.STRING)
    @Column(name = "direction")
    private UsageDirection direction;

    @Column(name = "name")
    private String name;

    @Column(name = "units_used")
    private Integer unitsUsed;

    @Column(name = "operation_time", columnDefinition = "TIMESTAMP")
    private LocalDateTime operationTime;

    @ManyToOne
    @JoinColumn(name = "user_data_id")
    private UserData userData;

}