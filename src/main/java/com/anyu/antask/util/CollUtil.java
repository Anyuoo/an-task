package com.anyu.antask.util;

import java.util.*;

/**
 * @author Anyu
 * @version 1.0.0
 * @since 2021/8/20
 */
public class CollUtil {
    private CollUtil() {}

    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    public static boolean isNotEmpty(Collection<?> collection) {
        return !isEmpty(collection);
    }

    public static <T> List<List<T>> split(Collection<T> collection, int size) {
        if (isEmpty(collection) || size == 0)
            return Collections.emptyList();
        List<List<T>> result = new ArrayList<>();
        ArrayList<T> subList = new ArrayList<>(size);
        T t;
        for (Iterator<T> iterator = collection.iterator(); iterator.hasNext(); subList.add(t)) {
            t = iterator.next();
            if (subList.size() >= size) {
                result.add(subList);
                subList = new ArrayList<>(size);
            }
        }
        result.add(subList);
        return result;
    }

    public static <T> List<List<T>> emptyList() {
        return Collections.emptyList();
    }
}
