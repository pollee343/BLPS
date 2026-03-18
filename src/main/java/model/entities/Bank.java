package model.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "bank")
public class Bank {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @Column(name = "last_name")
//    private String lastName;
//
//    @Column(name = "first_name")
//    private String firstName;
//
//    @Column(name = "middle_name")
//    private String middleName;
//
//    @Column(name = "birth_date")
//    private LocalDate birthDate;
//
//    @Column(name = "passport_series")
//    private String passportSeries;
//
//    @Column(name = "passport_number")
//    private String passportNumber;

    @Column(name = "card_number", length = 16, nullable = false, unique = true)
    private String cardNumber;

    @Column(name = "cvc", length = 3, nullable = false)
    private String cvc;

    @Column(name = "balance", precision = 19, scale = 2, nullable = false)
    private BigDecimal balance;

}