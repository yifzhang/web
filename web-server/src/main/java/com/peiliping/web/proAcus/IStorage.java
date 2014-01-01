package com.peiliping.web.proAcus;

public interface IStorage<T> {
	/**
	 * 添加数据
	 */
	public boolean add(T t) ;
	/**
	 * 删除数据
	 */	
	public T del() ;
	/**
	 *容器是否满了 
	 */
	public boolean isFull() ;
	public boolean isEmpty();
	public boolean is3();
	public boolean is5();
	public boolean is10();
	public boolean is20();
	public boolean is50();
	public boolean is80();
	/**
	 *当前容器中的数量 
	 */
	public int size();
}
