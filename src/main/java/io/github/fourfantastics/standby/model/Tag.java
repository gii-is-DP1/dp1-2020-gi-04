package io.github.fourfantastics.standby.model;

import javax.persistence.Entity;
import javax.validation.constraints.NotEmpty;

import org.hibernate.validator.constraints.Length;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Tag extends BaseEntity{
		
	@NotEmpty
	@Length(max = 20)
	String tagname;
	
}
