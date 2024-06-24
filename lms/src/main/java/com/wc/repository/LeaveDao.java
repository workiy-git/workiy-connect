package com.wc.repository;

//new add

import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.wc.models.LeaveDetails;
import com.wc.models.UserInfo;

@Repository
public class LeaveDao {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@SuppressWarnings("deprecation")
	public LeaveDetails findById(int leaveId) {
		String sql = "SELECT * FROM leave_details WHERE id = ?";
		return jdbcTemplate.queryForObject(sql, new Object[]{leaveId}, new LeaveDetailsRowMapper());
	}

	//    public void save(LeaveDetails leaveDetails) {
	//        String sql = "INSERT INTO leave_details ( from_date, to_date, reason) VALUES ( ?, ?, ?)";
	//        jdbcTemplate.update(sql, leaveDetails.getFromDate(), leaveDetails.getToDate(), leaveDetails.getReason());
	//    }

	public void update(LeaveDetails leaveDetails) {
		String sql = "UPDATE leave_details SET from_date = ?, to_date = ?, reason = ?,cancel = ?,withdraw = ?,duration = ? WHERE id = ?";
		jdbcTemplate.update(sql, leaveDetails.getFromDate(), leaveDetails.getToDate(), leaveDetails.getReason(), leaveDetails.iscancel(),leaveDetails.iswithdraw(),leaveDetails.getDuration(), leaveDetails.getId());
	}
	//v new add
	@SuppressWarnings("deprecation")
	public UserInfo findUserByName(String firstName, String lastName) {
		String sql = "SELECT * FROM userinfo WHERE first_name = ? AND last_name = ?";
		return jdbcTemplate.queryForObject(sql, new Object[]{firstName, lastName}, new UserInfoRowMapper());		
	}
	//^ new add

}



