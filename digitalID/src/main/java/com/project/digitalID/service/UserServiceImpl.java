package com.project.digitalID.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.digitalID.dao.UserDAO;
import com.project.digitalID.models.User;

@Service
public class UserServiceImpl implements UserService{

	UserDAO userDao;
	
	@Autowired
	public void setUserDao(UserDAO userDao) {
		this.userDao = userDao;
	}

	public List<User> getListUser() {
		return userDao.getListUser();
	}

	public User findUserById(String regno) {
		return userDao.findUserById(regno);
	}
	
	
}
