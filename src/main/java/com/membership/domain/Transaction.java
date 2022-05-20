package com.membership.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull(message = "transaction date time should not be null")
    private LocalDateTime dateTime;

    @Valid
    @JsonIgnore
    @ManyToOne()
    @JoinColumn(name = "membership_id")
    private Membership membership;
//
//    @JsonIgnore
//    @ManyToOne(cascade = CascadeType.PERSIST)
//    private Member member;

    @Valid
    @ManyToOne
    @JoinColumn(name = "location_id")
    private Location location;

//    @Valid
//    @ManyToOne
//    @JoinColumn(name = "activity_type_id")
//    private ActivityType activityType;

    @NotNull(message = "transaction status should not be null")
    private boolean isSuccessful;
    
    @NotNull
    @Enumerated(EnumType.STRING)
    private TransactionDescription transactionDescription;

}
