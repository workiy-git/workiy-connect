package com.wc.controllers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
//import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.wc.models.LeaveDetails;
import com.wc.models.UserInfo;
import com.wc.service.LeaveManageService;
import com.wc.service.UserInfoService;

@Controller
public class LeaveManageController {

	@Autowired
	private LeaveManageService leaveManageService;

	@Autowired
	private UserInfoService userInfoService;

	private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.S";
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);


	@RequestMapping(value = "/user/apply-leave", method = RequestMethod.GET)
	public ModelAndView applyLeave(ModelAndView mav) {
		UserInfo userInfo = userInfoService.getUserInfo();
		int leaveBalance = userInfo.getLeaveBalance();
		//    	    System.out.println("hi this is test" + leaveBalance);	    
		mav.addObject("leaveDetails", new LeaveDetails());       
		mav.addObject("leaveBalance", leaveBalance);
		mav.setViewName("applyLeave");
		return mav;
	}


	@RequestMapping(value = "/user/apply-leave", method = RequestMethod.POST)
	public ModelAndView submitApplyLeave(ModelAndView mav, @Valid LeaveDetails leaveDetails, BindingResult bindingResult) {
		UserInfo userInfo = userInfoService.getUserInfo();
		if (bindingResult.hasErrors()) {
			mav.setViewName("applyLeave");
		} else {
			LocalDate fromdate = leaveDetails.getFromDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			LocalDate todate = leaveDetails.getToDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

			// Check if fromDate is after toDate
			if (fromdate.isAfter(todate)) {
				mav.addObject("NoBalanceMessage", "From date cannot be after To date. Please select the correct dates.");
				mav.setView(new RedirectView("/user/apply-leave"));
			} 
			else {
				leaveDetails.setUsername(userInfo.getEmail());
				leaveDetails.setEmployeeName(userInfo.getFirstName() + " " + userInfo.getLastName());
				int duration = calculateWorkingDays(fromdate, todate);
				int currentBalance = userInfo.getLeaveBalance(); 
				int newBalance = currentBalance - duration;
				userInfo.setLeaveBalance(newBalance);  // Assuming setLeaveBalance() method exists;
				if(newBalance<0){
					mav.addObject("NoBalanceMessage", "Your LeaveBalance is low !"); 
					mav.setView(new RedirectView("/user/apply-leave"));
				}
				else {

					userInfoService.updateUserInfo(userInfo); // Update the user info in the database
					leaveManageService.applyLeave(leaveDetails);
					mav.addObject("successMessage", "Your Leave Request is registered!");
					mav.setView(new RedirectView("/user/lms"));
				}
			}
		}
		return mav;
	}



	@RequestMapping(value = "/user/edit-leave/{id}", method = RequestMethod.GET)
	public ModelAndView editLeave(@PathVariable("id") int id, ModelAndView mav) {
		UserInfo userInfo = userInfoService.getUserInfo();
		int leaveBalance = userInfo.getLeaveBalance();
		LeaveDetails leaveDetails = leaveManageService.getLeaveDetailsOnId(id);
		System.out.println("hi this before post fromdate " + leaveDetails.getFromDate());
		System.out.println("hi this before post todate " + leaveDetails.getToDate());
		System.out.println("hi this before post duration " + leaveDetails.getDuration());

		//v new add
		//		Date fromDate = leaveDetails.getFromDate();
		//		Date toDate = leaveDetails.getToDate();
		//
		//		// Check for null values
		//		if (fromDate == null || toDate == null) {
		//			throw new UnsupportedOperationException("FromDate or ToDate cannot be null");
		//		}
		//		LocalDate fromdate = leaveDetails.getFromDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		//		LocalDate todate = leaveDetails.getToDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		//		// Define the formatter for MM/dd/yyyy
		//		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
		//
		//		// Format the LocalDate to the desired string format
		//		String formattedFromDate = fromdate.format(formatter);
		//		String formattedToDate = todate.format(formatter);
		//		
		//		System.out.println("this is from formattedFromDate  " + formattedFromDate);
		//		System.out.println("this is from formattedToDate  " + formattedToDate);
		//		// Add the formatted dates to the ModelAndView
		//		mav.addObject("formattedFromDate", formattedFromDate);
		//		mav.addObject("formattedToDate", formattedToDate);

		//^new add        

		mav.addObject("leaveDetails", leaveDetails);
		mav.addObject("leaveBalance", leaveBalance);
		mav.setViewName("editLeave");
		return mav;
	}

	@RequestMapping(value = "/user/edit-leave/{id}", method = RequestMethod.POST)
	public ModelAndView updateLeave(@PathVariable("id") int id, @Valid LeaveDetails leaveDetails, BindingResult bindingResult, ModelAndView mav) {
		UserInfo userInfo = userInfoService.getUserInfo();
		if (bindingResult.hasErrors()) {
			mav.setViewName("editLeave");
		} else {
			LocalDate fromdate = leaveDetails.getFromDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			LocalDate todate = leaveDetails.getToDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			// Check if fromDate is after toDate
			if (fromdate.isAfter(todate)) {
				mav.addObject("NoBalanceMessage", "From date cannot be after To date. Please select the correct dates.");
				mav.setView(new RedirectView("/user/apply-leave"));
			} 
			else {
				System.out.println("hi this after post" + fromdate);
				LeaveDetails leaveDetails1 = leaveManageService.getLeaveDetailsOnId(id);
				int oldDuration = leaveDetails1.getDuration();
				int newDuration = calculateWorkingDays(fromdate, todate);
				int currentBalance = userInfo.getLeaveBalance(); 
				int newcurrentBalance = oldDuration + currentBalance;
				int newBalance = newcurrentBalance - newDuration;
				//				System.out.println("this is from edit  oldDuration " + oldDuration);
				//				System.out.println("this is from edit  newDuration " + newDuration);
				//				System.out.println("this is from edit  currentBalance " + currentBalance);
				//				System.out.println("this is from edit new balance " + newBalance);
				userInfo.setLeaveBalance(newBalance);  // Assuming setLeaveBalance() method exists;
				if(newBalance<0){
					mav.addObject("NoBalanceMessage", "Your LeaveBalance is low !"); 
					mav.setView(new RedirectView("/user/edit-leave/{id}"));
				}
				else {
					leaveManageService.updateLeave(id, leaveDetails);
					userInfoService.updateUserInfo(userInfo); // Update the user info in the database
					//					leaveManageService.editLeave(leaveDetails);
					mav.addObject("successMessage", "Your Leave Request has been updated!");
					mav.setView(new RedirectView("/user/lms"));
				}
			}
		}
		return mav;

	}


	//	@RequestMapping(value = "/user/edit-leave/{id}", method = RequestMethod.POST)
	//	public ModelAndView updateLeave(@PathVariable("id") int id,
	//	                                @RequestParam("formattedFromDate") String formattedFromDate,
	//	                                @RequestParam("formattedToDate") String formattedToDate,
	//	                                @Valid LeaveDetails leaveDetails, BindingResult bindingResult,
	//	                                ModelAndView mav) {
	//
	//	    // Ensure formattedFromDate and formattedToDate are not null or empty
	//	    if (formattedFromDate == null || formattedFromDate.isEmpty() || 
	//	        formattedToDate == null || formattedToDate.isEmpty()) {
	//	        bindingResult.reject("dateFormat", "Invalid date format.");
	//	        mav.setViewName("editLeave");
	//	        System.out.println("hi this is me 1 ");
	//	        return mav;
	//	        
	//	    }
	//
	//	    try {
	//	        Date fromDate = dateFormat.parse(formattedFromDate);
	//	        Date toDate = dateFormat.parse(formattedToDate);
	//	        System.out.println("hi this is me 2 ");
	//	        leaveDetails.setFromDate(fromDate);
	//	        leaveDetails.setToDate(toDate);
	//	    } catch (ParseException e) {
	//	    	System.out.println("hi this is me 2.1");
	//	        bindingResult.rejectValue("fromDate", "error.leaveDetails", "Invalid date format.");
	//	        bindingResult.rejectValue("toDate", "error.leaveDetails", "Invalid date format.");
	//	    }
	//
	//	    UserInfo userInfo = userInfoService.getUserInfo();
	//	    if (bindingResult.hasErrors()) {
	//	    	System.out.println("hi this is me 3 ");
	//	        mav.setViewName("editLeave");
	//	    } else {
	//	        LocalDate fromdate = leaveDetails.getFromDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
	//	        LocalDate todate = leaveDetails.getToDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
	//	        System.out.println("hi this is me 4 ");
	//	        if (fromdate.isAfter(todate)) {
	//	            mav.addObject("NoBalanceMessage", "From date cannot be after To date. Please select the correct dates.");
	//	            System.out.println("hi this is me 5 ");
	//	            mav.setViewName("editLeave");
	//	        } else {
	//	            int duration = calculateWorkingDays(fromdate, todate);
	//	            int currentBalance = userInfo.getLeaveBalance();
	//	            int newBalance = currentBalance - duration;
	//	            System.out.println("hi this is me 6 ");
	//
	//	            if (newBalance < 0) {
	//	                mav.addObject("NoBalanceMessage", "Your Leave Balance is low!");
	//	                mav.setViewName("editLeave");
	//	            } else {
	//	                leaveManageService.updateLeave(id, leaveDetails);
	//	                mav.addObject("successMessage", "Your Leave Request has been updated!");
	//	                mav.setViewName("redirect:/user/lms");
	//	            }
	//	        }
	//	    }
	//	    return mav;
	//	}



	@RequestMapping(value = "/user/cancel-leave/{id}", method = RequestMethod.GET)
	public ModelAndView cancelLeave(@PathVariable("id") int id, ModelAndView mav) {
		LeaveDetails leaveDetails = leaveManageService.getLeaveDetailsOnId(id);
		UserInfo userInfo = userInfoService.getUserInfo();

		if (leaveDetails != null && leaveDetails.isActive()) {
			int duration = leaveDetails.getDuration();
			System.out.println("Current Duration after reject is:" + duration);
			int currentBalance = userInfo.getLeaveBalance();  // Assuming getLeaveBalance() method exists
			int newBalance = currentBalance + duration;
			System.out.println("Current Balance after reject is:" + newBalance);
			userInfo.setLeaveBalance(newBalance);  // Assuming setLeaveBalance() method exists
			userInfoService.updateUserInfo(userInfo);
			leaveManageService.cancelLeave(id);
			mav.addObject("successMessage", "Your Leave Request has been canceled!");
		} else {
			mav.addObject("errorMessage", "Leave Request cannot be canceled!");
		}

		mav.setView(new RedirectView("/user/lms"));
		return mav;
	}

	@RequestMapping(value = "/user/withdraw/{id}", method = RequestMethod.GET)
	public ModelAndView withdrawLeave(@PathVariable("id") int id, ModelAndView mav) {
		LeaveDetails leaveDetails = leaveManageService.getLeaveDetailsOnId(id);
		UserInfo userInfo = userInfoService.getUserInfo();
		if (leaveDetails != null && leaveDetails.isAcceptRejectFlag()) {
			userInfoService.updateUserInfo(userInfo);
			leaveManageService.withdrawLeave(id);
			mav.addObject("successMessage", "Your Leave whitdraw has been Requested!");
		}
		mav.setView(new RedirectView("/user/lms"));
		return mav;
	}



	@RequestMapping(value = "/admin/get-all-leaves", method = RequestMethod.GET)
	public @ResponseBody List<LeaveResponse> getAllLeaves(
			@RequestParam(value = "pending", defaultValue = "false") boolean pending,
			@RequestParam(value = "accepted", defaultValue = "false") boolean accepted,
			@RequestParam(value = "rejected", defaultValue = "false") boolean rejected,
			@RequestParam(value = "cancel", defaultValue = "false") boolean cancel) throws Exception {
		List<LeaveDetails> leaves;
		if (pending || accepted || rejected || cancel ) {
			System.out.println("this is from if get all leave ");
			leaves = leaveManageService.getAllLeavesOnStatus(pending, accepted, rejected, cancel);
		} else {
			System.out.println("this is from else get all  leave ");
			leaves = leaveManageService.getAllLeaves();
		}

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Calendar calendar = Calendar.getInstance();

		return leaves.stream().map(leaveDetails -> {

			calendar.setTime(leaveDetails.getToDate());
			calendar.add(Calendar.DATE, 1);

			LeaveResponse leaveResponse = new LeaveResponse();
			leaveResponse.setTitle(leaveDetails.getEmployeeName());
			leaveResponse.setStart(dateFormat.format(leaveDetails.getFromDate()));
			leaveResponse.setEnd(dateFormat.format(calendar.getTime()));
			if (leaveDetails.isActive() && !leaveDetails.iscancel()) {
				leaveResponse.setColor("#0878af");
			} else if (!leaveDetails.isActive() && leaveDetails.isAcceptRejectFlag()) {
				leaveResponse.setColor("#0de04d");
			} else if (!leaveDetails.isActive() && !leaveDetails.isAcceptRejectFlag()) {
				leaveResponse.setColor("#e00d0d");
			}else if (leaveDetails.iscancel()) {
				leaveResponse.setColor("#ff5b00");
			}
			return leaveResponse;
		}).collect(Collectors.toList());


	}


	@RequestMapping(value = "/user/get-all-user-leaves", method = RequestMethod.GET)
	public @ResponseBody List<LeaveResponse> getUserLeaves(
			@RequestParam(value = "email", required = false) String email,
			@RequestParam(value = "pending", defaultValue = "false") boolean pending,
			@RequestParam(value = "accepted", defaultValue = "false") boolean accepted,
			@RequestParam(value = "rejected", defaultValue = "false") boolean rejected,
			@RequestParam(value = "cancel", defaultValue = "false") boolean cancel) throws Exception {
		List<LeaveDetails> leaves;
		UserInfo userInfo = userInfoService.getUserInfo();
		if (pending || accepted || rejected || cancel ) {
			System.out.println("email calander get all user leave " + userInfo.getEmail());
			leaves = leaveManageService.getUserEmailLeavesOnStatus(userInfo.getEmail(),pending, accepted, rejected, cancel);
		} else {
			System.out.println("this is from else get all user leave ");
			leaves = leaveManageService.getUserLeaves(userInfo.getEmail());
		}

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Calendar calendar = Calendar.getInstance();

		return leaves.stream().map(leaveDetails -> {

			calendar.setTime(leaveDetails.getToDate());
			calendar.add(Calendar.DATE, 1);

			LeaveResponse leaveResponse = new LeaveResponse();
			leaveResponse.setTitle(leaveDetails.getEmployeeName());
			leaveResponse.setStart(dateFormat.format(leaveDetails.getFromDate()));
			leaveResponse.setEnd(dateFormat.format(calendar.getTime()));
			if (leaveDetails.isActive() && !leaveDetails.iscancel()) {
				leaveResponse.setColor("#0878af");
			} else if (!leaveDetails.isActive() && leaveDetails.isAcceptRejectFlag()) {
				leaveResponse.setColor("#0de04d");
			} else if (!leaveDetails.isActive() && !leaveDetails.isAcceptRejectFlag()) {
				leaveResponse.setColor("#e00d0d");
			}else if (leaveDetails.iscancel()) {
				leaveResponse.setColor("#ff5b00");
			}
			return leaveResponse;
		}).collect(Collectors.toList());


	}

	@RequestMapping(value="/admin/manage-leaves", method= RequestMethod.GET)
	public ModelAndView manageLeaves(ModelAndView mav) {
		mav.addObject("leavesList", leaveManageService.getAllActiveLeaves());
		mav.addObject("withdrawList", leaveManageService.getAllWithdrawLeaves()); 
		mav.setViewName("manageLeaves");
		return mav;
	}

	@RequestMapping(value = "/admin/manage-leaves/{action}/{id}", method = RequestMethod.GET)
	public ModelAndView acceptOrRejectLeaves(ModelAndView mav, @PathVariable("action") String action,
			@PathVariable("id") int id) {
		LeaveDetails leaveDetails = leaveManageService.getLeaveDetailsOnId(id);
		//	UserInfo userInfo = userInfoService.getUserInfo();
		// Retrieve username associated with the leave application
		String username = leaveDetails.getUsername();

		// Retrieve user information based on the username
		UserInfo userInfo = userInfoService.findUserByEmail(username);
		System.out.println("Username is:" + username);

		if (action.equals("accept")) {
			leaveDetails.setAcceptRejectFlag(true);
			leaveDetails.setActive(false);
		}
		else if(action.equals("accept-withdraw")) {
			// increase the leave balance
			int duration = leaveDetails.getDuration();
			System.out.println("Current Duration after reject is:" + duration);
			int currentBalance = userInfo.getLeaveBalance();  // Assuming getLeaveBalance() method exists
			int newBalance = currentBalance + duration;
			System.out.println("Current Balance after reject is:" + newBalance);
			userInfo.setLeaveBalance(newBalance);  // Assuming setLeaveBalance() method exists
			userInfoService.updateUserInfo(userInfo);  // Update the user info in the database
			leaveDetails.setcancel(true);
			leaveDetails.setActive(true);
			leaveDetails.setAcceptRejectFlag(false);
			leaveDetails.setwithdraw(false);
		}
		else if(action.equals("reject-withdraw")) {
			leaveDetails.setwithdraw(false);
		}
		else if (action.equals("reject")) {
			// increase the leave balance
			int duration = leaveDetails.getDuration();
			System.out.println("Current Duration after reject is:" + duration);
			int currentBalance = userInfo.getLeaveBalance();  // Assuming getLeaveBalance() method exists
			int newBalance = currentBalance + duration;
			System.out.println("Current Balance after reject is:" + newBalance);
			userInfo.setLeaveBalance(newBalance);  // Assuming setLeaveBalance() method exists
			userInfoService.updateUserInfo(userInfo);  // Update the user info in the database
			leaveDetails.setAcceptRejectFlag(false);
			leaveDetails.setActive(false);
		}
		leaveManageService.updateLeaveDetails(leaveDetails);
		mav.addObject("successMessage", "Updated Successfully!");
		mav.setView(new RedirectView("/admin/manage-leaves"));
		return mav;
	}

	@RequestMapping(value = "/user/my-leaves", method = RequestMethod.GET)
	public ModelAndView showMyLeaves(ModelAndView mav) {
		UserInfo userInfo = userInfoService.getUserInfo();
		List<LeaveDetails> leavesList = leaveManageService.getAllLeavesOfUser(userInfo.getEmail());
		mav.addObject("leavesList", leavesList);
		mav.setViewName("myLeaves");
		return mav;
	}

	@GetMapping("/admin/download-leaves/{id}")
	public ResponseEntity<InputStreamResource> downloadLeaves(@PathVariable("id") int id) throws IOException {
		UserInfo userInfo = userInfoService.getUserById(id);
		if (userInfo == null) {
			return ResponseEntity.notFound().build();
		} 
		// Retrieve leave details for the given email
		List<LeaveDetails> leavesList = leaveManageService.getAllLeavesOfUser(userInfo.getEmail());

		// Generate CSV data
		ByteArrayInputStream csvData = generateCsvData(leavesList);

		// Set headers for file download
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Disposition", "attachment; filename=leave-details.csv");

		return ResponseEntity.ok()
				.headers(headers)
				.contentType(MediaType.parseMediaType("application/csv"))
				.body(new InputStreamResource(csvData));
	}

	private ByteArrayInputStream generateCsvData(List<LeaveDetails> leavesList) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		PrintWriter writer = new PrintWriter(out);

		// Write CSV header
		writer.println("Leave ID,Start Date,End Date,Reason,Type");

		// Write CSV rows
		for (LeaveDetails leave : leavesList) {
			writer.println(leave.getId() + "," + leave.getFromDate() + "," + leave.getToDate() + "," +
					leave.getReason() + "," + leave.getLeaveType());
		}

		writer.flush();
		return new ByteArrayInputStream(out.toByteArray());
	}

	
	
	
	@GetMapping("/admin/leave-Report")
	public ModelAndView showLeaveReport(
			@RequestParam(value = "name", required = false) String name,
			@RequestParam(value = "year", required = false) String year,
			ModelAndView mav) {

		// Fetch all users
		List<UserInfo> userList = userInfoService.getUsers();
		//		 Calculate the total duration for all users
		Map<String, Double> userTotalDurations = userList.stream()
				.collect(Collectors.toMap(
						UserInfo::getEmail, 
						user -> leaveManageService.getLeavesOfUserByEmailAndYear(user.getEmail(), year).stream()
						.mapToDouble(LeaveDetails::getDuration)
						.sum()
						));

		
		// Fetch leave details based on the provided name and year
		List<LeaveDetails> leaveDetails = List.of();
		if (name != null && !name.isEmpty()) {
			if (year != null) {
				leaveDetails = leaveManageService.getLeavesOfUserByNameAndYear(name, year);
			} 
			else {
				leaveDetails = leaveManageService.getLeavesOfUserByName(name);
			}
			
		}




		// Calculate the total duration
		double totalDuration = leaveDetails.stream()
				.mapToDouble(LeaveDetails::getDuration)
				.sum();

		// Calculate the total duration for all users
		mav.addObject("userTotalDurations", userTotalDurations);
		mav.addObject("users", userList);
		mav.addObject("totalDuration", totalDuration);
		mav.addObject("leaveDetails", leaveDetails);
		mav.addObject("selectedYear", year);
		mav.addObject("selectedUserName", name);
		mav.setViewName("leaveReport");
		return mav;
	}



	@GetMapping("/admin/leave-Report/download")
    @ResponseBody
    public void downloadLeaveReport(@RequestParam(value = "year", required = false) String year, HttpServletResponse response) throws IOException {
        List<UserInfo> userList = userInfoService.getUsers();
        Map<String, Double> userTotalDurations = userList.stream()
            .collect(Collectors.toMap(
                UserInfo::getEmail,
                user -> leaveManageService.getLeavesOfUserByEmailAndYear(user.getEmail(), year).stream()
                                          .mapToDouble(LeaveDetails::getDuration)
                                          .sum()
            ));

        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"leave_report "+year+".csv\"");

        PrintWriter writer = response.getWriter();
        writeLeaveReportToCsv(writer, userList, userTotalDurations);
    }

    private void writeLeaveReportToCsv(PrintWriter writer, List<UserInfo> users, Map<String, Double> userTotalDurations) {
        writer.println("Name,E-mail,Role,Total Leaves Taken");
        for (UserInfo user : users) {
            String name = user.getFirstName() + " " + user.getLastName();
            String email = user.getEmail();
            String role = user.getRole();
            double totalLeaves = userTotalDurations.getOrDefault(email, 0.0);

            writer.printf("%s,%s,%s,%.2f%n", name, email, role, totalLeaves);
        }
    }
	







	private int calculateWorkingDays(LocalDate fromdate, LocalDate todate) {
		int workingDays = 0;
		LocalDate currentDate = fromdate;

		while (!currentDate.isAfter(todate)) {
			if (currentDate.getDayOfWeek() != DayOfWeek.SATURDAY && currentDate.getDayOfWeek() != DayOfWeek.SUNDAY) {
				workingDays++;
			}
			currentDate = currentDate.plusDays(1);
		}

		return workingDays;
	}


}
