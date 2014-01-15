package com.peiliping.web.server.datamerge;

import lombok.Getter;

public abstract class AbstractData {
	@Getter
	protected String name ;  //保存在MAP的key
	@Getter
	protected long expiretime ;//到期时间点
	
	public AbstractData(String name,long expiretime) {
		this.name = name ;
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
