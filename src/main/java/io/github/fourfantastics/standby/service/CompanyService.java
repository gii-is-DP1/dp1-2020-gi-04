package io.github.fourfantastics.standby.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import io.github.fourfantastics.standby.model.Company;
import io.github.fourfantastics.standby.repository.CompanyRepository;

public class CompanyService {
	@Autowired 
	CompanyRepository companyRepository;
	
	public Optional<Company> getNotificationById(Long id){
		return companyRepository.findById(id);
	}
	
	public void saveNotification(Company company) {
		companyRepository.save(company);
	}

}
