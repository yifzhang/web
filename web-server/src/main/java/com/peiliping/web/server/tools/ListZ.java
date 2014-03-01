package com.peiliping.web.server.tools;

import java.util.ArrayList;
import java.util.Collection;

public class ListZ extends ArrayList<String> {

    private static final long serialVersionUID = 1L;

    private volatile boolean  lock             = false;

    public ListZ put(String e) {
        if (lock)
            return this;
        super.add(e);
        return this;
    }

    public ListZ put(String... s) {
        if (lock)
            return this;
        if (s != null && s.length > 0) {
            for (String i : s)
                put(i);
        }
        return this;
    }

    public static ListZ putAGet(String e) {
        ListZ l = new ListZ();
        return l.put(e);
    }

    public static ListZ putAGet(String... s) {
        ListZ l = new ListZ();
        return l.put(s);
    }

    @Override
    public void add(int index, String element) {
        if (lock)
            return;
        super.add(index, element);
    }

    @Override
    public boolean addAll(Collection<? extends String> c) {
        if (lock)
            return false;
        return super.addAll(c);
    }

    @Override
    public boolean add(String e) {
        if (lock)
            return false;
        return super.add(e);
    }

    @Override
    public boolean addAll(int index, Collection<? extends String> c) {
        if (lock)
            return false;
        return super.addAll(index, c);
    }

    public ListZ lockZ() {
        lock = true;
        return ListZ.this;
    }
}
