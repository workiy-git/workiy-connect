package com.wc.controllers;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.wc.models.UserInfo;
import com.wc.service.UserInfoService;

//import org.springframework.web.bind.annotation.ModelAttribute;
//import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ProfileController {
	@Autowired
	private UserInfoService userInfoService;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@RequestMapping(value = "/user/Profile", method = RequestMethod.GET)
	public ModelAndView manageUsers1(ModelAndView mav) {
		UserInfo userInfo = userInfoService.getUserInfo();
		mav.addObject("userProfile", userInfo);
		mav.setViewName("profile");
		return mav;
	}

	@RequestMapping(value = "/user/editProfile", method = RequestMethod.GET)
	public ModelAndView manageUsers(ModelAndView mav) {
		UserInfo userInfo = userInfoService.getUserInfo();
		mav.addObject("userProfile", userInfo);
		mav.addObject("countries", getCountryList());
		mav.addObject("bloodGroup",getBloodGroupList());
		mav.addObject("states", getStatesList(userInfo.getCountry())); // Load states based on the country
		mav.setViewName("editProfile");
		return mav;
	}
	
	@RequestMapping(value = "/updateProfile", method = RequestMethod.POST)
	public ModelAndView manageUsers(ModelAndView mav, @Valid @ModelAttribute("userProfile") UserInfo userProfile,@RequestParam("profilePicture") MultipartFile profilePicture) {
		UserInfo updateProfile = userInfoService.findUserByEmail(userProfile.getEmail());
			System.out.println("hi this is profile" + userProfile.getEmail());
		if (updateProfile != null) {
			System.out.println("this is from POST profile");
			updateProfile.setFirstName(userProfile.getFirstName());
			updateProfile.setLastName(userProfile.getLastName());
			updateProfile.setBloodGroup(userProfile.getBloodGroup());
			updateProfile.setPhoneNumber(userProfile.getPhoneNumber());
			updateProfile.setEmergencyPhoneNumber(userProfile.getEmergencyPhoneNumber());
			updateProfile.setDateOfBirth(userProfile.getDateOfBirth());
			updateProfile.setRole(userProfile.getRole());
			updateProfile.setEmail(userProfile.getEmail());
			updateProfile.setPassword(bCryptPasswordEncoder.encode(userProfile.getPassword()));
			updateProfile.setTemporaryAddress(userProfile.getTemporaryAddress());
			updateProfile.setAddress(userProfile.getAddress());
			updateProfile.setCity(userProfile.getCity());
			updateProfile.setCountry(userProfile.getCountry());
			updateProfile.setState(userProfile.getState());
			updateProfile.setZip(userProfile.getZip());
		if (profilePicture != null && !profilePicture.isEmpty()) {
            try {
            	System.out.println("this is from try profile");
                updateProfile.setProfilePicture(profilePicture.getBytes());
                updateProfile.setProfilePictureType(profilePicture.getContentType());
            } catch (IOException e) {
            	System.out.println("this is from catch profile");
                mav.addObject("errorMessage", "Failed to upload profile picture");
                mav.setView(new RedirectView("/profile"));
                return mav;
            }
        }
		System.out.println("this is from UpdateUser profile");
		userInfoService.UpdateUser(updateProfile);
		mav.setView(new RedirectView("/user/lms"));
		mav.addObject("successMessage", "User Details updated successfully!!");
		}
		else {
			mav.addObject("errorMessage", "User not found");
			mav.setView(new RedirectView("/profile"));
		}
		
		return mav;
	}
	
	
//	 @PostMapping("/uploadProfilePicture")
//	    public ResponseEntity<String> uploadProfilePicture(@RequestParam("profilePicture") MultipartFile file, @Valid UserInfo userInfo) {
//	        try {
//	            UserInfo userInfo1 = userInfoService.findUserByEmail(userInfo.getEmail());
//	            if (userInfo1 != null) {
//	                userInfo1.setProfilePicture(file.getBytes());
//	                userInfo1.setProfilePictureType(file.getContentType());
//	                userInfoService.updateUserInfo(userInfo1);
//	                return ResponseEntity.ok("Profile picture uploaded successfully");
//	            } else {
//	                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
//	            }
//	        } catch (Exception e) {
//	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while uploading the profile picture");
//	        }
//	    }
//	
//	@RequestMapping(value = "/profilePicture", method = RequestMethod.GET)
//	public ResponseEntity<byte[]> getProfilePictureByEmail(@RequestParam("email") String email) {
//	    UserInfo userInfo = userInfoService.findUserByEmail(email);
//	    if (userInfo != null && userInfo.getProfilePicture() != null) {
//	    	 String imageType = userInfo.getProfilePictureType(); // Assuming you have a method to get image type
//	    	 MediaType mediaType = getMediaTypeForImageType(imageType);
//	        HttpHeaders headers = new HttpHeaders();
//	        headers.setContentType(mediaType);
//	        return new ResponseEntity<>(userInfo.getProfilePicture(), headers, HttpStatus.OK);
//	    } else {
//	        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//	    }
//	}
	 
		@RequestMapping(value = "/profilePicture", method = RequestMethod.GET)
		public ResponseEntity<byte[]> getProfilePictureByEmail( @RequestParam("email") @Valid String email) {
		    UserInfo userInfo1 = userInfoService.findUserByEmail(email);
		    if (userInfo1 != null && userInfo1.getProfilePicture() != null) {
		    	 String imageType = userInfo1.getProfilePictureType(); // Assuming you have a method to get image type
		    	 MediaType mediaType = getMediaTypeForImageType(imageType);
		        HttpHeaders headers = new HttpHeaders();
		        headers.setContentType(mediaType);
		        return new ResponseEntity<>(userInfo1.getProfilePicture(), headers, HttpStatus.OK);
		    } else {
		        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		    }
		}

	@RequestMapping(value = "/getStates", method = RequestMethod.GET)
	public @ResponseBody List<String> getStates(@RequestParam("country") String country) {
		return getStatesList(country);
	}
	private List<String>getCountryList(){
		return Arrays.asList("Canada","India","Qatar","United States");    
	}
	private List<String> getStatesList(String country) {
		switch (country) {
		case "India":
			return getStatesList_India();
		case "United States":
			return getStatesList_United_States();
		case "Canada":
			return getStatesList_Canada();
		case "Qatar":
			return getStatesList_Qatar();
		default:
			return Arrays.asList("State list not available");
		}
	}

	private List<String> getStatesList_India() {
		// Return list of states
		return Arrays.asList("Andhra Pradesh", "Arunachal Pradesh", "Assam", "Bihar", "Chhattisgarh", "Goa", "Gujarat", "Haryana", "Himachal Pradesh", "Jammu and Kashmir", "Jharkhand", "Karnataka", "Kerala", "Madhya Pradesh", "Maharashtra", "Manipur", "Meghalaya", "Mizoram", "Nagaland", "Odisha", "Punjab", "Rajasthan", "Sikkim", "Tamil Nadu", "Telangana", "Tripura", "Uttar Pradesh", "Uttarakhand", "West Bengal");    
	}
	private List<String> getStatesList_United_States(){
		return Arrays.asList("Alabama", "Alaska", "Arizona", "Arkansas", "California", "Colorado", "Connecticut", "Delaware", "District of Columbia", "Florida", "Georgia", "Hawaii", "Idaho", "Illinois", "Indiana", "Iowa", "Kansas", "Kentucky", "Louisiana", "Maine", "Montana", "Nebraska", "Nevada", "New Hampshire", "New Jersey", "New Mexico", "New York", "North Carolina", "North Dakota", "Ohio", "Oklahoma", "Oregon", "Maryland", "Massachusetts", "Michigan", "Minnesota", "Mississippi", "Missouri", "Pennsylvania", "Rhode Island", "South Carolina", "South Dakota", "Tennessee", "Texas", "Utah", "Vermont", "Virginia", "Washington", "West Virginia", "Wisconsin", "Wyoming");
	}
	private List<String> getStatesList_Canada() {
		return Arrays.asList("Alberta", "British Columbia", "Manitoba", "New Brunswick", "Newfoundland and Labrador", "Northwest Territories", "Nova Scotia", "Nunavut", "Ontario", "Prince Edward Island", "Quebec", "Saskatchewan", "Yukon");
	}
	private List<String> getStatesList_Qatar() {
		return Arrays.asList("Al Shamal", "Al Khor", "Al-Shahaniya", "Umm Salal", "Al Daayen", "Doha (Ad Dawhah)", "Al Rayyan", "Al Wakra");
	}
	
	private List<String> getBloodGroupList(){
		return Arrays.asList("A+","A-","B+","B-","AB+","AB-","O+","O-");
	}
	
	 private MediaType getMediaTypeForImageType(String imageType) {
	        if ("image/jpeg".equalsIgnoreCase(imageType) || "image/jpg".equalsIgnoreCase(imageType)) {
	            return MediaType.IMAGE_JPEG;
	        } else if ("image/png".equalsIgnoreCase(imageType)) {
	            return MediaType.IMAGE_PNG;
	        } else if ("image/gif".equalsIgnoreCase(imageType)) {
	            return MediaType.IMAGE_GIF;
	        } else {
	            return MediaType.IMAGE_JPEG; // Default
	        }
	    }
}