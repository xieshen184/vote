package com.yc.us.web.action;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.RequestAware;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.SessionAware;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ModelDriven;
import com.yc.us.entity.User;
import com.yc.us.utilservice.UserService;
import com.yc.us.utilservice.impl.UserServiceImpl;

public class UserAction implements ModelDriven<User>, SessionAware,
		RequestAware {
	private UserService userService;
	private User user;
	private Map<String, Object> session;
	private Map<String, Object> request;

	public UserAction() {
		userService = new UserServiceImpl();
	}

	public String list() {
		List<User> users = userService.listUsers();
		// ActionContext.getContext().getSession().put("users", users);//
		// 把数据存储到session中
		session.put("users", users);// 把数据存储到session中
		return "success";
	}

	public String detail() {
		user = userService.detailUserInfo(user);
		// ((Map<String,
		// Object>)ActionContext.getContext().get("request")).put("user",
		// user);// 把数据存储到request中
		// session.put("user", user);// 把数据存储到session中
		request.put("user", user);// 把数据存储到request中
		return "success";
	}

	public String update() {
		user = userService.detailUserInfo(user);
		// ((Map<String,
		// Object>)ActionContext.getContext().get("request")).put("user",
		// user);// 把数据存储到request中
		session.put("user", user);// 把数据存储到session中
		 //request.put("user", user);
		return "success";
	}

	public String doupdate() {
		if (userService.modifyUserInfo(user)) {
			return "success";
		} else {
			request.put("errorMsg", "更新数据失败!!!");
			return "fail";
		}
	}

	@Override
	public User getModel() {
		user = new User();
		return user;
	}

	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	@Override
	public void setRequest(Map<String, Object> request) {
		this.request = request;
	}
}
