package io.github.fourfantastics.standby.model.form;

import java.util.Date;

import io.github.fourfantastics.standby.model.Notification;
import io.github.fourfantastics.standby.model.NotificationType;
import io.github.fourfantastics.standby.model.User;
import io.github.fourfantastics.standby.utils.RelativeTimeFormatter;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class NotificationWrapper {
	Long id;

	String text;

	Long emissionDate;

	Long readDate;

	NotificationType type;

	User user;

	private NotificationWrapper(Notification notification) {
		this.id = notification.getId();
		this.text = notification.getText();
		this.emissionDate = notification.getEmissionDate();
		this.readDate = notification.getReadDate();
		this.type = notification.getType();
		this.user = notification.getUser();
	}

	public static NotificationWrapper of(Notification notification) {
		return new NotificationWrapper(notification);
	}

	public String getFormattedRelativeEmissionTime() {
		return RelativeTimeFormatter.toRelative(new Date().getTime() - getEmissionDate(), 1);
	}
}
