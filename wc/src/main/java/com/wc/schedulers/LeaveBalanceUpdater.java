package com.wc.schedulers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.wc.models.UserInfo;
import com.wc.service.UserInfoService;

//import com.wc.models.UserInfo;
//import com.wc.service.UserInfoService;

@Component
public class LeaveBalanceUpdater {

    @Autowired
    private UserInfoService userInfoService;

    @Scheduled(cron = "0 0 0 1 * ?") // Cron expression to run at midnight on the first day of every month
    public void updateLeaveBalances() {
        List<UserInfo> allUsers = userInfoService.getAllUsers();
        for (UserInfo user : allUsers) {
            // Add 2 leaves to the current balance
            int currentBalance = user.getLeaveBalance();
            int newBalance = currentBalance + 2;
            user.setLeaveBalance(newBalance);
            userInfoService.updateUserInfo(user);
        }
    }
}