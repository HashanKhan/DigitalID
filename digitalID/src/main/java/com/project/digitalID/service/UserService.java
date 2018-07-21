package com.project.digitalID.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.project.digitalID.models.User;

@Service
public interface UserService {
	public List<User> getListUser();
	
	public User findUserById(String regno);
}
