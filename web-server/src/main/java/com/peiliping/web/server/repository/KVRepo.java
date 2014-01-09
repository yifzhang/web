package com.peiliping.web.server.repository;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.peiliping.web.server.dao.KVDAO;
import com.peiliping.web.server.dataobject.KV;

@Repository
public class KVRepo {
	@Autowired
	private KVDAO kvDao;
	
	public KV getKV(String key){
		if(StringUtils.isNotBlank(key)){
			DataSourceContextHolder.setDataSourceName("vpsds");
			return kvDao.getKV(key);
		}else 
			return null;
	}

}
