package com.wc.repository;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

//import com.wc.models.LeaveDetails;
import com.wc.models.UserInfo;

@Repository(value = "userInfoRepository")
public interface UserInfoRepository extends JpaRepository<UserInfo, Serializable> {

    public UserInfo findByEmail(String email);
    
//    public LeaveDetails findLeaveDetailsByEmail (String email);
    
    public UserInfo findByResetToken(String resetToken);

    public List<UserInfo> findAllByOrderById();

    public UserInfo findById(int id);

    @Transactional
    @Modifying
    @Query(value = "update userinfo set active=false where id=?", nativeQuery = true)
    public void blockUser(int id);

    @Transactional
    @Modifying
    @Query(value = "update userinfo set active=true where id=?", nativeQuery = true)
    public void unBlockUser(int id);
}
