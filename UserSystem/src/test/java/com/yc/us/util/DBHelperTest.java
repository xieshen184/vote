package com.yc.us.util;

import static org.junit.Assert.*;

import java.sql.Connection;

import org.junit.Test;

public class DBHelperTest {

	@Test
	public void testGetConnection() {
		Connection con = DBHelper.getConnection();
		assertNotNull("数据库连接失败！！！", con);
	}

}
