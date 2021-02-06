package io.github.fourfantastics.standby.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomErrorController implements ErrorController  {
    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Map<String, Object> model) {
		model.put("statusCode", request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE));
		model.put("message", request.getAttribute(RequestDispatcher.ERROR_MESSAGE));
		
		List<Throwable> throwables = new ArrayList<Throwable>();
		Throwable throwable = (Throwable) request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
		while (throwable != null) {
			throwables.add(throwable);
			throwable = throwable.getCause();
		}
		model.put("throwables", throwables);
		
        return "error";
    }

    @Override
    public String getErrorPath() {
        return "/error";
    }
}