package com.peiliping.web.DAO;

import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

public class KVDAOImpl extends SqlMapClientDaoSupport implements KVDAO {

	@Override
	public KV getKV(String key) {
		KV r = (KV)getSqlMapClientTemplate().queryForObject("KVDAO.select",key);
		return r ;
	}

}
