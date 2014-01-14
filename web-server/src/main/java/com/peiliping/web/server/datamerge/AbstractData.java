package com.peiliping.web.server.datamerge;

import lombok.Getter;

public abstract class AbstractData {
	@Getter
	protected long id ;
	@Getter
	protected String name ;
	@Getter
	protected String type ;
	@Getter
	protected double count ;//次数
	@Getter
	protected double cost ;//消耗时间
	@Getter
	protected double max ;
	@Getter
	protected double min ;
	@Getter
	protected long expiretime ;//到期时间点
	
	public AbstractData(long id,String name,String type,double count,double cost,double max,double min,long expiretime) {
		this.id = id ;
		this.name = name ;
		this.type = type ;
		this.count = count;
		this.cost = cost ;
		this.max = max ;
		this.min = min ;
		this.expiretime = expiretime ;
	}
	
	/**
	 * 要返回一个新创建的data对象
	 * @param data
	 * @return
	 */
	public abstract AbstractData merge(AbstractData data);
	
	public abstract void cusumerAction();
}
