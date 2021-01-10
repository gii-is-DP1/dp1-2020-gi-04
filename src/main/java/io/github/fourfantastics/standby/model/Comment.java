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
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import io.github.fourfantastics.standby.utils.Utils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@EqualsAndHashCode(of = "id")
@ToString(exclude = { "user", "shortFilm" })
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
	static Integer collapsableMinLength = 250;
	static Integer collapsableMinLines = 5;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;

	@NotNull
	@NotEmpty
	@Length(max = 1000)
	@Column(nullable = false)
	String text;

	@NotNull
	@Column(nullable = false)
	Long date;

	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	User user;

	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	ShortFilm shortFilm;
	
	public String getFormattedDate() {
		SimpleDateFormat dateFormatter = new SimpleDateFormat("dd MMM yyyy", Locale.UK);
		return dateFormatter.format(new Date(getDate()));
	}
	
	public Boolean isCollapsable() {
		String text = getText();
		return text.length() > collapsableMinLength || Utils.lineCount(text) > collapsableMinLines;
	}
}
