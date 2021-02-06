package io.github.fourfantastics.standby.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.github.fourfantastics.standby.model.Company;
import io.github.fourfantastics.standby.model.Filmmaker;
import io.github.fourfantastics.standby.model.PrivacyRequest;
import io.github.fourfantastics.standby.model.RequestStateType;
import io.github.fourfantastics.standby.model.User;
import io.github.fourfantastics.standby.model.UserType;
import io.github.fourfantastics.standby.repository.PrivacyRequestRepository;
import io.github.fourfantastics.standby.service.exception.UnauthorizedException;
import io.github.fourfantastics.standby.utils.Utils;

@Service
public class PrivacyRequestService {
	PrivacyRequestRepository privacyRequestRepository;
	NotificationService notificationService;

	@Autowired
	public PrivacyRequestService(PrivacyRequestRepository privacyRequestRepository,
			NotificationService notificationService) {
		this.privacyRequestRepository = privacyRequestRepository;
		this.notificationService = notificationService;
	}

	public void sendPrivacyRequest(User sender, User receiver) throws UnauthorizedException {
		if (sender.getType() != UserType.Company) {
			throw new UnauthorizedException("You must be logged as a Company to perform this action",
					Utils.hashSet("notCompany"));
		}
		Company senderCompany = (Company) sender;
		if (receiver.getType() != UserType.Filmmaker) {
			throw new UnauthorizedException("Privacy Request can only be sent to filmmakers",
					Utils.hashSet("notFilmmaker"));
		}
		Filmmaker receiverFilmmaker = (Filmmaker) receiver;
		if (senderCompany.getSentRequests().stream().anyMatch(x -> x.getFilmmaker().getName().equals(receiver.getName()))) {
			throw new UnauthorizedException("Privacy Request can only be sent to filmmakers once",
					Utils.hashSet("AlreadySent"));
		}	
		
		PrivacyRequest request = new PrivacyRequest();
		request.setCompany(senderCompany);
		request.setFilmmaker(receiverFilmmaker);
		request.setRequestDate(new Date().getTime());
		request.setRequestState(RequestStateType.PENDING);
		privacyRequestRepository.save(request);

		notificationService.sendPrivacyRequestNotification(sender.getName(), (User) receiver);
	}

	public void acceptPrivacyRequest(PrivacyRequest request) {
		request.setRequestState(RequestStateType.ACCEPTED);
		privacyRequestRepository.save(request);
		if (request.getCompany().getConfiguration().getByPrivacyRequests()) {
			Company receiver = request.getCompany();
			Filmmaker sender = request.getFilmmaker();
			notificationService.sendPrivacyRequestResponseNotification(sender.getName(), (User) receiver, true);
		}
	}

	public void declinePrivacyRequest(PrivacyRequest request) {
		request.setRequestState(RequestStateType.DECLINED);
		privacyRequestRepository.save(request);
		if (request.getCompany().getConfiguration().getByPrivacyRequests()) {
			Company receiver = request.getCompany();
			Filmmaker sender = request.getFilmmaker();
			notificationService.sendPrivacyRequestResponseNotification(sender.getName(), (User) receiver, false);
		}
	}
}