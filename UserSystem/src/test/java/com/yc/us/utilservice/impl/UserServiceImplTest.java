package com.yc.us.utilservice.impl;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import com.yc.us.entity.User;
import com.yc.us.utilservice.UserService;

public class UserServiceImplTest {

	@Test
	public void testListUsers() {
		UserService us = new UserServiceImpl();
		List<User> users = us.listUsers();
		System.out.println(users);
		assertNotNull("没有找到数据！！！",users);
	}

}
