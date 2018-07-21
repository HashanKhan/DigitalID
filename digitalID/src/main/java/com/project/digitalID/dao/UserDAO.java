package com.project.digitalID.dao;

import java.util.List;

import com.project.digitalID.models.User;

public interface UserDAO {
	public List<User> getListUser();
	
	public User findUserById(String regno);
}
