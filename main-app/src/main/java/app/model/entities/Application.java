package app.model.entities;

import app.model.enams.ApplicationStatus;
import app.model.enams.ApplicationType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Entity
@Table(name = "applications")
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "app_type")
    private ApplicationType applicationType;

    @Column(name = "email", length = 255, nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "app_status")
    private ApplicationStatus applicationStatus = ApplicationStatus.CREATED;

    @ManyToOne
    @JoinColumn(name = "user_data_id")
    private UserData userData;
}
