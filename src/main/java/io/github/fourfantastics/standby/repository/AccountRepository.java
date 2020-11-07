package io.github.fourfantastics.standby.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import io.github.fourfantastics.standby.model.Account;

@Repository
public interface AccountRepository extends CrudRepository<Account, Long>{

}
