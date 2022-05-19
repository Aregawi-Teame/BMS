package com.membership.domain;

import lombok.*;

import java.util.List;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name ="activity_type")
public class ActivityType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "activity name should not be null")
    @NotEmpty(message = "activity name should not be empty")
    @Column(unique = true, name="activity_name")
    private String activityName;
    
    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "activityType")
    private List<TimeSlot> timeSlots;
}
