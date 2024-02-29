package com.lms.controllers;

import java.util.List;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.lms.models.EmployeeStatus;
import com.lms.models.UserInfo;
import com.lms.service.EmailService;
import com.lms.service.UserInfoService;

@Controller
public class AdminController {

	@Autowired
    private UserInfoService userInfoService;
    
    @Autowired
    private EmailService emailService; // Assuming you have an EmailService bean

    @RequestMapping(value = "/user/change-password", method = RequestMethod.GET)
    public ModelAndView changePasswordForm(ModelAndView mav) {

	mav.setViewName("changePassword");
	return mav;
    }

    @RequestMapping(value = "/user/change-password", method = RequestMethod.POST)
    public ModelAndView changePasswordSubmit(@RequestParam("currentPassword") String current_password,
	    @RequestParam("newPassword") String new_password) {
	ModelAndView mav = new ModelAndView();
	BCryptPasswordEncoder bCryptPassEncoder = new BCryptPasswordEncoder();
	String username = SecurityContextHolder.getContext().getAuthentication().getName();
	UserInfo userInfo = userInfoService.findUserByEmail(username);
	String encodedPassword = userInfo.getPassword();
	if (!bCryptPassEncoder.matches(current_password, encodedPassword)) {
	    mav.addObject("successMessage", "Current Password entered is wrong!!!");
	    mav.setView(new RedirectView("/user/change-password"));
	    return mav;
	}
	userInfo.setPassword(new_password);
	userInfoService.saveUser(userInfo);
	mav.addObject("successMessage", "Password changed Successfully!!!");
	mav.setView(new RedirectView("/user/home"));
	return mav;
    }
    
    @RequestMapping(value = "/user/manage-users", method = RequestMethod.GET)
    public ModelAndView showManageUsers(ModelAndView mav) {

	List<UserInfo> userList = userInfoService.getUsers();
	mav.addObject("users", userList);
	mav.setViewName("manageUsers");
	return mav;
    }

    @RequestMapping(value = "/user/manage-users/{action}/{id}", method = RequestMethod.GET)
    public ModelAndView manageUsers(ModelAndView mav, @PathVariable("action") String action,
	    @PathVariable("id") int id) {

    	UserInfo userInfo = null;
        if (action.equals("edit")) {
            userInfo = userInfoService.getUserById(id);
            mav.addObject("userInfo", userInfo);
            mav.setViewName("editUser");

        } else if (action.equals("delete")) {
        	sendEmployeeStatusEmail(id, EmployeeStatus.REMOVED);
        	userInfoService.deleteUser(id);
            mav.addObject("successMessage", "User removed successfully!!");
            mav.setView(new RedirectView("/user/manage-users"));
//            sendEmployeeStatusEmail(id, EmployeeStatus.REMOVED);
        } else if (action.equals("block")) {
            userInfoService.blockUser(id);
            mav.addObject("successMessage", "User blocked successfully!!");
            mav.setView(new RedirectView("/user/manage-users"));
            sendEmployeeStatusEmail(id, EmployeeStatus.BLOCKED);
        } else if (action.equals("unblock")) {
            userInfoService.unBlockUser(id);
            mav.addObject("successMessage", "User is active now!!");
            mav.setView(new RedirectView("/user/manage-users"));
            sendEmployeeStatusEmail(id, EmployeeStatus.UNBLOCKED);
        }

        return mav;
    }
    
    private void sendEmployeeStatusEmail(int userId, EmployeeStatus status) {
        String subject;
        String templateName;

        switch (status) {
            case REMOVED:
                subject = "Employee Removed";
                templateName = EmailService.EMPLOYEE_REMOVED_TEMPLATE;
                break;
            case BLOCKED:
                subject = "Employee Blocked";
                templateName = EmailService.EMPLOYEE_BLOCKED_TEMPLATE;
                break;
            case UNBLOCKED:
                subject = "Employee Active";
                templateName = EmailService.EMPLOYEE_ACTIVE_TEMPLATE;
                break;
            default:
                // Handle other cases if needed
                return;
        }

        // Fetch user email using the findUserById method
        UserInfo user = userInfoService.getUserById(userId);

        if (user != null) {
            // Get the email from the user object
            String to = user.getEmail();

            // Call the general sendEmail method from EmailService
            emailService.sendEmail(to, subject, templateName);
        } else {
            // Handle the case where user is not found
            // You can log an error, throw an exception, or handle it as appropriate for your application
        }
    }

    
    @RequestMapping(value = "/user/manage-users/save-user-edit", method = RequestMethod.POST)
    public ModelAndView saveUserEdit(ModelAndView mav, @Valid UserInfo userInfo, BindingResult bindResult) {

	/*User userExists = userService.findUserByEmail(user.getEmail());
	User updateUser = userService.getUserById(user.getId());

	if (userExists != null && !user.getEmail().equals(updateUser.getEmail())) {
	    bindResult.rejectValue("email", "error.user", "User already exists with Email id");
	}

	if (bindResult.hasErrors()) {
	    mav.addObject("errorField", bindResult.getFieldError().getField());
	    mav.addObject("errorMessage", bindResult.getFieldError().getDefaultMessage());
	    mav.setView(new RedirectView("/docs-app/admin/manage-users/edit/" + user.getId() + ""));
	} else {
	    updateUser.setName(user.getName());
	    updateUser.setLastName(user.getLastName());
	    updateUser.setEmail(user.getEmail());
	    updateUser.setPassword(user.getPassword());
	    updateUser.setRole(user.getRole());
	    userService.saveUser(updateUser);
	    mav.addObject("successMessage", "User Details updated successfully!!");
	    mav.setView(new RedirectView("/docs-app/admin/manage-users"));
	}
*/
	return mav;
    }


}
