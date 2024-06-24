package com.wc.controllers;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
//import java.util.Objects;
import java.util.UUID;

//import javax.servlet.http.HttpServletRequest;
//import javax.validation.Valid;

//import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.authentication.AnonymousAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
//import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.servlet.ModelAndView;
//import org.springframework.security.authentication.AnonymousAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.web.servlet.ModelAndView;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wc.models.UserInfo;
import com.wc.repository.UserInfoRepository;
import com.wc.service.EmailService;

/**
 * This controller will provide the basic operations fo users. Like
 * signing-in,registering a new user.
 * 
 * 
 *
 */
@Controller
@RequestMapping("/password-reset")
public class PasswordResetController {

//	@Autowired
//    private UserInfoService userInfoService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserInfoRepository userInfoRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @GetMapping("/request")
    public String showRequestForm() {
        return "passwordResetRequest";
    }
    
    private static final Logger log = LoggerFactory.getLogger(PasswordResetController.class);

    @PostMapping("/request")
    public String processRequestForm(@RequestParam("email") String email, RedirectAttributes redirectAttributes) {
        boolean success = requestPasswordReset(email);

        if (success) {
            // Display a success message on the login page
            redirectAttributes.addFlashAttribute("successMessage", "A password reset link has been sent to your email.");
        } else {
            // Display an error message on the login page
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to process the password reset request.");
        }

        // Redirect to the login page
        return "redirect:/login";
    }

    // ... (other methods)

    private boolean requestPasswordReset(String userEmail) {
        UserInfo user = userInfoRepository.findByEmail(userEmail);

        if (user != null) {
            // Generate a reset token
            String resetToken = UUID.randomUUID().toString();
            user.setResetToken(resetToken);

            // Save the user with the reset token
            userInfoRepository.save(user);

            // Send an email with the reset token
            return sendResetPasswordEmail(user.getEmail(), resetToken);
        }

        return false;
    }

    private boolean sendResetPasswordEmail(String userEmail, String resetToken) {
        try {
            // Generate the reset link using the reset token
            String resetLink = "http://localhost:8080/password-reset/reset?token=" + resetToken;

            // Retrieve the user from the database
            UserInfo user = userInfoRepository.findByEmail(userEmail);

            if (user != null) {
                // Save the reset token in the user's record (if not already saved)
                user.setResetToken(resetToken);
                userInfoRepository.save(user);

                // Construct the email content with the reset link
                String subject = "Password Reset";
                String emailContent = "Click the link below to reset your password: <a href='" + resetLink + "'>Reset Password</a>";
                String to = userEmail;

                // Send the email
                emailService.sendPasswordResetEmail(to, subject, emailContent);

                return true;  // Email sent successfully
            } else {
                log.error("User not found for email: {}", userEmail);
                return false; // User not found
            }
        } catch (Exception e) {
            // Log the exception or handle it as needed
            log.error("Error sending password reset email to {}", userEmail, e);
            return false; // Email sending failed
        }
    }
    
    @GetMapping("/reset")
    public String showResetPasswordPage(@RequestParam("token") String resetToken, Model model) {
        // Add logic to check if the resetToken is valid
        // For example, verify it's not expired and matches a user

        if (isValidResetToken(resetToken)) {
            model.addAttribute("resetToken", resetToken);
            return "passwordReset";
        } else {
            // Handle invalid token, redirect to an error page, or display an error message
            return "redirect:/error";
        }
    }

    @PostMapping("/reset")
    public String processResetPasswordForm(
            @RequestParam("token") String resetToken,
            @RequestParam("newPassword") String password,
            @RequestParam("confirmPassword") String confirmPassword,
            RedirectAttributes redirectAttributes
    ) {
        // For example, verify it's not expired and matches a user

        if (isValidResetToken(resetToken)) {
            if (password.equals(confirmPassword)) {
                // Retrieve the user from the database
                UserInfo user = userInfoRepository.findByResetToken(resetToken);

                if (user != null) {
                    // Update the user's password
                    user.setPassword(passwordEncoder.encode(password));
                    user.setResetToken(null); // Reset the reset token

                    // Save the updated user
                    userInfoRepository.save(user);

                    // Redirect to the login page with a success message
                    redirectAttributes.addFlashAttribute("successMessage", "Your password has been successfully reset. Please log in with your new password.");
                    return "redirect:/login";
                } else {
                    // Handle user not found
                    redirectAttributes.addFlashAttribute("errorMessage", "User not found.");
                }
            } else {
                // Handle password mismatch
                redirectAttributes.addFlashAttribute("errorMessage", "Passwords do not match.");
            }
        } else {
            // Handle invalid token
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid reset token.");
        }

        // Redirect to the error page or reset password page with an error message
        return "redirect:/error";
    }

    private boolean isValidResetToken(String resetToken) {
        // Retrieve the user based on the reset token
        UserInfo user = userInfoRepository.findByResetToken(resetToken);

        // Check if the user exists and the token is not expired
        if (user != null && isTokenValidForUser(resetToken, user)) {
            return true;
        }

        return false;
    }
    
    private boolean isTokenValidForUser(String resetToken, UserInfo user) {
        // Implement your token validation logic here
        // For example, check if the token matches the one stored for the user
        return resetToken.equals(user.getResetToken()) && !isTokenExpired(user);
    }
    
    private boolean isTokenExpired(UserInfo user) {
        // Implement logic to check if the reset token is expired
        // For example, compare the current date with the token creation date
        // and determine if it's within a valid timeframe

        // Here's a placeholder implementation that assumes a 1-hour expiration
        LocalDateTime tokenCreationTime = user.getResetTokenCreationTime();
        LocalDateTime currentDateTime = LocalDateTime.now();

        return ChronoUnit.HOURS.between(tokenCreationTime, currentDateTime) > 1;
    }

}
