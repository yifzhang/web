package com.peiliping.web.server.dbtools.sequence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import javax.sql.DataSource;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.lang3.Validate;

import com.peiliping.web.server.dbtools.datasource.DynamicDataSource;
import com.peiliping.web.server.dbtools.datasource.DynamicDataSource.DynamicDataSourceUpdateListener;

public class SequenceService {
	
	public static HashMap<String,Class<? extends Sequence>> SequenceClassMap = new HashMap<String,Class<? extends Sequence>>();
	static {
		SequenceClassMap.put(DefaultSequence.class.getCanonicalName(), DefaultSequence.class);
	}
	
	public static HashMap<String,Class<? extends SequenceDao>> SequenceDaoClassMap = new HashMap<String,Class<? extends SequenceDao>>();
	static {
		SequenceDaoClassMap.put(DefaultSequenceDao.class.getCanonicalName(), DefaultSequenceDao.class);
		SequenceDaoClassMap.put(GroupSequenceDao.class.getCanonicalName(), GroupSequenceDao.class);
	}
	
	@Setter
	@Getter
	private int sequenceNum = 1 ;
	
	@Setter
	@Getter
	private DynamicDataSource dataSource;
	
	@Setter
	@Getter
	private String sequenceClazzName = DefaultSequence.class.getCanonicalName() ;
	@Setter
	@Getter
	private String sequenceDaoClazzName =GroupSequenceDao.class.getCanonicalName() ;
	
	private List<Sequence> sequenceList = new ArrayList<Sequence>() ;
	
	@Setter
	@Getter
	private String sequenceName;
	
	public void init() throws InstantiationException, IllegalAccessException{
		Validate.isTrue(sequenceNum>=1);
		Validate.isTrue(dataSource!=null);
		Validate.isTrue(sequenceClazzName!=null);
		dataSource.setListener(new DynamicDataSourceUpdateListener() {
			@Override
			public void call(String k, DataSource dnew, DataSource dold) {
				sequenceList.get(Integer.valueOf(k)).getSequenceDao().setDataSource(dnew);
			}
		});
		for(int i=0 ; i<sequenceNum ;i++){
			Sequence t1 = SequenceClassMap.get(sequenceClazzName).newInstance();
			t1.setName(sequenceName);
			SequenceDao t2 = SequenceDaoClassMap.get(sequenceDaoClazzName).newInstance();
			t2.setDataSource((DataSource)dataSource.getTmp_targetDataSources().get("" + i));
			t1.setSequenceDao(t2);
			sequenceList.add(t1);
		}
	}
	
	public long nextValue() throws SequenceException{
		Random random=new Random();
		int i = random.nextInt(sequenceNum);
		return sequenceList.get(i).nextValue(i,sequenceNum);
	}
	
}
