package com.yc.us.utilservice;

import java.util.List;

import com.yc.us.entity.User;

public interface UserService {
	// �г����е��û���Ϣ
	List<User> listUsers();

	// �����û�Id���û�
	User detailUserInfo(User user);

	// ��������
	boolean modifyUserInfo(User user);
}
