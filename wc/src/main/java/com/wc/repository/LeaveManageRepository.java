	package com.wc.repository;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.wc.models.LeaveDetails;

@Repository(value = "leaveManageRepository")
public interface LeaveManageRepository extends JpaRepository<LeaveDetails, Serializable> {


    @Query(nativeQuery = true, value = "select array_to_json(array_agg(row_to_json(t))) from (select employee_name||' on leave' as title,to_char(from_date,'YYYY-MM-DD') as start,to_char(to_date,'YYYY-MM-DD') as end from leave_details) as t;")
    public Object getAllLeavesAsJsonArray();

    @Query(nativeQuery = true, value = "select * from leave_details where active=true and cancel=false")
    public List<LeaveDetails> getAllActiveLeaves();
    
    @Query(nativeQuery = true, value = "select * from leave_details where withdraw=true ")
    public List<LeaveDetails> getAllWithdrawLeaves();

    @Query(nativeQuery = true, value = "select * from leave_details where username=? order by id desc")
    public List<LeaveDetails> getAllLeavesOfUser(String username);

	public LeaveDetails findById(int id);
	
// v new add
	@Query(nativeQuery = true, value = "select * from leave_details where username=? ")
	public List<LeaveDetails> findByEmail(String username);
	
	@Query(nativeQuery = true, value = "SELECT * FROM leave_details WHERE username = ?1 AND from_date >= TO_TIMESTAMP(?2, 'YYYY-MM-DD') AND to_date <= TO_TIMESTAMP(?3, 'YYYY-MM-DD') AND active = false AND accept_reject_flag = true")
	List<LeaveDetails> findAllByEmailAndYear(String username, String fromyear, String toyear );
	
	@Query(nativeQuery = true, value = "SELECT * FROM leave_details WHERE from_date >= TO_TIMESTAMP(?2, 'YYYY-MM-DD') AND to_date <= TO_TIMESTAMP(?3, 'YYYY-MM-DD') AND active = false AND accept_reject_flag = true")
	List<LeaveDetails> findAllByYear(String fromyear, String toyear );
	
    @Query(nativeQuery = true, value = "select * from leave_details WHERE username = ?1 AND accept_reject_flag = true and cancel=false")
    public List<LeaveDetails> getAllAcceptedLeavesofUser(String username);
// ^ new add


	
}



