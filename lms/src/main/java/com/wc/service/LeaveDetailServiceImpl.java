//package com.wc.service;
////newadd
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import com.wc.models.LeaveDetails;
//import com.wc.repository.LeaveDetailsRepository;
//
//import java.util.List;
//import java.util.stream.Collectors;
//import java.util.stream.Stream;
//
//@Service
//public class LeaveDetailServiceImpl implements LeaveDetailService {
//
//    @Autowired
//    private LeaveDetailsRepository leaveDetailsRepository;
//
//    @Override
//    public void applyLeave(LeaveDetails leaveDetails) {
//        leaveDetailsRepository.save(leaveDetails);
//    }
//
//    @Override
//    public List<LeaveDetails> getAllLeaves() {
//        return leaveDetailsRepository.findAll();
//    }
//
//    @Override
//    public List<LeaveDetails> getAllLeavesOnStatus(boolean pending, boolean accepted, boolean rejected)  {
//        Stream<LeaveDetails> stream = Stream.empty();
//        
//        if (pending) {
//            stream = Stream.concat(stream, leaveDetailsRepository.findByPending(true).stream());
//        }
//        if (accepted) {
//            stream = Stream.concat(stream, leaveDetailsRepository.findByAcceptRejectFlag(true).stream());
//        }
//        if (rejected) {
//            stream = Stream.concat(stream, leaveDetailsRepository.findByAcceptRejectFlag(false).stream());
//        }
//        
//        return stream.collect(Collectors.toList());
//    }
//
//    @Override
//    public LeaveDetails getLeaveDetailsOnId(int id) {
//        return leaveDetailsRepository.findById(id).orElse(null);
//    }
//
//    @Override
//    public void updateLeaveDetails(LeaveDetails leaveDetails) {
//        leaveDetailsRepository.save(leaveDetails);
//    }
//
//    @Override
//    public List<LeaveDetails> getAllLeavesOfUser(String email) {
//        return leaveDetailsRepository.findByUsername(email);
//    }
//
//    @Override
//    public List<LeaveDetails> getAllActiveLeaves() {
//        return leaveDetailsRepository.findByActive(true);
//    }
//}
