package io.github.fourfantastics.standby.service;

import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.github.fourfantastics.standby.model.Company;
import io.github.fourfantastics.standby.model.Filmmaker;
import io.github.fourfantastics.standby.model.PrivacyRequest;
import io.github.fourfantastics.standby.model.RequestStateType;
import io.github.fourfantastics.standby.model.User;
import io.github.fourfantastics.standby.repository.PrivacyRequestRepository;

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

	public void savePrivacyRequest(PrivacyRequest privacyRequest) {
		privacyRequestRepository.save(privacyRequest);
	}

	public Set<PrivacyRequest> getAllPrivacyRequest() {
		Set<PrivacyRequest> privacyRequests = new HashSet<>();
		Iterator<PrivacyRequest> iterator = privacyRequestRepository.findAll().iterator();
		while (iterator.hasNext()) {
			privacyRequests.add(iterator.next());
		}
		return privacyRequests;
	}

	public void sendPrivacyRequest(Company sender, Filmmaker receiver) {
		PrivacyRequest request = new PrivacyRequest();
		request.setCompany(sender);
		request.setFilmmaker(receiver);
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