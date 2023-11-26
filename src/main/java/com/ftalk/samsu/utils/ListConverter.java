package com.ftalk.samsu.utils;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ListConverter<T, K> {
    public static <T, K> List<K> listToList(List<T> ts, Function<T, K> constructor) {
        return ts.stream()
                .map(constructor)
                .collect(Collectors.toList());
    }
}