package com.yc.us.util;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSourceFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 数据访问封装DBHelper.java
 * @author jp
 * @date   2016年03月29日 
 */
public class DBHelper {
	private static DataSource dataSource;
	private static final Logger LOG = LogManager.getLogger(); // 创建日志记录器

	static {
		try {
			// 使用DBCP2连接池
			dataSource = BasicDataSourceFactory.createDataSource(DBProperties
					.getInstance(""));
			/*
			 * Context ctx = new InitialContext(); dataSource = (DataSource)
			 * ctx.lookup("java:comp/env/jdbc/ordering");
			 */
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取与数据库的连接
	 * @return 数据库连接对象
	 */
	public static Connection getConnection() {

		Connection con = null;
		try {
			con = dataSource.getConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return con;
	}

	/**
	 * 设置setXXX参数
	 * @param pstmt
	 * @param params
	 */
	public static void setParams(PreparedStatement pstmt, List<Object> params) {
		if (params == null || params.size() <= 0) {
			return;
		}
		try {
			for (int i = 0; i < params.size(); i++) {
				Object param = params.get(i);
				// 将所有的参数当做String来设置即可
				if (param instanceof Date) {
					pstmt.setTimestamp(i + 1,
							new Timestamp(((Date) param).getTime()));
				} else {
					pstmt.setString(i + 1, param.toString());
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	/**
	 * 单语句(一条语句)的更新
	 * @param sql:要执行的增删改的语句一条
	 * @param params:sql语句中?对应的参数集合
	 */
	public static int doUpdate(String sql, List<Object> params) {
		int r = 0;
		PreparedStatement stmt = null;
		Connection con = null;
		try {
			con = getConnection();
			stmt = con.prepareStatement(sql);
			setParams(stmt, params);
			r = stmt.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			closeAll(null, stmt, con);
		}
		return r;
	}

	/**
	 * 多条语句的更新
	 * @param sql:要执行的增删改的语句一条
	 * @param params:sql语句中?对应的参数集合
	 */
	public static void doUpdate(List<String> sqls, List<List<Object>> params) {
		PreparedStatement stmt = null;
		Connection con = null;
		try {
			con = getConnection();
			// 关闭隐式事务
			con.setAutoCommit(false);
			// 循环执行sqls,以及params设置参数
			for (int i = 0; i < sqls.size(); i++) {
				String sql = sqls.get(i);
				List<Object> p = params.get(i);
				stmt = con.prepareStatement(sql);
				setParams(stmt, p);
				stmt.executeUpdate();
			}

			// 提交事务
			con.commit();
		} catch (SQLException e) {
			// 回滚事务
			try {
				con.rollback();
			} catch (SQLException e1) {
				LOG.error(e);
				throw new RuntimeException(e);
			}
			// e.printStackTrace();
			LOG.error(e);
			throw new RuntimeException(e);
		} finally {
			// 恢复隐式事务
			try {
				con.setAutoCommit(true);
			} catch (SQLException e) {
				LOG.error(e);
				throw new RuntimeException(e);
			}
			closeAll(null, stmt, con);
		}

	}

	/**
	 * 支持聚合函数查询
	 * @param sql
	 * @param params
	 * @return 聚合函数查询结果
	 */
	public static double doSelectFunc(String sql, List<Object> params) {
		double r = 0.0;
		PreparedStatement stmt = null;
		Connection con = null;
		try {
			con = getConnection();
			stmt = con.prepareStatement(sql);
			setParams(stmt, params);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				r = rs.getDouble(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			closeAll(null, stmt, con);
		}
		return r;
	}

	/**
	 * 通过列名查询 弊端:进行算数运算时需要数据类型类型转换
	 * @param sql
	 * @param params
	 * @return
	 */
	public static List<Map<String, String>> doSelect(String sql,
			List<Object> params) {
		List<Map<String, String>> list = null;
		PreparedStatement stmt = null;
		Connection con = null;
		ResultSet rs = null;
		try {
			con = getConnection();
			stmt = con.prepareStatement(sql);
			setParams(stmt, params);
			rs = stmt.executeQuery();
			// 取出所有的列名,用于map中的键
			ResultSetMetaData rsmd = rs.getMetaData();
			List<String> columnNames = new ArrayList<String>();
			for (int i = 0; i < rsmd.getColumnCount(); i++) {
				String cname = rsmd.getColumnName(i + 1);
				columnNames.add(cname);
			}
			list = new ArrayList<Map<String, String>>();
			while (rs.next()) {
				// 一行就是一个map
				Map<String, String> object = new HashMap<String, String>();
				// 在每一行中按列取数据
				for (String cname : columnNames) {
					String value = rs.getString(cname);
					object.put(cname, value);
				}
				list.add(object);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			closeAll(rs, stmt, con);
		}
		return list;
	}

	/**
	 * 关闭数据库的操作
	 * @param rs
	 * @param pstmt
	 * @param con
	 */
	public static void closeAll(ResultSet rs, PreparedStatement pstmt,
			Connection con) {
		try {
			if (rs != null) {
				rs.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		try {
			if (pstmt != null) {
				pstmt.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		try {
			if (con != null) {
				con.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 查询结果
	 * @param sql
	 * @return
	 */
	public static List<Map<String, String>> doSelect(String sql) {
		List<Map<String, String>> list = null;
		ResultSet rs = null;
		Connection con = null;
		PreparedStatement stmt = null;

		try {
			con = getConnection();
			stmt = con.prepareStatement(sql);
			rs = stmt.executeQuery();
			// 取出所有的列名,用于map中的键
			ResultSetMetaData rsmd = rs.getMetaData();
			List<String> columnNames = new ArrayList<String>();
			for (int i = 0; i < rsmd.getColumnCount(); i++) {
				String cname = rsmd.getColumnName(i + 1);
				columnNames.add(cname);
			}
			list = new ArrayList<Map<String, String>>();
			while (rs.next()) {
				// 一行就是一个map
				Map<String, String> object = new HashMap<String, String>();
				// 在每一行中按列取数据
				for (String cname : columnNames) {
					String value = rs.getString(cname);
					object.put(cname, value);
				}
				list.add(object);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			closeAll(rs, stmt, con);
		}
		return list;

	}

	/**
	 * 插入数据
	 * @param t 插入数据的对象
	 * @return 受影响的行数
	 */
	public static <T> int save(T t) {
		return save(t, null, null);
	}

	/**
	 * 插入数据
	 * @param t 插入数据的对象
	 * @param seqName 序列名
	 * @param idColumn 主键名
	 * @return 受影响的行数
	 */
	public static <T> int save(T t, String seqName, String idColumn) {
		Class<?> c = t.getClass();
		List<Object> params = new ArrayList<Object>();
		String tableName = c.getName().substring(
				c.getName().lastIndexOf(".") + 1);
		Method[] ms = c.getMethods(); // 取到指定类对象的类的方法
		String sql = "insert into " + tableName + "(";
		String sqlValue = " values(";
		try {
			for (Method m : ms) {
				String mn = m.getName(); // 取到方法名
				// 取到所有对应属性的get方法
				if (mn.startsWith("is") || mn.startsWith("get")
						&& !mn.equals("getClass")) {
					Object obj = m.invoke(t);
					if (obj != null) {
						// mn.replaceFirst("get|is", "")取到字段名
						sql += mn.replaceFirst("get|is", "") + ",";
						sqlValue += "?,";
						params.add(obj);
					}
				}
			}
		} catch (Exception e) {
			LOG.error(e);
			throw new RuntimeException(e);
		}
		if (seqName != null && idColumn != null) {
			sql = sql + idColumn + ")";
			sqlValue = sqlValue + seqName + ".nextval)";
		} else {
			sql = sql.substring(0, sql.length() - 1) + ")";
			sqlValue = sqlValue.substring(0, sqlValue.length() - 1) + ")";
		}
		sql = sql + sqlValue;
		LOG.debug(sql);
		return doUpdate(sql, params);
	}

	/**
	 * 查询数据
	 * @param t 要查询的记录的匹配结果
	 * @return 返回一个对象
	 */
	public static <T> T query(T t) {
		List<T> ts = queryList(t);
		return ts != null && ts.size() > 0 ? ts.get(0) : null;
	}

	/**
	 * 查询数据
	 * @param t 要查询的记录的匹配结果
	 * @return 返回所有结果的对象集合
	 */
	public static <T> List<T> queryList(T t) {
		Class<?> c = t.getClass();
		List<T> ts = null;
		List<Object> params = new ArrayList<Object>();
		String tableName = c.getName().substring(
				c.getName().lastIndexOf(".") + 1);
		Method[] ms = c.getMethods(); // 取到指定类对象的类的方法
		String sql = "select * from " + tableName;
		String sqlValue = "";
		try {
			for (Method m : ms) {
				String mn = m.getName(); // 取到方法名
				// 取到所有对应属性的get方法
				if (mn.startsWith("is") || mn.startsWith("get")
						&& !mn.equals("getClass")) {
					Object obj = m.invoke(t);
					if (obj != null) {
						// mn.replaceFirst("get|is", "")取到字段名
						sqlValue += mn.replaceFirst("get|is", "") + "=? and "; // 拼接sql语句
						params.add(obj); // 拼接对应?的值
					}
				}
			}
		} catch (Exception e) {
			LOG.error(e);
			throw new RuntimeException(e);
		}
		sqlValue = sqlValue.length() == 0 ? "" : (" where " + sqlValue
				.substring(0, sqlValue.length() - 5));
		sql = sql + sqlValue;
		LOG.debug(sql);
		try {
			List<Map<String, String>> rs = doSelect(sql, params);
			ts = new ArrayList<T>();
			for (Map<String, String> results : rs) {
				T temp = (T) c.newInstance();
				for (Method m : ms) {
					String mn = m.getName(); // 取到属性名
					if (mn.startsWith("set")) {
						String typeName = m.getParameterTypes()[0].getName();
						String colName = mn.replaceFirst("set", "")
								.toUpperCase();
						if (typeName.equals(Integer.class.getName())) {
							m.invoke(temp,
									Integer.valueOf(results.get(colName)));
						} else if (typeName.equals(Double.class.getName())) {
							m.invoke(temp, Double.valueOf(results.get(colName)));
						} else {
							m.invoke(temp, results.get(colName));
						}
					}
				}
				ts.add(temp);
			}
		} catch (Exception e) {
			LOG.error(e);
			throw new RuntimeException(e);
		}
		return ts;
	}

	/**
	 * 更新数据
	 * @param t  要更新的记录的匹配结果
	 * @param idColumn 更新一条记录, 为空更新所有数据
	 * @return 受影响的行数
	 */
	public static <T> int update(T t, String idColumn) {
		Class<?> c = t.getClass();
		List<Object> params = new ArrayList<Object>();
		String tableName = c.getName().substring(
				c.getName().lastIndexOf(".") + 1);
		Method[] ms = c.getMethods(); // 取到指定类对象的类的方法
		String sql = "update " + tableName + " set ";
		String whereSql = "";
		try {
			for (Method m : ms) {
				String mn = m.getName(); // 取到方法名
				if (mn.startsWith("is") || mn.startsWith("get")
						&& !mn.equals("getClass")) {
					Object obj = m.invoke(t);
					String id = mn.replaceFirst("get|is", "");
					LOG.debug(id + " == " + obj);
					if (id.equals(idColumn) && obj == null) {
						throw new RuntimeException("没有找当主键字段值!!!");
					}
					if (id.equalsIgnoreCase(idColumn)) {
						whereSql = " where " + idColumn + "=?";
						params.add(obj);
						continue;
					}
					if (obj != null) {
						sql += id + "=?,";
						params.add(obj);
					}
				}
			}
		} catch (Exception e) {
			LOG.error(e);
			throw new RuntimeException(e);
		}
		sql = sql.substring(0, sql.length() - 1);
		sql += whereSql;
		LOG.debug(sql);
		return doUpdate(sql, params);
	}
	
	/**
	 * 
	 * @param t 要删除的记录的匹配结果
	 * @return 受影响的行数
	 */
	public static <T> int delete(T t) {
		Class<?> c = t.getClass();
		List<Object> params = new ArrayList<Object>();
		String tableName = c.getName().substring(
				c.getName().lastIndexOf(".") + 1);
		Method[] ms = c.getMethods(); // 取到指定类对象的类的方法
		String sql = "delete from " + tableName;
		String sqlValue = "";
		try {
			for (Method m : ms) {
				String mn = m.getName(); // 取到方法名
				// 取到所有对应属性的get方法
				if (mn.startsWith("is") || mn.startsWith("get")
						&& !mn.equals("getClass")) {
					Object obj = m.invoke(t);
					if (obj != null) {
						// mn.replaceFirst("get|is", "")取到字段名
						sqlValue += mn.replaceFirst("get|is", "") + "=? and "; // 拼接sql语句
						params.add(obj); // 拼接对应?的值
					}
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		sqlValue = sqlValue.length() == 0 ? "" : (" where " + sqlValue
				.substring(0, sqlValue.length() - 5));
		sql = sql + sqlValue;
		LOG.debug(sql);
		return doUpdate(sql, params);
	}
}