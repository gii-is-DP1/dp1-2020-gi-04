package io.github.fourfantastics.standby.web;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import io.github.fourfantastics.standby.model.form.SearchData;

@Controller
public class SearchController {

	@RequestMapping("/search")
	public String search(@ModelAttribute SearchData searchData, BindingResult result, Map<String, Object> model) {

		String q = searchData.getQ();

		if (q == null || q.chars().allMatch(Character::isWhitespace)) {
			return "redirect:/";
		}
		
		model.put("search", searchData);
		return "search";
	}
}
