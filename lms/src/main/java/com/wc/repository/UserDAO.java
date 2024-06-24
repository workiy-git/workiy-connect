package com.wc.repository;
//new add

import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Repository;

import com.wc.models.UserInfo;

@Repository
public class UserDAO {
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	public void update(UserInfo userInfo) {
		String sql = "UPDATE userinfo SET first_name = ? AND last_name = ? AND phone_number = ? AND address = ? AND zip = ? AND city = ? AND state = ? AND country = ?";
		jdbcTemplate.update(sql, userInfo.getFirstName(), userInfo.getLastName(), userInfo.getPhoneNumber(),userInfo.getAddress(),userInfo.getZip(),userInfo.getCity(),userInfo.getState(),userInfo.getCountry() );
	}

}
