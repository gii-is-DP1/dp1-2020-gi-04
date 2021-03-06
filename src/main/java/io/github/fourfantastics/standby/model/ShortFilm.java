package io.github.fourfantastics.standby.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import io.github.fourfantastics.standby.utils.RelativeTimeFormatter;
import io.github.fourfantastics.standby.utils.Utils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Data
@EqualsAndHashCode(of = "id")
@ToString(exclude = { "ratings", "comments", "tags", "uploader" })
@AllArgsConstructor
public class ShortFilm {
	static Integer collapsableMinLength = 250;
	static Integer collapsableMinLines = 5;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;

	@NotNull
	@NotEmpty
	@Length(max = 128)
	@Column(nullable = false)
	String title;

	@NotNull
	@NotEmpty
	@Column(nullable = false)
	String videoUrl;

	@Column(nullable = true)
	String thumbnailUrl;
	
	@NotNull
	@Column(nullable = false)
	Long uploadDate;

	@NotNull
	@Length(max = 10000)
	@Column(nullable = false)
	String description;
	
	@NotNull
	@Column(nullable = false)
	@Range(min = 0)
	Long viewCount;

	@NotNull
	@Column(nullable = false)
	@Range(min = 0, max = 10)
	Double ratingAverage = 0d;

	@OneToMany(fetch = FetchType.EAGER, mappedBy = "shortFilm")
	Set<Rating> ratings = new HashSet<Rating>();

	@OneToMany(fetch = FetchType.EAGER, mappedBy = "shortFilm")
	Set<Comment> comments = new HashSet<Comment>();

	@ManyToMany(fetch = FetchType.EAGER, mappedBy = "movies")
	Set<Tag> tags = new HashSet<Tag>();

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST, mappedBy = "shortfilm")
	Set<Role> roles = new HashSet<Role>();

	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	Filmmaker uploader;
	
	public String getFormattedUploadDate() {
		SimpleDateFormat dateFormatter = new SimpleDateFormat("dd MMM yyyy", Locale.UK);
		return dateFormatter.format(new Date(getUploadDate()));
	}
	
	public String getFormattedRelativeUploadTime() {
		return RelativeTimeFormatter.toRelative(new Date().getTime() - getUploadDate(), 1);
	}
	
	public Boolean isCollapsable() {
		String text = getDescription();
		return text.length() > collapsableMinLength || Utils.lineCount(text) > collapsableMinLines;
	}
	
	public ShortFilm() {
		super();
		setViewCount(0L);
	}
}