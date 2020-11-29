package io.github.fourfantastics.standby.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@EqualsAndHashCode(of = "id")
@ToString(exclude = "user")
@NoArgsConstructor
@AllArgsConstructor
public class NotificationConfiguration {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;

	@NotNull
	@Column(nullable = false)
	Boolean byComments;

	@NotNull
	@Column(nullable = false)
	Boolean byRatings;

	@NotNull
	@Column(nullable = false)
	Boolean bySubscriptions;

	@NotNull
	@Column(nullable = false)
	Boolean byPrivacyRequests;

	@OneToOne(fetch = FetchType.EAGER, optional = false) // configures
	User user;
}
