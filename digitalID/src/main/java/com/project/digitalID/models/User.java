package com.project.digitalID.models;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

@Entity
@Table(name = "users")
public class User {
	
	@Id
	@Column(name = "regno")
	private String regno;
	
	@Column(name = "name")
	private String name;
	
	@Column(name = "nic")
	private String nic;
	
	@Column(name = "photo")
	@Lob
	@Basic(fetch=FetchType.LAZY)
	private byte[] photo;
	
	@Column(name = "username")
	private String username;
	
	@Column(name = "password")
	private String password;
	
	public User() {
		super();
	}
	
	public User(String regno) {
		super();
		this.regno = regno;
	}
	
	public String getRegno() {
		return regno;
	}

	public void setRegno(String regno) {
		this.regno = regno;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNic() {
		return nic;
	}

	public void setNic(String nic) {
		this.nic = nic;
	}

	public byte[] getPhoto() {
		return photo;
	}

	public void setPhoto(byte[] photo) {
		this.photo = photo;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
