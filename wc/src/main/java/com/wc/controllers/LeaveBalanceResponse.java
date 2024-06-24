package com.wc.controllers;

public class LeaveBalanceResponse {
    private int leaveBalance;

    public LeaveBalanceResponse(int leaveBalance) {
        this.leaveBalance = leaveBalance;
    }

    public int getLeaveBalance() {
        return leaveBalance;
    }

    public void setLeaveBalance(int leaveBalance) {
        this.leaveBalance = leaveBalance;
    }
}
