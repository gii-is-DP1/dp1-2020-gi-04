package io.github.fourfantastics.standby.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import io.github.fourfantastics.standby.model.Notification;
import io.github.fourfantastics.standby.model.User;

public interface NotificationRepository extends CrudRepository<Notification, Long> {
	public List<Notification> findByUser(User u);

	public Integer countByUserAndReadDate(User u, Long readDate);

	public Integer countByUser(User u);

	public Page<Notification> findByUser(User u, Pageable pageable);
}
