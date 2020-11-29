package io.github.fourfantastics.standby.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import io.github.fourfantastics.standby.model.User;

public interface UserRepository extends CrudRepository<User,Long> {
	public Optional<User> findByName(String name);
}
