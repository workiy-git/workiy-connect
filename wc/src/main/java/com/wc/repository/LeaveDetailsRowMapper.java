package com.wc.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import com.wc.models.LeaveDetails;

public class LeaveDetailsRowMapper implements RowMapper<LeaveDetails> {
    @Override
    public LeaveDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
        LeaveDetails leaveDetails = new LeaveDetails();
        leaveDetails.setId(rs.getInt("id"));
        leaveDetails.setFromDate(rs.getDate("from_date"));
        leaveDetails.setToDate(rs.getDate("to_date"));
        leaveDetails.setReason(rs.getString("reason"));
        leaveDetails.setUsername(rs.getString("username"));
        leaveDetails.setEmployeeName(rs.getString("employee_name"));
        leaveDetails.setDuration(rs.getInt("duration"));
        leaveDetails.setActive(rs.getBoolean("active"));
        leaveDetails.setAcceptRejectFlag(rs.getBoolean("accept_reject_flag"));
        leaveDetails.setcancel(rs.getBoolean("cancel"));
        return leaveDetails;
    }
}
