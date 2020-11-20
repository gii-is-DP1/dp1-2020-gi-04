package io.github.fourfantastics.standby.repository;

import org.springframework.data.repository.CrudRepository;
import io.github.fourfantastics.standby.model.Notification;

public interface NotificationRepository extends CrudRepository<Notification,Long> {

}
