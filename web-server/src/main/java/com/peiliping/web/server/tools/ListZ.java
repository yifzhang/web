package com.peiliping.web.server.tools;

import java.util.ArrayList;
import java.util.Collection;

public class ListZ<E> extends ArrayList<E> {

    private static final long serialVersionUID = 1L;

    private volatile boolean  lock             = false;

    public ListZ<E> put(E e) {
        if (lock)
            return this;
        super.add(e);
        return this;
    }

    public ListZ<E> put(E... s) {
        if (lock)
            return this;
        if (s != null && s.length > 0) {
            for (E i : s)
                put(i);
        }
        return this;
    }

    public static <E> ListZ<E> newListZ(E e) {
        ListZ<E> l = new ListZ<E>();
        return l.put(e);
    }

    public static <E> ListZ<E> newListZ(E... e) {
        ListZ<E> l = new ListZ<E>();
        return l.put(e);
    }

    public static <E> ListZ<E> newListZ() {
        return new ListZ<E>();
    }

    @Override
    public void add(int index, E element) {
        if (lock)
            return;
        super.add(index, element);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        if (lock)
            return false;
        return super.addAll(c);
    }

    @Override
    public boolean add(E e) {
        if (lock)
            return false;
        return super.add(e);
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        if (lock)
            return false;
        return super.addAll(index, c);
    }

    public ListZ<E> lockZ() {
        lock = true;
        return ListZ.this;
    }

    public E pickUpFirstOne(PickUpFunction<E> pick) {
        for (E e : this) {
            if (pick.pickUp(e)) {
                return e;
            }
        }
        return null;
    }

    public E pickUpLastOne(PickUpFunction<E> pick) {
        for (int i = size() - 1; i >= 0; i--) {
            if (pick.pickUp(get(i))) {
                return get(i);
            }
        }
        return null;
    }

    public ListZ<E> pickUpSome(PickUpFunction<E> pick) {
        ListZ<E> l = ListZ.newListZ();
        for (E e : this) {
            if (pick.pickUp(e)) {
               l.put(e);
            }
        }
        return l;
    }

    public interface PickUpFunction<V> {
        boolean pickUp(V v);
    }

}
