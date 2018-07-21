package com.project.digitalID.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import com.project.digitalID.models.User;



@Repository
public class UserImpl implements UserDAO{

	NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	@Autowired
	public void setNamedParameterJdbcTemplate(NamedParameterJdbcTemplate namedParameterJdbcTemplate) throws DataAccessException{
		this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
	}

	public List<User> getListUser() {
		List<User> list = new ArrayList<User>();
		String sql = "SELECT name,regno,nic,photo FROM users";
		list = namedParameterJdbcTemplate.query(sql, getSqlParameterByModel(null), new UserMapper());
		return list;
	}
	
	private SqlParameterSource getSqlParameterByModel(User user) {
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		
		if(user != null) {
			paramSource.addValue("name", user.getName());
			paramSource.addValue("regno", user.getRegno());
			paramSource.addValue("nic", user.getNic());
			paramSource.addValue("photo", user.getPhoto());
		}
		return paramSource;
	}
	
	private static final class UserMapper implements RowMapper<User>{
		public User mapRow(ResultSet rs, int rowNum) throws SQLException{
			
			User user = new User();
				user.setName(rs.getString("name"));
				user.setRegno(rs.getString("regno"));
				user.setNic(rs.getString("nic"));
				user.setPhoto(rs.getBlob("photo").toString());
				return user;
		}
	}

	public User findUserById(String regno) {
		User user = new User();
		String sql = "SELECT * FROM users WHERE regno = " + "\""+regno+"\"";
		return namedParameterJdbcTemplate.queryForObject(sql, getSqlParameterByModel(user), new UserMapper());
	}
}
