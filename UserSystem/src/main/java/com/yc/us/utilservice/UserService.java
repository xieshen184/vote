package com.yc.us.utilservice;

import java.util.List;

import com.yc.us.entity.User;

public interface UserService {
	// 列出所有的用户信息
	List<User> listUsers();

	// 根据用户Id找用户
	User detailUserInfo(User user);

	// 更新数据
	boolean modifyUserInfo(User user);
}
