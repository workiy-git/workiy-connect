package com.wc.service;

import java.util.List;
import java.security.SecureRandom;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.wc.models.LeaveDetails;
//import com.wc.models.LeaveDetails;
import com.wc.models.UserInfo;
import com.wc.repository.UserDAO;
import com.wc.repository.UserInfoRepository;

@Service(value = "userInfoService")
public class UserInfoService {

    @Autowired
    private UserInfoRepository userInfoRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

  //new add
    @Autowired
    private UserDAO UserDAO;
    
    public void updateUser(UserInfo updateUser) {
    	UserDAO.update(updateUser);
    }
    //new^
    public UserInfo getUserInfo() {
        return this.findUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
    }


    public UserInfo findUserByEmail(String email) {
        return userInfoRepository.findByEmail(email);
    }

    public void saveUser(UserInfo userInfo) {
        userInfo.setPassword(bCryptPasswordEncoder.encode(userInfo.getPassword()));
        userInfo.setActive(false);
        userInfoRepository.save(userInfo);
    }
    public void UpdateUser(UserInfo userInfo) {
//        userInfo.setPassword(bCryptPasswordEncoder.encode(userInfo.getPassword()));
        userInfoRepository.save(userInfo);
    }

    public List<UserInfo> getUsers() {
        return userInfoRepository.findAllByOrderById();
    }

    public UserInfo getUserById(int id) {
        return userInfoRepository.findById(id);
    }

    public void deleteUser(int id) {
        userInfoRepository.deleteById(id);
    }

    public void blockUser(int id) {
        userInfoRepository.blockUser(id);
    }

    public void unBlockUser(int id) {
        userInfoRepository.unBlockUser(id);
    }

    // New method to fix the issue
    public String getUserEmailById(int id) {
        UserInfo user = userInfoRepository.findById(id);
        return (user != null) ? user.getEmail() : null;
    }
    
    public UserInfo findByResetToken(String resetToken) {
        return userInfoRepository.findByResetToken(resetToken);
    }
    
    @Service
    public class ResetTokenService {

        public String generateResetToken() {
            byte[] tokenBytes = new byte[32];
            new SecureRandom().nextBytes(tokenBytes);
            return Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
        }
    }
    public List<UserInfo> getAllUsers() {
        return userInfoRepository.findAll(); // Assuming findAll() method exists in UserInfoRepository
    }
    
    public UserInfo updateUserInfo(UserInfo userInfo) {
        return userInfoRepository.save(userInfo); // Assuming save() method exists in UserInfoRepository
    }
    
    public UserInfo getCurrentUser() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        // Assuming you have a method to retrieve UserInfo by email
        return userInfoRepository.findByEmail(userDetails.getUsername());
    }

}
