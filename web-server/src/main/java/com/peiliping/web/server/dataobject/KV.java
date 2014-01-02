package com.peiliping.web.server.dataobject;

import lombok.Getter;
import lombok.Setter;

import org.apache.ibatis.type.Alias;

@Alias("KV")
public class KV {
	
	@Setter
	@Getter
	private String k;
	
	@Setter
	@Getter
	private String v;

}
