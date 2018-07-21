package com.project.digitalID.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.project.digitalID.models.User;
import com.project.digitalID.service.UserService;

@RestController
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	UserService userService;
	
	@RequestMapping(value = "/users", method = RequestMethod.GET, headers="Accept=application/json")
	public ResponseEntity<List<User>> getListUser(){
		List<User> list = userService.getListUser();
		
		if(list.size() == 0) {
			return new ResponseEntity<List<User>>(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<List<User>>(list,HttpStatus.OK);
	}
	
	@RequestMapping(value = "{regno}", method = RequestMethod.GET, headers="Accept=application/json")
	public User getUser(@PathVariable("regno") String regno) {
         return userService.findUserById(regno);
	}
}
