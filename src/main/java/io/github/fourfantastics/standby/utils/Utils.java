package io.github.fourfantastics.standby.utils;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

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
}
