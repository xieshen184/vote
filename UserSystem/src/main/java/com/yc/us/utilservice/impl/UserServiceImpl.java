package com.yc.us.utilservice.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.yc.us.entity.User;
import com.yc.us.util.DBHelper;
import com.yc.us.utilservice.UserService;

public class UserServiceImpl implements UserService {

	@Override
	public List<User> listUsers() {
		List<User> users = null;// 转换成对象集合

		// 取数据
		String sql = "select * from profile";
		List<Map<String, String>> results = DBHelper.doSelect(sql);

		if (results != null && results.size() > 0) {
			users = new ArrayList<User>();

			for (Map<String, String> map : results) {
				users.add(new User(Integer.valueOf(map.get("ID")), map
						.get("NAME"), map.get("BIRTHDAY"), map.get("GENDER"),
						map.get("CAREER"), map.get("ADDRESS"), map
								.get("MOBILE")));
			}
		}
		return users;
	}

	@Override
	public User detailUserInfo(User user) {
		// 取数据
		String sql = "select * from profile where id=" + user.getId();
		List<Map<String, String>> results = DBHelper.doSelect(sql);
		if (results != null && results.size() > 0) {

			Map<String, String> map = results.get(0);
			return new User(Integer.valueOf(map.get("ID")), map.get("NAME"),
					map.get("BIRTHDAY"), map.get("GENDER"), map.get("CAREER"),
					map.get("ADDRESS"), map.get("MOBILE"));
		}

		return null;
	}

	@Override
	public boolean modifyUserInfo(User user) {
		String sql = "update profile set name =?,birthday =?, gender =?,career =?, address =?, mobile =? where id="
				+ user.getId();
		List<Object> params = Arrays.asList(new Object[] { user.getName(),
				user.getBirthday(), user.getGender(), user.getCareer(),
				user.getAddress(), user.getMobile() });

		try {
			DBHelper.doUpdate(sql, params);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

}
