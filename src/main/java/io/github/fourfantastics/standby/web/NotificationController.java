package io.github.fourfantastics.standby.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import io.github.fourfantastics.standby.model.Notification;
import io.github.fourfantastics.standby.model.User;
import io.github.fourfantastics.standby.model.form.NotificationData;
import io.github.fourfantastics.standby.model.form.NotificationWrapper;
import io.github.fourfantastics.standby.model.form.Pagination;
import io.github.fourfantastics.standby.service.NotificationService;
import io.github.fourfantastics.standby.service.UserService;

@Controller
public class NotificationController {
	@Autowired
	UserService userService;

	@Autowired
	NotificationService notificationService;

	@RequestMapping(value = "userNotifications", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
	public @ResponseBody Object getUserNotifications(HttpSession session) {
		Map<String, Object> res = new HashMap<String, Object>();
		User user = userService.getLoggedUser(session).orElse(null);

		if (user == null) {
			res.put("status", 302);
			res.put("url", "/login");
			return res;
		}

		Integer notificationsCount = notificationService.getUnreadNotifications(user);

		res.put("count", notificationsCount);
		res.put("status", 200);

		return res;
	}

	@RequestMapping("/notifications")
	public String getNotificationsView(HttpSession session, Map<String, Object> model,
			@ModelAttribute NotificationData notificationData) {
		User user = userService.getLoggedUser(session).orElse(null);

		if (user == null) {
			return "redirect:/login";
		}

		if (notificationData.getPagination() == null) {
			notificationData.setPagination(Pagination.empty());
		}
		notificationData.getPagination().setTotalElements(notificationService.countNotifications(user));
		List<Notification> notifications = notificationService
				.getPaginatedNotifications(user,
						notificationData.getPagination().getPageRequest(Sort.by("emissionDate").descending()))
				.getContent();
		notificationData
				.setNotifications(notifications.stream().map(NotificationWrapper::of).collect(Collectors.toList()));
		model.put("notificationData", notificationData);

		notificationService.readNotifications(notifications);
		return "userNotifications";
	}
}