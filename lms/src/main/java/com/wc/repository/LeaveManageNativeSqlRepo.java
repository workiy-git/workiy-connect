package com.wc.repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.wc.models.LeaveDetails;

@Repository
public class LeaveManageNativeSqlRepo {

    @PersistenceContext
    EntityManager entityManager;


    @SuppressWarnings("unchecked")
    public List<LeaveDetails> getAllLeavesOnStatus(StringBuffer whereQuery) {

	Query query = entityManager.createNativeQuery("select * from leave_details where " + whereQuery,
		LeaveDetails.class);
	
	return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<LeaveDetails> getUserEmailLeavesOnStatus(String email, StringBuffer whereQuery) {
        // Build the final query string with a space before the whereQuery
        String queryString = "SELECT * FROM leave_details WHERE username = ?1 AND (" + whereQuery.toString() + ")";

        // Create the query
        Query query = entityManager.createNativeQuery(queryString, LeaveDetails.class);
        
        // Set the email parameter
        query.setParameter(1, email);

        // Execute the query and return the results
        return query.getResultList();
    }
}
