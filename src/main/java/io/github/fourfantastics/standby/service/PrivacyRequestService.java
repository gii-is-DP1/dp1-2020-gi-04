package io.github.fourfantastics.standby.service;

import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import io.github.fourfantastics.standby.model.Company;
import io.github.fourfantastics.standby.model.Filmmaker;
import io.github.fourfantastics.standby.model.PrivacyRequest;
import io.github.fourfantastics.standby.model.RequestStateType;
import io.github.fourfantastics.standby.model.User;
import io.github.fourfantastics.standby.model.UserType;
import io.github.fourfantastics.standby.repository.PrivacyRequestRepository;
import io.github.fourfantastics.standby.service.exception.NotFoundException;
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

	public Optional<PrivacyRequest> getPrivacyRequestByFilmmakerAndCompany(Filmmaker filmmaker, Company company) {
		return privacyRequestRepository.findByFilmmakerAndCompany(filmmaker, company);
	}
	
	public void sendPrivacyRequest(User sender, User receiver) throws UnauthorizedException {
		if (sender.getType() != UserType.Company) {
			throw new UnauthorizedException("You must be logged as a company to perform this action",
					Utils.hashSet("notCompany"));
		}
		Company senderCompany = (Company) sender;

		if (receiver.getType() != UserType.Filmmaker) {
			throw new UnauthorizedException("Privacy requests can only be sent to filmmakers",
					Utils.hashSet("notFilmmaker"));
		}
		Filmmaker receiverFilmmaker = (Filmmaker) receiver;

		if (getPrivacyRequestByFilmmakerAndCompany(receiverFilmmaker, senderCompany).isPresent()) {
			throw new UnauthorizedException("Privacy requests can only be sent to filmmakers once",
					Utils.hashSet("AlreadySent"));
		}

		PrivacyRequest request = new PrivacyRequest();
		request.setCompany(senderCompany);
		request.setFilmmaker(receiverFilmmaker);
		request.setRequestDate(new Date().getTime());
		request.setRequestState(RequestStateType.PENDING);
		privacyRequestRepository.save(request);

		notificationService.sendPrivacyRequestNotification(sender.getName(), receiver);
	}

	public void acceptPrivacyRequest(User user, Long requestId) throws Exception {
		PrivacyRequest request = privacyRequestRepository.findById(requestId).orElse(null);
		if (request == null) {
			throw new NotFoundException("Privact request not found", Utils.hashSet("notFoundRequest"));
		}
		
		if (user.getType() != UserType.Filmmaker) {
			throw new UnauthorizedException("You must be a Filmmaker in order to accept a request",
					Utils.hashSet("notFilmmaker"));
		}
		
		if (request.getRequestState() != RequestStateType.PENDING) {
			throw new UnauthorizedException("Request already answered", Utils.hashSet("alreadyAnswered"));
		}
		
		if (!request.getFilmmaker().equals((Filmmaker) user)) {
			throw new UnauthorizedException("This request does not belong to you",
					Utils.hashSet("notReceiverOfRequest"));
		}
		
		request.setRequestState(RequestStateType.ACCEPTED);
		privacyRequestRepository.save(request);
		if (request.getCompany().getConfiguration().getByPrivacyRequests()) {
			Company receiver = request.getCompany();
			Filmmaker sender = request.getFilmmaker();
			notificationService.sendPrivacyRequestResponseNotification(sender.getName(), (User) receiver, true);
		}
	}

	public void declinePrivacyRequest(User user, Long requestId) throws Exception {
		PrivacyRequest request = privacyRequestRepository.findById(requestId).orElse(null);
		if (request == null) {
			throw new NotFoundException("Privact request not found", Utils.hashSet("notFoundRequest"));
		}
		
		if (user.getType() != UserType.Filmmaker) {
			throw new UnauthorizedException("You must be a Filmmaker in order to accept a request",
					Utils.hashSet("notFilmmaker"));
		}
		
		if (request.getRequestState() != RequestStateType.PENDING) {
			throw new UnauthorizedException("Request already answered", Utils.hashSet("alreadyAnswered"));
		}
		
		if (!request.getFilmmaker().equals((Filmmaker) user)) {
			throw new UnauthorizedException("This request does not belong to you",
					Utils.hashSet("notReceiverOfRequest"));
		}
		
		request.setRequestState(RequestStateType.DECLINED);
		privacyRequestRepository.save(request);
		if (request.getCompany().getConfiguration().getByPrivacyRequests()) {
			Company receiver = request.getCompany();
			Filmmaker sender = request.getFilmmaker();
			notificationService.sendPrivacyRequestResponseNotification(sender.getName(), (User) receiver, false);
		}
	}

	public Page<PrivacyRequest> getPrivacyRequestByFilmmaker(Long filmmakerId, Pageable pageable) {
		return privacyRequestRepository.getPrivacyRequestOfFilmmaker(filmmakerId, pageable);
	}
}