package io.github.fourfantastics.standby.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@EqualsAndHashCode(callSuper=true)
@NoArgsConstructor
public class Notification extends BaseEntity{
	
	@NotNull
	@NotEmpty
	@Column(nullable = false)
	String text;
	
	@NotNull
	@Column(nullable = false)
	Long emisionDate;
	
	@NotNull
	@Column(nullable = false)
	Long readDate;

}
