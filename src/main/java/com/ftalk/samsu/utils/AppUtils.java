package com.ftalk.samsu.utils;

import com.ftalk.samsu.exception.SamsuApiException;
import org.springframework.http.HttpStatus;

import java.util.HashSet;
import java.util.Set;

public class AppUtils {
	public static void validatePageNumberAndSize(int page, int size) {
		if (page < 0) {
			throw new SamsuApiException(HttpStatus.BAD_REQUEST, "Page number cannot be less than zero.");
		}

		if (size < 0) {
			throw new SamsuApiException(HttpStatus.BAD_REQUEST, "Size number cannot be less than zero.");
		}

		if (size > AppConstants.MAX_PAGE_SIZE) {
			throw new SamsuApiException(HttpStatus.BAD_REQUEST, "Page size must not be greater than " + AppConstants.MAX_PAGE_SIZE);
		}
	}

	public static <T> Set<T> mergeSets(Set<T> set1, Set<T> set2) {
		Set<T> mergedSet = new HashSet<>();
		if (set1 != null) {
			mergedSet.addAll(set1);
		}
		if (set2 != null) {
			mergedSet.addAll(set2);
		}
		return mergedSet;
	}

}
