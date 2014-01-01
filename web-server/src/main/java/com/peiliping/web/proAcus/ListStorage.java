package com.peiliping.web.proAcus;

import java.util.ArrayList;
import java.util.List;

public class ListStorage<V> implements IStorage<V> {

	private int capacity;		//容积

	private List<V> storage;	//容器

	public ListStorage(int capacity) {
		this.capacity = capacity;
		this.storage = new ArrayList<V>();
	}

	@Override
	public synchronized boolean add(V v) {
		if (isFull()) {
			return false;
		}
		storage.add(v);
		return true;
	}

	@Override
	public synchronized V del() {
		if (storage == null || storage.size() == 0) {
			return null;
		}
		V v = storage.get(0);
		storage.remove(0);
		return v;
	}
	
	@Override
	public boolean isFull() {
		return storage == null ? true : storage.size() >= capacity;
	}
	@Override
	public boolean is80() {
		return storage == null ? false : storage.size()/Double.valueOf(capacity) >=0.8;
	}
	@Override
	public boolean is50() {
		return storage == null ? false : storage.size()/Double.valueOf(capacity) >=0.5;
	}
	@Override
	public boolean is20() {
		return storage == null ? false : storage.size()/Double.valueOf(capacity) <=0.2;
	}
	@Override
	public boolean is10() {
		return storage == null ? false : storage.size()/Double.valueOf(capacity) <=0.1;
	}
	@Override
	public boolean is5() {
		return storage == null ? false : storage.size()/Double.valueOf(capacity) <=0.05;
	}
	@Override
	public boolean is3() {
		return storage == null ? false : storage.size()/Double.valueOf(capacity) <=0.05;
	}
	@Override
	public boolean isEmpty(){
		return storage == null ? true : storage.size() == 0 ;
	}
	@Override
	public int size(){
		return storage==null ? 0 : storage.size();
	}
}
