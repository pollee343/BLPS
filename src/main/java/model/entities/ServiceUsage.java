package model.entities;

import jakarta.persistence.*;
import model.enams.UsageType;

import java.time.LocalDateTime;

@Entity
@Table(name = "service_usage")
public class ServiceUsage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "operation_type")
    private UsageType operationType;

    @Column(name = "units_used")
    private Integer unitsUsed;

    @Column(name = "operation_time", columnDefinition = "TIMESTAMP")
    private LocalDateTime operationTime;

    @ManyToOne
    @JoinColumn(name = "user_data_id")
    private UserData userData;

}