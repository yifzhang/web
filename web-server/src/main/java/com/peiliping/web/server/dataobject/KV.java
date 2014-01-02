package com.peiliping.web.dataobject;

import org.apache.ibatis.type.Alias;

import lombok.Getter;
import lombok.Setter;

@Alias("KV")
public class KV {
	
	@Setter
	@Getter
	private String k;
	
	@Setter
	@Getter
	private String v;

}
