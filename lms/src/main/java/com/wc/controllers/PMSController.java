package com.wc.controllers;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.List;

//import javax.servlet.http.HttpServletRequest;
//import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
//import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.wc.forms.RatingForm;
import com.wc.models.UserInfo;
import com.wc.service.PerformanceManageService;
import com.wc.service.UserInfoService;

@Controller
public class PMSController {
	
	@Autowired
    private UserInfoService userInfoService;

    @Autowired
    private PerformanceManageService ratingService; // Assuming you have a service to handle ratings
    
    @RequestMapping(value = "/user/pms", method = RequestMethod.GET)
    public ModelAndView performanceManagement(ModelAndView mav) {
        // Assuming you have a Thymeleaf template named pms.html
        mav.setViewName("pms");
        return mav;
    }
    
    @RequestMapping(value = "/user/pms/rate-employees", method = RequestMethod.GET)
    public ModelAndView rateEmployees(ModelAndView mav) {
        // Assuming you have a Thymeleaf template named rate-employees.html
    	List<UserInfo> userList = userInfoService.getUsers();
    	mav.addObject("users", userList);
        mav.setViewName("rateEmployee");
        return mav;
    }
    
    @RequestMapping(value = "/user/pms/rate-employees", method = RequestMethod.POST)
    public String saveRating(@ModelAttribute("ratingForm") RatingForm ratingForm,
            RedirectAttributes redirectAttributes) {
        // Save the rating to the database using your service
        ratingService.saveRating(ratingForm);

     // Redirect to /user/pms/rate-employees with a success message
        redirectAttributes.addAttribute("success", "Rating submitted successfully");
        return "redirect:/user/pms/rate-employees";
    }
    
    @RequestMapping(value = "/user/pms/rate/{id}", method = RequestMethod.GET)
    public ModelAndView ratingPage(ModelAndView mav, @PathVariable("id") int userId,
                                   @RequestParam(name = "success", required = false) String success, Model model) {
        mav.setViewName("ratingPage");
        
     // Calculate the current year
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);

        // Add the current year as a model attribute
        model.addAttribute("currentYear", currentYear);
        model.addAttribute("currentMonthValue", LocalDate.now().getMonthValue());

        // Assuming you have a UserInfo object based on the employeeId
        UserInfo userInfo = userInfoService.getUserById(userId);

        // Create a new RatingForm and set the employeeId
        RatingForm ratingForm = new RatingForm();
        ratingForm.setUserId(userInfo.getId());

        // Add the RatingForm to the model
        mav.addObject("ratingForm", ratingForm);

        // Add success message to the model if present
        if (success != null) {
            mav.addObject("successMessage", success);
        }

        return mav;
    }

    @RequestMapping(value = "/user/pms/rate/{id}", method = RequestMethod.POST)
    public String saveRating(@ModelAttribute("ratingForm") RatingForm ratingForm) {
        // Save the rating to the database using your service
        ratingService.saveRating(ratingForm);

     // Redirect to /user/pms/rate-employees with a success message
//        redirectAttributes.addAttribute("success", "Rating submitted successfully");
        return "redirect:/user/pms/rate-employees";
    }
    
    @RequestMapping(value = "/user/pms/list-ratings", method = RequestMethod.GET)
    public ModelAndView listRatings(ModelAndView mav, @AuthenticationPrincipal UserDetails userDetails, Model model) {
        // Get the user's ID from the authenticated user details
        String userId = userDetails.getUsername(); // Assuming the username is the user ID
        
     // Calculate the current year
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);

        // Add the current year as a model attribute
        model.addAttribute("currentYear", currentYear);
        model.addAttribute("currentMonthValue", LocalDate.now().getMonthValue());
        
        // Get the user's ratings from the database using your service
        List<RatingForm> userRatings = ratingService.getUserRatings(userId);

        mav.addObject("userRatings", userRatings);
        mav.setViewName("listRatings");
        return mav;
    }
    
    @PostMapping("/user/pms/view-rating")
    public String yourMethod(@RequestParam("year") int year, @RequestParam("month") String month, Model model) {
        // Your method logic using year and month

        // Add necessary attributes to the model if needed
        model.addAttribute("year", year);
        model.addAttribute("month", month);

        return "viewRating";
    }
    
    @GetMapping("/user/pms/view-rating")
    public String viewRating(Model model, @AuthenticationPrincipal UserDetails userDetails) {
    	
    	// Get the user's ID from the authenticated user details
        String userId = userDetails.getUsername(); // Assuming the username is the user ID
        // Assume you have a service class to handle database operations
        List<RatingForm> ratings = ratingService.getUserRatings(userId);
        
        // Add the ratings to the model
        model.addAttribute("ratings", ratings);
        model.addAttribute("userId", userId);

        return "viewRating";
    }
    
//    @GetMapping("/user/pms/rate/{id}")
//    public String yourMethod(Model model) {
//        // Calculate the current year
//        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
//
//        // Add the current year as a model attribute
//        model.addAttribute("currentYear", currentYear);
//        model.addAttribute("currentMonthValue", LocalDate.now().getMonthValue());
//
//        // Your existing code
//
//        return "ratingPage"; // Make sure to return the correct template name
//    }
    
//    @GetMapping("/user/pms/list-ratings")
//    public String yourMethod1(Model model) {
//        // Calculate the current year
//        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
//
//        // Add the current year as a model attribute
//        model.addAttribute("currentYear", currentYear);
//        model.addAttribute("currentMonthValue", LocalDate.now().getMonthValue());
//
//        // Your existing code
//
//        return "listRatings"; // Make sure to return the correct template name
//    }
}