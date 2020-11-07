package io.github.fourfantastics.standby.model;

import java.sql.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.Size;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import com.sun.istack.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShortFilm {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	Long id;
	@NotNull
	@Size(min = 1, max = 64)
	String name;
	@NotNull
	@Size(max = 500)
	String description;
	@DateTimeFormat(iso = ISO.DATE_TIME)
	Date uploadDate;
	@NotNull
	@Size(min = 1)
	Long duration;
}