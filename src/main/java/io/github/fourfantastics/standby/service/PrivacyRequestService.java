package io.github.fourfantastics.standby.service;

import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.github.fourfantastics.standby.model.Company;
import io.github.fourfantastics.standby.model.Filmmaker;
import io.github.fourfantastics.standby.model.Notification;
import io.github.fourfantastics.standby.model.NotificationType;
import io.github.fourfantastics.standby.model.PrivacyRequest;
import io.github.fourfantastics.standby.model.RequestStateType;
import io.github.fourfantastics.standby.repository.PrivacyRequestRepository;

@Service
public class PrivacyRequestService {
	PrivacyRequestRepository privacyRequestRepository;
	CompanyService companyService;
	NotificationService notificationService;
	FilmmakerService filmmakerService;

	@Autowired
	public PrivacyRequestService(PrivacyRequestRepository privacyRequestRepository, CompanyService companyService,
			NotificationService notificationService, FilmmakerService filmmakerService) {
		this.privacyRequestRepository = privacyRequestRepository;
		this.companyService = companyService;
		this.notificationService = notificationService;
		this.filmmakerService = filmmakerService;
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

	public void sendPrivacyRequest(Company company, Filmmaker receiver) {
		PrivacyRequest request = new PrivacyRequest();
		request.setCompany(company);
		request.setFilmmaker(receiver);
		request.setRequestDate(new Date().getTime());
		request.setRequestState(RequestStateType.PENDING);
		privacyRequestRepository.save(request);

		company.getSentRequests().add(request);
		companyService.saveCompany(company);

		Notification newPrivacyRequestNotification = new Notification();
		newPrivacyRequestNotification.setEmissionDate(new Date().getTime());
		newPrivacyRequestNotification.setText(String.format("%s wants to know more about you ;)", company.getName()));
		newPrivacyRequestNotification.setUser(receiver);
		newPrivacyRequestNotification.setType(NotificationType.PRIVACY_REQUEST);
		receiver.getNotifications().add(newPrivacyRequestNotification);
		notificationService.saveNotification(newPrivacyRequestNotification);

		receiver.getReceivedRequests().add(request);
		receiver.getNotifications().add(newPrivacyRequestNotification);
		filmmakerService.saveFilmmaker(receiver);
	}

	public void acceptPrivacyRequest(PrivacyRequest request) {
		request.setRequestState(RequestStateType.ACCEPTED);
		privacyRequestRepository.save(request);
		if (request.getCompany().getConfiguration().getByPrivacyRequests()) {
			Company sender = request.getCompany();
			Notification petitionStateNotification = new Notification();
			petitionStateNotification.setEmissionDate(new Date().getTime());
			petitionStateNotification
					.setText(String.format("%s has accepted your petition", request.getFilmmaker().getName()));
			petitionStateNotification.setUser(sender);
			petitionStateNotification.setType(NotificationType.PRIVACY_REQUEST);
			sender.getNotifications().add(petitionStateNotification);

			notificationService.saveNotification(petitionStateNotification);
		}
	}

	public void declinePrivacyRequest(PrivacyRequest request) {
		request.setRequestState(RequestStateType.DECLINED);
		privacyRequestRepository.save(request);
		if (request.getCompany().getConfiguration().getByPrivacyRequests()) {
			Company sender = request.getCompany();
			Notification petitionStateNotification = new Notification();
			petitionStateNotification.setEmissionDate(new Date().getTime());
			petitionStateNotification
					.setText(String.format("%s has declined your petition", request.getFilmmaker().getName()));
			petitionStateNotification.setUser(sender);
			petitionStateNotification.setType(NotificationType.PRIVACY_REQUEST);
			sender.getNotifications().add(petitionStateNotification);

			notificationService.saveNotification(petitionStateNotification);
		}
	}
}