package io.github.fourfantastics.standby.repository;

import java.util.Optional;
import java.util.Set;

import org.springframework.data.repository.CrudRepository;

import io.github.fourfantastics.standby.model.Filmmaker;
import io.github.fourfantastics.standby.model.Subscription;
import io.github.fourfantastics.standby.model.User;

public interface SubscriptionRepository extends CrudRepository<Subscription, Long> {
	public Set<Subscription> findByFilmmaker(Filmmaker filmmaker);
	
	public Set<Subscription> findBySubscriber(User subscriber);
	
	public Optional<Subscription> findBySubscriberAndFilmmaker(User subscriber, Filmmaker filmmaker);
	
	public Integer countBySubscriber(User subscriber);
	
	public Integer countByFilmmaker(Filmmaker filmmaker);
}
