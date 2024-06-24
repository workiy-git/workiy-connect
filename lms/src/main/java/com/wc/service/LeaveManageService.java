package com.wc.service;

import java.util.Collections;
import java.util.List;
import java.time.DayOfWeek;
import java.time.LocalDate;
//import java.time.Period;
import java.time.ZoneId;
//import java.time.temporal.ChronoUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wc.models.LeaveDetails;
import com.wc.models.UserInfo;
import com.wc.repository.LeaveManageNativeSqlRepo;
import com.wc.repository.LeaveManageRepository;
import com.wc.repository.LeaveDao;
import com.wc.repository.LeaveDetailsRepository;

@Service(value = "leaveManageService")
public class LeaveManageService {

    @Autowired
    private LeaveManageRepository leaveManageRepository;

    @Autowired
    private LeaveManageNativeSqlRepo leaveManageNativeRepo;
    
    @Autowired
    private LeaveDetailsRepository leaveDetailsRepository;
    
//new add
    @Autowired
    private LeaveDao leaveDao;

    public LeaveDetails getLeaveDetailsById(int leaveId) {
        return leaveDao.findById(leaveId);
    }

    public void applyLeave1(LeaveDetails leaveDetails) {
        leaveDao.update(leaveDetails);
    }

    public void updateLeave(int id, LeaveDetails leaveDetails) {
    	LocalDate fromDate = leaveDetails.getFromDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate toDate = leaveDetails.getToDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        int workingDays = calculateWorkingDays(fromDate, toDate);
        leaveDetails.setDuration(workingDays);
        leaveDao.update(leaveDetails);
    }
    //new^

    public void cancelLeave(int id) {
        LeaveDetails leaveDetails = leaveDao.findById(id);
        if (leaveDetails != null && leaveDetails.isActive()) {
        	leaveDetails.setActive(false);
            leaveDetails.setcancel(true);
            leaveDao.update(leaveDetails);
        }
    }
    public void withdrawLeave(int id) {
        LeaveDetails leaveDetails = leaveDao.findById(id);
        if (leaveDetails != null && leaveDetails.isAcceptRejectFlag()) {
            leaveDetails.setwithdraw(true);
            leaveDao.update(leaveDetails);
        }
    }
   
// V new add    
    public List<LeaveDetails> getLeavesOfUserByName(String name) {
        String[] names = name.split(" ");
        if (names.length != 2) {
            return Collections.emptyList();
        }
        String firstName = names[0];
        String lastName = names[1];
        UserInfo user = leaveDao.findUserByName(firstName, lastName);
        if (user != null) {
            return leaveManageRepository.getAllLeavesOfUser(user.getEmail());
        } else {
            return Collections.emptyList();
        }
    }
    
    public List<LeaveDetails> getLeavesOfUserByEmailAndYear (String email,String year){
    	
    	String fromdateFormat = year + "-01-01";
        String todateFormat = year + "-12-31";
        return leaveManageRepository.findAllByEmailAndYear(email,fromdateFormat , todateFormat);
    }
    
    public List<LeaveDetails> getLeavesOfUserByNameAndYear(String name, String year) {
        String[] names = name.split(" ");
        if (names.length != 2) {
            return Collections.emptyList();
        }
        String firstName = names[0];
        String lastName = names[1];
        String fromdateFormat = year + "-01-01";
        String todateFormat = year + "-12-31";
        System.out.println("this is thee year " + fromdateFormat);
        UserInfo user = leaveDao.findUserByName(firstName, lastName);
        if (user != null) {
            return leaveManageRepository.findAllByEmailAndYear(user.getEmail(), fromdateFormat , todateFormat);
        } else {
            return Collections.emptyList();
        }
    }
    
// ^ new add
    
    @SuppressWarnings("deprecation")
    public void applyLeave(LeaveDetails leaveDetails) {

//	int duration = leaveDetails.getToDate().getDate() - leaveDetails.getFromDate().getDate();
//	leaveDetails.setDuration(duration + 1);
    	LocalDate fromDate = leaveDetails.getFromDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate toDate = leaveDetails.getToDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        int workingDays = calculateWorkingDays(fromDate, toDate);
        leaveDetails.setDuration(workingDays);
	leaveDetails.setActive(true);
	leaveManageRepository.save(leaveDetails);
    }
    
    private int calculateWorkingDays(LocalDate fromDate, LocalDate toDate) {
        int workingDays = 0;
        LocalDate currentDate = fromDate;

        while (!currentDate.isAfter(toDate)) {
            if (currentDate.getDayOfWeek() != DayOfWeek.SATURDAY && currentDate.getDayOfWeek() != DayOfWeek.SUNDAY) {
                workingDays++;
            }
            currentDate = currentDate.plusDays(1);
        }

        return workingDays;
    }

    public List<LeaveDetails> getAllLeaves() {
//    return null;
	return leaveManageRepository.findAll();
    }

    public List<LeaveDetails> getUserLeaves(String gmail) {
//      return null;
    	var email = gmail;
  	return leaveManageRepository.findByEmail(email);
      }
    
    public LeaveDetails getLeaveDetailsOnId(int id) {

	return leaveManageRepository.findById(id);
    }

    public void updateLeaveDetails(LeaveDetails leaveDetails) {

	leaveManageRepository.save(leaveDetails);

    }
    public List<LeaveDetails> getAllWithdrawLeaves() {

    return leaveManageRepository.getAllWithdrawLeaves();
     }

    public List<LeaveDetails> getAllActiveLeaves() {

	return leaveManageRepository.getAllActiveLeaves();
    }

    public List<LeaveDetails> getAllLeavesOfUser(String username) {

	return leaveManageRepository.getAllLeavesOfUser(username);

    }

    public List<LeaveDetails> getAllLeavesOnStatus(boolean pending, boolean accepted, boolean rejected, boolean cancel) {

	StringBuffer whereQuery = new StringBuffer();
	if (pending)
	    whereQuery.append("(active=true and cancel=false) or ");
	if (accepted)
	    whereQuery.append("(active=false and accept_reject_flag=true) or ");
	if (rejected)
	    whereQuery.append("(active=false and accept_reject_flag=false) or ");
	if (cancel)
		whereQuery.append("cancel=true or ");

	whereQuery.append(" 1=0");
	
	return leaveManageNativeRepo.getAllLeavesOnStatus(whereQuery);
    }
    
    public List<LeaveDetails> getUserEmailLeavesOnStatus(String gmail,boolean pending, boolean accepted, boolean rejected, boolean cancel) {
    	
    	var email = gmail;
    	StringBuffer whereQuery = new StringBuffer();
    	if (pending)
    	    whereQuery.append("(active=true and cancel=false) or ");
    	if (accepted)
    	    whereQuery.append("(active=false and accept_reject_flag=true) or ");
    	if (rejected)
    	    whereQuery.append("(active=false and accept_reject_flag=false) or ");
    	if (cancel)
    		whereQuery.append("cancel=true or ");

    	whereQuery.append(" 1=0");
    	
    	return leaveManageNativeRepo.getUserEmailLeavesOnStatus(email,whereQuery);
        }
}