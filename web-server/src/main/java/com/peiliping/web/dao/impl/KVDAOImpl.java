package com.peiliping.web.dao.impl;

import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import com.peiliping.web.dao.KVDAO;
import com.peiliping.web.dataobject.KV;

public class KVDAOImpl extends SqlMapClientDaoSupport implements KVDAO {

	@Override
	public KV getKV(String key) {
		KV r = (KV)getSqlMapClientTemplate().queryForObject("KVDAO.select",key);
		return r ;
	}

}
