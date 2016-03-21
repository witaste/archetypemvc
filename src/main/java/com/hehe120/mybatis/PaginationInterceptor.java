package com.hehe120.mybatis;

import java.sql.Connection;
import java.util.Properties;

import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.factory.DefaultObjectFactory;
import org.apache.ibatis.reflection.wrapper.DefaultObjectWrapperFactory;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Intercepts({ @Signature(type = StatementHandler.class, method = "prepare", args = { Connection.class }) })
public class PaginationInterceptor implements Interceptor {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	private Properties properties;

	public Object intercept(Invocation invocation) throws Throwable {
		try {
			StatementHandler statementHandler = (StatementHandler) invocation
					.getTarget();
			MetaObject metaStatementHandler = MetaObject.forObject(
					statementHandler, new DefaultObjectFactory(),
					new DefaultObjectWrapperFactory());

			RowBounds rowBounds = (RowBounds) metaStatementHandler
					.getValue("delegate.rowBounds");
			if (rowBounds == null || rowBounds == RowBounds.DEFAULT) {
				return invocation.proceed();
			}

			String originalSql = (String) metaStatementHandler
					.getValue("delegate.boundSql.sql");


			Configuration configuration = (Configuration) metaStatementHandler
					.getValue("delegate.configuration");

			Dialect.Type databaseType = null;
			try {
				databaseType = Dialect.Type.valueOf(properties.getProperty("dialect").toUpperCase());
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
			if (databaseType == null) {
				throw new RuntimeException(
						"the value of the dialect property in configuration.xml is not defined : "
								+ configuration.getVariables().getProperty(
										"dialect"));
			}
			Dialect dialect = null;
			switch (databaseType) {
			case ORACLE:
				dialect = new OracleDialect();
				break;
			case MYSQL:// 需要实现Mysql 物理分页逻辑 
				dialect = new MysqlDialect();
				break;

			}

			metaStatementHandler.setValue("delegate.boundSql.sql", dialect
					.getLimitString(originalSql, rowBounds.getOffset(),
							rowBounds.getLimit()));
			metaStatementHandler.setValue("delegate.rowBounds.offset",
					RowBounds.NO_ROW_OFFSET);
			metaStatementHandler.setValue("delegate.rowBounds.limit",
					RowBounds.NO_ROW_LIMIT);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return invocation.proceed();
	}

	public Object plugin(Object target) {
		return Plugin.wrap(target, this);
	}

	public void setProperties(Properties properties) {
		this.properties = properties;
	}

}
