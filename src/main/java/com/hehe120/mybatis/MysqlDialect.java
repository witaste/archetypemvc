package com.hehe120.mybatis;

/**
 * MyBatis mysql 实现物理分页
 * 
 */
public class MysqlDialect extends Dialect {

	@Override
	public String getLimitString(String sql, int offset, int limit) {
		sql = sql.trim();
		StringBuffer pagingSelect = new StringBuffer(sql.length() + 100);
		pagingSelect.append(sql);
		pagingSelect.append(" limit " + offset + "," + limit);

		return pagingSelect.toString();
	}

}
