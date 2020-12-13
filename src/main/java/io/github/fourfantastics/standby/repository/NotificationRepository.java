package io.github.fourfantastics.standby.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import io.github.fourfantastics.standby.model.Notification;
import io.github.fourfantastics.standby.model.User;

public interface NotificationRepository extends CrudRepository<Notification, Long> {
	public List<Notification> findByUser(User u);
}
