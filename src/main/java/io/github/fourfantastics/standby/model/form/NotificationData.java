package io.github.fourfantastics.standby.model.form;

import java.util.Set;

import io.github.fourfantastics.standby.model.Notification;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class NotificationData {

	Set<Notification> notifications;
	
	Pagination pagination;
	
}
