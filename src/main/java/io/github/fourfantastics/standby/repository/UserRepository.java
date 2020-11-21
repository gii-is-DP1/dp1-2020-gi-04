package io.github.fourfantastics.standby.repository;

import org.springframework.data.repository.CrudRepository;

import io.github.fourfantastics.standby.model.User;

public interface UserRepository extends CrudRepository<User,Long> {

	
}
