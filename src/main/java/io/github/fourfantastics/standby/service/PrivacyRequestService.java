package io.github.fourfantastics.standby.service;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.github.fourfantastics.standby.model.PrivacyRequest;
import io.github.fourfantastics.standby.repository.PrivacyRequestRepository;

@Service
public class PrivacyRequestService {
	@Autowired
	PrivacyRequestRepository privacyRequestRepository;

	public void savePrivacyRequest(PrivacyRequest privacyRequest) {
		privacyRequestRepository.save(privacyRequest);
	}

	public Set<PrivacyRequest> getAllRoles() {
		Set<PrivacyRequest> privacyRequests = new HashSet<>();
		Iterator<PrivacyRequest> iterator = privacyRequestRepository.findAll().iterator();
		while (iterator.hasNext()) {
			privacyRequests.add(iterator.next());
		}
		return privacyRequests;
	}
}
