package com.style.utils.lang;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Set工具类
 */
public class SetUtils extends org.apache.commons.collections.SetUtils {

    /**
     * HashSet
     */
    public static <E> HashSet<E> newHashSet() {
        return new HashSet<>();
    }

    @SafeVarargs
    public static <E> HashSet<E> newHashSet(E... elements) {
        HashSet<E> set = newHashSet(elements.length);
        CollectUtils.addAll(set, elements);
        return set;
    }

    public static <E> HashSet<E> newHashSet(int initialCapacity) {
        return new HashSet<>(initialCapacity);
    }

    public static <E> HashSet<E> newHashSet(Iterable<? extends E> iterable) {
        HashSet<E> set = newHashSet();
        addAll(set, iterable);
        return set;
    }

    public static <E> HashSet<E> newHashSet(Iterator<? extends E> iterator) {
        HashSet<E> set = newHashSet();
        addAll(set, iterator);
        return set;
    }

    /**
     * LinkedHashSet
     */
    public static <E> LinkedHashSet<E> newLinkedHashSet() {
        return new LinkedHashSet<>();
    }

    public static <E> LinkedHashSet<E> newLinkedHashSet(int initialCapacity) {
        return new LinkedHashSet<>(initialCapacity);
    }

    @SafeVarargs
    public static <E> LinkedHashSet<E> newLinkedHashSet(E... elements) {
        LinkedHashSet<E> set = newLinkedHashSet(elements.length);
        CollectUtils.addAll(set, elements);
        return set;
    }

    public static <E> LinkedHashSet<E> newLinkedHashSet(Iterable<? extends E> iterable) {
        LinkedHashSet<E> set = newLinkedHashSet();
        addAll(set, iterable);
        return set;
    }

    public static <E> LinkedHashSet<E> newLinkedHashSet(Iterator<? extends E> iterator) {
        LinkedHashSet<E> set = newLinkedHashSet();
        addAll(set, iterator);
        return set;
    }

    /**
     * ConcurrentHashSet
     */
    public static <E> Set<E> newConcurrentHashSet() {
        return Collections.newSetFromMap(new ConcurrentHashMap<>());
    }

    public static <E> Set<E> newConcurrentHashSet(Iterable<? extends E> elements) {
        Set<E> set = newConcurrentHashSet();
        addAll(set, elements);
        return set;
    }


    /**
     * TreeSet
     */
    public static <E extends Comparable> TreeSet<E> newTreeSet() {
        return new TreeSet<>();
    }

    public static <E extends Comparable> TreeSet<E> newTreeSet(Iterable<? extends E> iterable) {
        TreeSet<E> set = newTreeSet();
        addAll(set, iterable);
        return set;
    }

    public static <E> TreeSet<E> newTreeSet(Comparator<? super E> comparator) {
        return new TreeSet<>(comparator);
    }

    /**
     * newIdentityHashSet
     */
    public static <E> Set<E> newIdentityHashSet() {
        return Collections.newSetFromMap(MapUtils.newIdentityHashMap());
    }

    /**
     * newIdentityHashSet
     */
    public static <E> CopyOnWriteArraySet<E> newCopyOnWriteArraySet() {
        return new CopyOnWriteArraySet<>();
    }

    public static <E> CopyOnWriteArraySet<E> newCopyOnWriteArraySet(Iterable<? extends E> iterable) {
        Collection<? extends E> elementsCollection = (iterable instanceof Collection) ? cast(iterable) : ListUtils.newArrayList(iterable);
        return new CopyOnWriteArraySet<>(elementsCollection);
    }

    private static <T> boolean addAll(Collection<T> collection, Iterator<? extends T> iterator) {
        boolean wasModified = false;
        while (iterator.hasNext()) {
            wasModified |= collection.add(iterator.next());
        }
        return wasModified;
    }

    @SuppressWarnings("all")
    public static <T> boolean addAll(Collection<T> collection, Iterable<? extends T> iterable) {
        if (iterable instanceof Collection) {
            Collection<? extends T> c = cast(iterable);
            return collection.addAll(c);
        }
        return addAll(collection, iterable.iterator());
    }

    private static <T> Collection<T> cast(Iterable<T> iterable) {
        return (Collection<T>) iterable;
    }
}

