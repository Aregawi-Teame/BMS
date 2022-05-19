package com.membership.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Objects;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Plan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "plan name should not be null")
    @NotEmpty(message = "plan name should not be empty")
    private String name;

    private String description;
    @ManyToMany
    @JoinTable(name="Plan_Location",
            joinColumns = {@JoinColumn(name="Plan_id")},
            inverseJoinColumns = {@JoinColumn(name="Location_id")}
    )

    private Set<Location> locations;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinTable(name="Plan_Role",
            joinColumns = {@JoinColumn(name="Plan_id")},
            inverseJoinColumns = {@JoinColumn(name="Role_id")}
    )
    private Set<Role> roles;

    public Plan(Long id, String name, String description, Set<Location> locations, Set<Role> roles) {
        this.id = id;
        this.name = name;
        this.description = description;

        this.locations = locations;
        this.roles = roles;
    }

    @Override
    public String toString() {
        return "Plan{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", locations=" + locations +
                ", roles=" + roles +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Plan)) return false;
        Plan plan = (Plan) o;
        return getId() == plan.getId() && getName().equals(plan.getName()) && Objects.equals(getDescription(), plan.getDescription()) && getLocations().equals(plan.getLocations()) && getRoles().equals(plan.getRoles());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName(), getDescription(), getLocations(), getRoles());
    }
}
