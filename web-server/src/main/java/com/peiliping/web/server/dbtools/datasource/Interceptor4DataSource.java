package com.peiliping.web.server.dbtools.datasource;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import lombok.Setter;

import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Intercepts({
                @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
                @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
                @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class,
                                BoundSql.class})})
public class Interceptor4DataSource implements Interceptor {

    public volatile boolean     flag                  = true;

    private Map<String, Object> cache                 = new HashMap<String, Object>();

    private Map<String, String> vs                    = new HashMap<String, String>();

    private static Logger       logger                = LoggerFactory.getLogger(Interceptor4DataSource.class);

    private static final Object SKIP                  = new Object();

    @Setter
    private boolean             cleanThreadLocalFirst = false;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        if (cleanThreadLocalFirst) {
            DataSourceContextHolder.clearDataSourceName();
        }
        if (!flag) {
            return invocation.proceed();
        }

        MappedStatement ms = (MappedStatement) invocation.getArgs()[0];
        if (cache.get(ms.getId()) == null) {
            BoundSql b = ms.getBoundSql(invocation.getArgs()[1]);
            logger.warn(b.getSql());
            String tablename = SQLParser.findTableName(b.getSql());
            String ds = vs.get(tablename);
            if (ds == null) {
                cache.put(ms.getId(), SKIP);
            } else {
                DataSourceContextHolder.setDataSourceName(ds);
                cache.put(ms.getId(), ds);
            }
        } else if (cache.get(ms.getId()) == SKIP) {

        } else {
            DataSourceContextHolder.setDataSourceName((String) cache.get(ms.getId()));
        }
        Object o = null;
        try {
            o = invocation.proceed();
        } finally {
            DataSourceContextHolder.clearDataSourceName();
        }
        return o;
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
        if (properties == null || properties.size() == 0)
            return;
        for (Entry<Object, Object> e : properties.entrySet()) {
            vs.put((String) e.getKey(), (String) e.getValue());
        }
    }

}
