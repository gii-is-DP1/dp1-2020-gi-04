package io.github.fourfantastics.standby.web;

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
		model.put("servletName", request.getAttribute(RequestDispatcher.ERROR_SERVLET_NAME));
		model.put("throwable", request.getAttribute(RequestDispatcher.ERROR_EXCEPTION));
		model.put("requestUri", request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI));
		model.put("message", request.getAttribute(RequestDispatcher.ERROR_MESSAGE));
        return "error";
    }

    @Override
    public String getErrorPath() {
        return "/error";
    }
}