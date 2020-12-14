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
}
