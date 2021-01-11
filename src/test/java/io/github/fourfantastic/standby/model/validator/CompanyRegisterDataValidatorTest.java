package io.github.fourfantastic.standby.model.validator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.validation.BindException;

import io.github.fourfantastics.standby.StandbyApplication;
import io.github.fourfantastics.standby.model.form.CompanyRegisterData;
import io.github.fourfantastics.standby.model.validator.CompanyRegisterDataValidator;

@ActiveProfiles("test")
@SpringBootTest(classes = StandbyApplication.class)
public class CompanyRegisterDataValidatorTest {
	@Autowired
	CompanyRegisterDataValidator validator;
	
	@Test
	public void supportTest() {
		assertTrue(validator.supports(CompanyRegisterData.class));
		assertFalse(validator.supports(Object.class));
	}
	
	@Test
	public void validateTest() {
		final CompanyRegisterData mockData = new CompanyRegisterData();
		mockData.setName("company1");
		mockData.setEmail("business@company.com");
		mockData.setPassword("password");
		mockData.setConfirmPassword("password");
		mockData.setBusinessPhone("612345678");
		mockData.setCompanyName("Company Studios");
		mockData.setOfficeAddress("Calle Manzana 4");
		mockData.setTaxIDNumber("123-45-1234567");
		
		BindException errors = new BindException(mockData, "CompanyRegisterData");
		validator.validate(mockData, errors);
		
		assertFalse(errors.hasErrors());
		assertFalse(errors.hasGlobalErrors());
		assertFalse(errors.hasFieldErrors());
	}
	
	@Test
	public void validatePasswordMismatchTest() {
		final CompanyRegisterData mockData = new CompanyRegisterData();
		mockData.setName("company1");
		mockData.setEmail("business@company.com");
		mockData.setPassword("password");
		mockData.setConfirmPassword("password2");
		mockData.setBusinessPhone("612345678");
		mockData.setCompanyName("Company Studios");
		mockData.setOfficeAddress("Calle Manzana 4");
		mockData.setTaxIDNumber("123-45-1234567");
		
		BindException errors = new BindException(mockData, "CompanyRegisterData");
		validator.validate(mockData, errors);
		
		assertTrue(errors.hasErrors());
		assertFalse(errors.hasGlobalErrors());
		assertTrue(errors.hasFieldErrors());
		
		assertThat(errors.getFieldErrorCount("name")).isEqualTo(0);
		assertThat(errors.getFieldErrorCount("email")).isEqualTo(0);
		assertThat(errors.getFieldErrorCount("password")).isEqualTo(0);
		assertThat(errors.getFieldErrorCount("confirmPassword")).isEqualTo(1);
		assertThat(errors.getFieldErrorCount("businessPhone")).isEqualTo(0);
		assertThat(errors.getFieldErrorCount("companyName")).isEqualTo(0);
		assertThat(errors.getFieldErrorCount("officeAddress")).isEqualTo(0);
		assertThat(errors.getFieldErrorCount("taxIDNumber")).isEqualTo(0);
	}
	
	@Test
	public void validateMissingDataTest() {
		final CompanyRegisterData mockData = new CompanyRegisterData();
		mockData.setName("");
		mockData.setEmail("business@company.com");
		mockData.setPassword("password");
		mockData.setConfirmPassword("");
		mockData.setBusinessPhone("612345678");
		mockData.setCompanyName("Company Studios");
		mockData.setOfficeAddress("");
		mockData.setTaxIDNumber("123-45-1234567");
		
		BindException errors = new BindException(mockData, "CompanyRegisterData");
		validator.validate(mockData, errors);
		
		assertTrue(errors.hasErrors());
		assertFalse(errors.hasGlobalErrors());
		assertTrue(errors.hasFieldErrors());
		
		System.out.println(errors);
		
		assertThat(errors.getFieldErrorCount("name")).isEqualTo(1);
		assertThat(errors.getFieldErrorCount("email")).isEqualTo(0);
		assertThat(errors.getFieldErrorCount("password")).isEqualTo(0);
		assertThat(errors.getFieldErrorCount("confirmPassword")).isEqualTo(1);
		assertThat(errors.getFieldErrorCount("businessPhone")).isEqualTo(0);
		assertThat(errors.getFieldErrorCount("companyName")).isEqualTo(0);
		assertThat(errors.getFieldErrorCount("officeAddress")).isEqualTo(1);
		assertThat(errors.getFieldErrorCount("taxIDNumber")).isEqualTo(0);
	}
	
	@Test
	public void validateOutOfBoundsTest() {
		final CompanyRegisterData mockData = new CompanyRegisterData();
		mockData.setName(Stream.generate(() -> "a").limit(65).collect(Collectors.joining()));
		mockData.setEmail("business@company.com");
		mockData.setPassword("password");
		mockData.setConfirmPassword("password");
		mockData.setBusinessPhone("612345678");
		mockData.setCompanyName("Company Studios");
		mockData.setOfficeAddress("Calle Manzana 4");
		mockData.setTaxIDNumber("123-45-1234567");
		
		BindException errors = new BindException(mockData, "CompanyRegisterData");
		validator.validate(mockData, errors);
		
		assertTrue(errors.hasErrors());
		assertFalse(errors.hasGlobalErrors());
		assertTrue(errors.hasFieldErrors());
		
		assertThat(errors.getFieldErrorCount("name")).isEqualTo(1);
		assertThat(errors.getFieldErrorCount("email")).isEqualTo(0);
		assertThat(errors.getFieldErrorCount("password")).isEqualTo(0);
		assertThat(errors.getFieldErrorCount("confirmPassword")).isEqualTo(0);
		assertThat(errors.getFieldErrorCount("businessPhone")).isEqualTo(0);
		assertThat(errors.getFieldErrorCount("companyName")).isEqualTo(0);
		assertThat(errors.getFieldErrorCount("officeAddress")).isEqualTo(0);
		assertThat(errors.getFieldErrorCount("taxIDNumber")).isEqualTo(0);
	}
}
