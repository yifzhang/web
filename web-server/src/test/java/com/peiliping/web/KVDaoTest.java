package com.peiliping.web;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.peiliping.web.server.dao.KVDAO;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"file:src/test/resources/test-dbsource.xml"})
public class KVDaoTest {
	@Autowired
	private KVDAO kvDAO;

	@Test
	public void test_KV(){
		System.out.println(kvDAO.getKV("1").getK());
	}	
	
	
}
