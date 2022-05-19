package com.membership.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalTime;

@JsonIgnoreProperties({"hibernateLazyInitializer"})
@Entity
@Data
@Table(name = "time_slot")
public class TimeSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "timeslot_id")
    private Long id;

    @NotNull(message = "timeslot start time should not be null")
    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @NotNull(message = "timeslot end time should not be null")
    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @NotNull(message = "timeslot day of week should not be null")
    @Column(name = "day_of_week")
    @Enumerated(EnumType.STRING)
    private DayType dayOfWeek;

    
    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name="activity_type_id")
    private ActivityType activityType;

}
