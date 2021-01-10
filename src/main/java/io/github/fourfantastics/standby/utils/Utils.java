package io.github.fourfantastics.standby.utils;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
	@SafeVarargs
	public static final <T> Set<T> hashSet(T... objs) {
		Set<T> set = new HashSet<T>();
		Collections.addAll(set, objs);
		return set;
	}

	public static Integer ensureRange(Integer value, Integer max, Integer min) {
		return Math.min(Math.max(value, min), max);
	}

	public static Integer ensureMax(Integer value, Integer max) {
		return Math.min(value, max);
	}

	public static Integer ensureMin(Integer value, Integer min) {
		return Math.max(value, min);
	}
	
	public static Integer lineCount(String input) {
		Pattern newlinePattern = Pattern.compile("\r\n|\r|\n");
	    Matcher m = newlinePattern.matcher(input);
	    int count = 0;
	    int matcherEnd = -1;
	    
	    while (m.find()) {
	        matcherEnd = m.end();
	        count++;
	    }
	    
	    if (matcherEnd < input.length()) {
	        count++;
	    }

	    return count;
	}
}
