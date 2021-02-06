package io.github.fourfantastics.standby.model.form;

import java.util.List;

import io.github.fourfantastics.standby.model.Filmmaker;
import io.github.fourfantastics.standby.model.PrivacyRequest;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RequestData {
	
	Filmmaker filmmaker;
	
	Pagination privacyRequestPagination = Pagination.empty();
	
	List<PrivacyRequest> request;
}
