package io.github.fourfantastics.standby.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
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
public class Notification {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;

	@NotNull
	@Column(nullable = false)
	String text;

	@NotNull
	@Column(nullable = false)
	Long emisionDate;

	@Column(nullable = true)
	Long readDate;

	@NotNull
	@Column(nullable = false)
	NotificationType type;
	
	@ManyToOne(fetch = FetchType.EAGER, optional = false) // receives
	User user;
	
	public String getFormattedEmissionDate() {
		SimpleDateFormat dateFormatter = new SimpleDateFormat("dd MMM yyyy", Locale.UK);
		return dateFormatter.format(new Date(getEmisionDate()));
	}
	
	public String getFormattedReadDate() {
		SimpleDateFormat dateFormatter = new SimpleDateFormat("dd MMM yyyy", Locale.UK);
		return dateFormatter.format(new Date(getReadDate()));
	}
}
