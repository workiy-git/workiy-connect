package com.wc.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.wc.models.UserInfo;
import com.wc.service.EmailService;
import com.wc.service.LeaveManageService;
import com.wc.service.UserInfoService;

/**
 * This controller will provide the basic operations fo users. Like
 * signing-in,registering a new user.
 * 
 * 
 *
 */
@Controller
public class LoginController {

    @Autowired
    private UserInfoService userInfoService;
    
    @Autowired
    private EmailService emailService;

    @Autowired
    LeaveManageService leaveManageService;

    /**
     * This method opens up the login page if user is not authenticated
     * otherwise redirects the user to user home page.
     * 
     * @return
     */
    @RequestMapping(value = { "/", "/login" }, method = RequestMethod.GET)
    public ModelAndView login(ModelAndView mav) {

	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	UserInfo userInfo = userInfoService.findUserByEmail(auth.getName());

	mav.addObject("userInfo", userInfo);
	if (!(auth instanceof AnonymousAuthenticationToken)) {
	    mav.setViewName("dashboard");
	    return mav;
	}
	mav.setViewName("login");
	return mav;
    }

    /**
     * Opens the registration page to register a new user.
     * 
     * @return ModelAndView
     */
    @RequestMapping(value = "/admin/register", method = RequestMethod.GET)
    public ModelAndView registration(ModelAndView mav) {

	UserInfo userInfo = new UserInfo();
	mav.addObject("userInfo", userInfo);
	mav.setViewName("registration");
	return mav;
    }

    /**
     * Gets the form input from registration page and adds the user to the
     * database.
     * 
     * @param user
     * @param bindResult
     * @return ModelAndView
     */
    @RequestMapping(value = "/admin/register", method = RequestMethod.POST)
    public ModelAndView createNewUser(ModelAndView mav, @Valid UserInfo userInfo, BindingResult bindResult) {

        UserInfo userExists = userInfoService.findUserByEmail(userInfo.getEmail());

        if (userExists != null) {
            bindResult.rejectValue("email", "error.user", "User already exists with Email id");
        }

        if (bindResult.hasErrors()) {
            mav.setViewName("registration" + "?errorMessage=" + bindResult.getFieldError().getDefaultMessage() );
        } else {
        	userInfo.setCountry(" "); 
            userInfoService.saveUser(userInfo);
            mav.addObject("successMessage", "User registered successfully! ");
            mav.addObject("userInfo", new UserInfo());
            mav.setViewName("registration");

            // Send registration confirmation email
            sendRegistrationRequestEmail(userInfo.getEmail());

        }
        return mav;
    }

    private void sendRegistrationRequestEmail(String to) {
        String subject = "Employee Registered";
        String templateName = EmailService.REGISTRATION_REQUEST_TEMPLATE;

        // Call the general sendEmail method from EmailService
        emailService.sendEmail(to, subject, templateName);
    }



    
//    public ModelAndView createNewUser(ModelAndView mav, @Valid UserInfo userInfo, BindingResult bindResult) {
//
//	UserInfo userExists = userInfoService.findUserByEmail(userInfo.getEmail());
//
//	if (userExists != null) {
//	    bindResult.rejectValue("email", "error.user", "User already exists with Email id");
//	}
//
//	if (bindResult.hasErrors()) {
//	    mav.setViewName("registration");
//	} else {
//	    userInfoService.saveUser(userInfo);
//	    mav.addObject("successMessage", "User registered successfully! Awaiting for Manager approval!!");
//	    mav.addObject("userInfo", new UserInfo());
//	    mav.setViewName("registration");
//	}
//	return mav;
//    }

    /**
     * Shows the admin page after user authentication is done.
     * 
     * @param request
     * @return ModelAndView
     * @throws JSONException
     */
    @RequestMapping(value = "/user/lms", method = RequestMethod.GET)
    public ModelAndView home(ModelAndView mav, HttpServletRequest request) throws Exception {

    	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserInfo userInfo = userInfoService.findUserByEmail(auth.getName());
        request.getSession().setAttribute("userInfo", userInfo);

        mav.addObject("userInfo", userInfo);
        mav.setViewName("home");
        return mav;

    }

}
