package io.github.fourfantastics.standby.model;

import java.sql.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import com.sun.istack.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShortFilm {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	Long id;
	@NotNull
	String name;
	@NotNull
	String description;
	@DateTimeFormat(iso = ISO.DATE)
	Date uploadDate;
	@NotNull
	Long duration;
}