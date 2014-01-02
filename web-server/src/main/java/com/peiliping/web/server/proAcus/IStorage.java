package com.peiliping.web.server.proAcus;

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
	public boolean isxiaoyu(double n);
	public boolean isdayu(double n);
	/**
	 *当前容器中的数量 
	 */
	public int size();
}
