package io.github.fourfantastic.standby.model.validator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.validation.BindException;

import io.github.fourfantastics.standby.StandbyApplication;
import io.github.fourfantastics.standby.model.form.CompanyConfigurationData;
import io.github.fourfantastics.standby.model.validator.CompanyConfigurationDataValidator;

@ActiveProfiles("test")
@SpringBootTest(classes = StandbyApplication.class)
public class CompanyConfigurationDataValidatorTest {
	@Autowired
	CompanyConfigurationDataValidator validator;
	
	@Test
	public void supportTest() {
		assertTrue(validator.supports(CompanyConfigurationData.class));
		assertFalse(validator.supports(Object.class));
	}
	
	@Test
	public void validateTest() {
		final CompanyConfigurationData mockData = new CompanyConfigurationData();
		mockData.setBusinessPhone("612345678");
		mockData.setCompanyName("Company Studios");
		mockData.setOfficeAddress("Calle Manzana 4");
		mockData.setTaxIDNumber("123-45-1234567");
		mockData.setByPrivacyRequests(true);
		
		BindException errors = new BindException(mockData, "CompanyConfigurationData");
		validator.validate(mockData, errors);
		
		assertFalse(errors.hasErrors());
		assertFalse(errors.hasGlobalErrors());
		assertFalse(errors.hasFieldErrors());
	}
	
	@Test
	public void validateMissingDataTest() {
		final CompanyConfigurationData mockData = new CompanyConfigurationData();
		mockData.setBusinessPhone("");
		mockData.setCompanyName("Company Studios");
		mockData.setOfficeAddress("");
		mockData.setTaxIDNumber("123-45-1234567");
		mockData.setByPrivacyRequests(true);
		
		BindException errors = new BindException(mockData, "CompanyConfigurationData");
		validator.validate(mockData, errors);
		
		assertTrue(errors.hasErrors());
		assertFalse(errors.hasGlobalErrors());
		assertTrue(errors.hasFieldErrors());
		
		System.out.println(errors);
		
		assertThat(errors.getFieldErrorCount("businessPhone")).isEqualTo(1);
		assertThat(errors.getFieldErrorCount("companyName")).isEqualTo(0);
		assertThat(errors.getFieldErrorCount("officeAddress")).isEqualTo(1);
		assertThat(errors.getFieldErrorCount("taxIDNumber")).isEqualTo(0);
		assertThat(errors.getFieldErrorCount("byPrivacyRequests")).isEqualTo(0);
	}
}
