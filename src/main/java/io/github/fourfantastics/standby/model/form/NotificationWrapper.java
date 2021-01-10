package io.github.fourfantastics.standby.model.form;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.github.fourfantastics.standby.model.Notification;
import io.github.fourfantastics.standby.model.NotificationType;
import io.github.fourfantastics.standby.model.User;
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

	public String getFormattedEmissionDate() {
		SimpleDateFormat dateFormatter = new SimpleDateFormat("dd MMM yyyy", Locale.UK);
		return dateFormatter.format(new Date(getEmissionDate()));
	}

	public String getFormattedReadDate() {
		SimpleDateFormat dateFormatter = new SimpleDateFormat("dd MMM yyyy", Locale.UK);
		return dateFormatter.format(new Date(getReadDate()));
	}
}
