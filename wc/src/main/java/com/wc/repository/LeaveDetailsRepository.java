package com.wc.repository;
//new add

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.wc.models.LeaveDetails;

import java.util.List;

@Repository
public interface LeaveDetailsRepository extends JpaRepository<LeaveDetails, Integer> {
    List<LeaveDetails> findByUsername(String username);
    List<LeaveDetails> findByActive(boolean active);
    List<LeaveDetails> findBycancel(boolean cancel);
    List<LeaveDetails> findByAcceptRejectFlag(boolean acceptRejectFlag);
}
