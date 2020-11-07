package io.github.fourfantastics.standby.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.github.fourfantastics.standby.model.Account;
import io.github.fourfantastics.standby.repository.AccountRepository;

@Service
public class AccountService {
	@Autowired
	AccountRepository accountRepository;
	
	public Optional<Account> getAccountById(Long id) {
		return accountRepository.findById(id);
	}
	
	public void saveAccount(Account account) {
		accountRepository.save(account);
	}
}
