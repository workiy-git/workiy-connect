package com.wc.repository;

//import java.io.Serializable;
//import java.util.List;

//import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
//import org.springframework.stereotype.Service;

import com.wc.forms.RatingForm;

@Repository
public interface PMSRepository extends CrudRepository<RatingForm, Long> {
	
	boolean existsByUserIdAndYearAndMonth(int userId, int year, String month);
    // Add custom queries or methods if needed
}
