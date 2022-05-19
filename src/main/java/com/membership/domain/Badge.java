package com.membership.domain;

import java.time.LocalDate;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor @Setter @Getter
@Table(name="badges")
public class Badge {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "badge_id")
	private Long id;

	@NotNull(message = "badge status should not be null")
	@Column(name="is_active",nullable = false, columnDefinition="boolean default false")
	private boolean isActive;

	@NotNull(message = "issue date for badge should not be null")
	@Column(name="issue_date")
	private LocalDate issueDate;

	@NotNull(message = "expiry date of badge should not be null")
	@Future
	@Column(name="expiration_date")
	private LocalDate expirationDate;

	@Valid
	@JsonIgnore
	@ManyToOne
	private Member member;
	
	public Badge(boolean isActive, LocalDate issueDate, LocalDate expirationDate) {
		super();
		this.isActive = isActive;
		this.issueDate = issueDate;
		this.expirationDate = expirationDate;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(expirationDate, id, issueDate);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Badge other = (Badge) obj;
		return Objects.equals(expirationDate, other.expirationDate) && Objects.equals(id, other.id)
				&& Objects.equals(issueDate, other.issueDate);
	}
	
	
	
}
